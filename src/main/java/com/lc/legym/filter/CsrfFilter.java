package com.lc.legym.filter;

import com.alibaba.fastjson.JSON;
import com.lc.legym.util.EncryptUtils;
import com.lc.legym.util.ResultData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

/**
 * @author Aaron Yeung
 * @date 9/7/2022 8:35 AM
 */
@Slf4j
@Component
public class CsrfFilter extends OncePerRequestFilter {

    private static final String SALT = "8292af46b29830b1d7b9275f8233b0594f67d6e5";
    private static final int SHA1_LENGTH = 40;
    /**
     * timestamp/100
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

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        if (!check(request)) {
            String res = JSON.toJSONString(ResultData.error("forbidden", System.currentTimeMillis()));
            response.getWriter().write(res);
            response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }
        filterChain.doFilter(request, response);
    }

    public boolean check(HttpServletRequest request) {
        String ak = request.getParameter("ak");
        String akHeader = request.getHeader(TOKEN_AK);
        String ga = request.getHeader(TOKEN_GA);
        String he = request.getHeader(TOKEN_HE);
        String pathname = request.getRequestURI();
        return isAkValid(ak, akHeader) && isGaValid(ga) && isPathValid(pathname, he);

    }

    public boolean isAkValid(String ak, String akHeader) {
        if (!StringUtils.hasText(akHeader)) {
            return false;
        }
        if (akHeader.length() != SHA1_LENGTH) {
            return false;
        }
        String sha1 = EncryptUtils.getSha1((ak == null ? "" : ak) + SALT);
        return Objects.equals(akHeader, sha1);
    }

    public boolean isGaValid(String ga) {

        if (!StringUtils.hasText(ga)) {
            return false;
        }
        if (ga.length() != SHA1_LENGTH) {
            return false;
        }

        long time = System.currentTimeMillis() / 1000;
        for (long i = time - 1; i <= time + 1; i++) {
            if (Objects.equals(EncryptUtils.getSha1(i + SALT), ga)) {
                return true;
            }
        }

        return false;
    }

    public boolean isPathValid(String pathname, String he) {
        String sha1 = EncryptUtils.getSha1(pathname + SALT);
        return Objects.equals(he, sha1);
    }

}
