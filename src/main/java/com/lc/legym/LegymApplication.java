package com.lc.legym;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.util.TimeZone;

/**
 * @author Aaron Yeung
 */
@SpringBootApplication
public class LegymApplication {

    public static void main(String[] args) throws IOException {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Shanghai"));
        SpringApplication.run(LegymApplication.class, args);
        Runtime.getRuntime().exec("explorer http://127.0.0.1");
    }

}
