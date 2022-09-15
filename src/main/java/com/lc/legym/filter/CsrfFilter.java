package com.lc.legym.filter;

import com.alibaba.fastjson.JSON;
import com.lc.legym.model.vo.TimeVO;
import com.lc.legym.util.EncryptUtils;
import com.lc.legym.util.RateLimitUtils;
import com.lc.legym.util.ResultData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import static com.lc.legym.enums.Constant.*;

/**
 * @author Aaron Yeung
 * @date 9/7/2022 8:35 AM
 */
@Slf4j
@Component
public class CsrfFilter extends OncePerRequestFilter {


    private static final int SHA1_LENGTH = 40;
    /**
     * timestamp/1000
     */
    private static final String TOKEN_GA = "x-lc-ga";
    /**
     * ak
     */
    private static final String TOKEN_AK = "x-lc-ak";
    /**
     * uri
     */
    private static final String TOKEN_HE = "x-lc-he";

    /**
     * timestamp
     */
    private static final String TOKEN_TIMESTAMP = "x-lc-timestamp";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String remoteAddr = request.getRemoteAddr();
        if (!RateLimitUtils.tryAcquire(remoteAddr)) {
            String res = JSON.toJSONString(ResultData.error("频繁访问", System.currentTimeMillis()));
            writeToResp(res, response);
            return;
        }

        String timestamp = request.getHeader(TOKEN_TIMESTAMP);
        TimeVO timeValid = isTimeValid(timestamp);
        if (StringUtils.hasText(timestamp) && timeValid != null) {
            String res = JSON.toJSONString(ResultData.error(
                    "与服务器时间不一致, 请校准手机/电脑时间!",
                    timeValid));
            writeToResp(res, response);
            return;
        }

        if (!check(request)) {
            String res = JSON.toJSONString(ResultData.error("Forbidden", System.currentTimeMillis()));
            writeToResp(res, response);
            return;
        }

        filterChain.doFilter(request, response);
    }

    private boolean check(HttpServletRequest request) {
        String ak = request.getParameter(AK_NAME);
        String akHeader = request.getHeader(TOKEN_AK);
        String ga = request.getHeader(TOKEN_GA);
        String he = request.getHeader(TOKEN_HE);
        String pathname = request.getRequestURI();
        return isAkValid(ak, akHeader) && isGaValid(ga) && isPathValid(pathname, he);

    }

    private TimeVO isTimeValid(String timestamp) {
        long curInSec = System.currentTimeMillis() / 1000;
        if (!StringUtils.hasText(timestamp)) {
            return new TimeVO(0L, curInSec);
        }
        try {
            long clientTimeInSec = Long.parseLong(timestamp) / 1000;
            if (Math.abs(curInSec - clientTimeInSec) > TIMESTAMP_PER) {
                return new TimeVO(clientTimeInSec, curInSec);
            }
        } catch (Exception e) {
            return new TimeVO(0L, curInSec);
        }

        return null;
    }

    private boolean isAkValid(String ak, String akHeader) {
        if (!StringUtils.hasText(akHeader)) {
            return false;
        }
        if (akHeader.length() != SHA1_LENGTH) {
            return false;
        }
        String sha1 = EncryptUtils.getSha1((ak == null ? "" : ak) + SALT);
        return Objects.equals(akHeader, sha1);
    }

    private boolean isGaValid(String ga) {

        if (!StringUtils.hasText(ga)) {
            return false;
        }
        if (ga.length() != SHA1_LENGTH) {
            return false;
        }

        long time = System.currentTimeMillis() / 1000;
        for (long i = time - TIMESTAMP_PER; i <= time + TIMESTAMP_PER; i++) {
            if (Objects.equals(EncryptUtils.getSha1(i + SALT), ga)) {
                return true;
            }
        }

        return false;
    }

    private boolean isPathValid(String pathname, String he) {
        String sha1 = EncryptUtils.getSha1(pathname + SALT);
        return Objects.equals(he, sha1);
    }

    @SuppressWarnings("all")
    private static void writeToResp(String res, HttpServletResponse response) throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
        response.setStatus(HttpServletResponse.SC_OK);
        ServletOutputStream outputStream = response.getOutputStream();
        outputStream.write(res.getBytes(StandardCharsets.UTF_8));
        outputStream.flush();
        outputStream.close();
    }

}
