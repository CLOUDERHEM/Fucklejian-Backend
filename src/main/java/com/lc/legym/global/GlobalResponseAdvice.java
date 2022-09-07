package com.lc.legym.global;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lc.legym.util.ResultData;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.util.LinkedHashMap;
import java.util.Map;

import static com.lc.legym.enums.Constant.MAX_MSG_LENGTH;

/**
 * @author Aaron Yeung
 * @date 4/14/2022 10:30 PM
 */
@Slf4j
@RestControllerAdvice
public class GlobalResponseAdvice implements ResponseBodyAdvice<Object> {

    private ObjectMapper objectMapper;

    @Autowired
    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean supports(@NotNull MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {

        if (body instanceof ResultData) {
            return body;
        } else if (body instanceof String) {
            try {
                return objectMapper.writeValueAsString(ResultData.success(null, body));
            } catch (JsonProcessingException ex) {
                log.info("", ex);
            }
        } else if (body instanceof LinkedHashMap<?, ?>) {

            Map<?, ?> map = (Map<?, ?>) body;
            String msg = null;
            String tmp = (String) map.get("error");
            if (StringUtils.hasText(tmp)) {
                if (tmp.length() > MAX_MSG_LENGTH) {
                    msg = tmp.substring(0, MAX_MSG_LENGTH);
                } else {
                    msg = tmp;
                }
            }
            return ResultData.result((Integer) map.get("status"), msg, null);
        }

        return ResultData.success(null, body);
    }
}
