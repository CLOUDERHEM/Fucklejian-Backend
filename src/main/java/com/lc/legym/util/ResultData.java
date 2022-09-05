package com.lc.legym.util;


import lombok.Builder;
import lombok.Data;

/**
 * @author Aaron Yeung
 * @date 1/3/2022 3:49 PM
 */
@Builder
@Data
public class ResultData<T> {


    private Integer code;
    private String msg;
    private T data;

    public static <T> ResultData<T> success(Integer code, String msg, T data) {
        return new ResultData<>(code, msg, data);
    }

    public static <T> ResultData<T> success(String msg, T data) {
        return new ResultData<>(0, msg, data);
    }

    public static <T> ResultData<T> success(String msg) {
        return new ResultData<>(0, msg, null);
    }


    public static <T> ResultData<T> error(Integer code, String msg, T data) {
        return new ResultData<>(code, msg, data);
    }

    public static <T> ResultData<T> error(String msg, T data) {
        return new ResultData<>(1, msg, data);
    }

    public static <T> ResultData<T> error(String msg) {
        return new ResultData<>(1, msg, null);
    }

    public static <T> ResultData<T> result(Integer code, String msg, T data) {
        return new ResultData<>(code, msg, data);
    }

    public ResultData(Integer code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }
}
