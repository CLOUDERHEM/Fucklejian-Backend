package com.lc.legym.global;


import com.lc.legym.util.ResultData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

import static com.lc.legym.enums.Constant.MAX_MSG_LENGTH;

/**
 * @author Aaron Yeung
 * @date 4/8/2022 6:54 PM
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(Exception.class)
    public ResultData<String> handleException(Exception ex) {

        log.error("", ex);

        String msg;
        if (ex.getMessage().length() <= MAX_MSG_LENGTH) {
            msg = ex.getMessage();
        } else {
            msg = ex.getMessage().substring(0, MAX_MSG_LENGTH) + "...";
        }

        return ResultData.error(msg);
    }

    @ExceptionHandler(SQLException.class)
    public ResultData<String> handleSqlException(SQLException ex) {

        log.error("", ex);

        return ResultData.error("数据库错误!");
    }


    @ExceptionHandler(BindException.class)
    public ResultData<?> validationErrorHandler(BindException ex) {
        List<String> collect = ex.getBindingResult().getAllErrors()
                .stream()
                .map(ObjectError::getDefaultMessage)
                .collect(Collectors.toList());
        StringBuilder sb = new StringBuilder();
        collect.forEach(sb::append);

        // log.error("", ex);

        return ResultData.builder()
                .code(1)
                .msg(sb.toString()).build();
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResultData<?> validationErrorHandler(MethodArgumentNotValidException ex) {
        List<String> collect = ex.getBindingResult().getAllErrors()
                .stream()
                .map(ObjectError::getDefaultMessage)
                .collect(Collectors.toList());
        StringBuilder sb = new StringBuilder();
        collect.forEach(e -> sb.append(e).append(" "));

        // log.error("", ex);

        return ResultData.builder()
                .code(1)
                .msg(sb.toString()).build();
    }


    @ExceptionHandler(ConstraintViolationException.class)
    public ResultData<?> validationErrorHandler(ConstraintViolationException ex) {
        List<String> errorInformation = ex.getConstraintViolations()
                .stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toList());
        StringBuilder sb = new StringBuilder();
        errorInformation.forEach(sb::append);

        // log.error("", ex);

        return ResultData.builder()
                .code(1)
                .msg(sb.toString()).build();
    }

}