package com.lc.legym.controller;

import com.lc.legym.enums.Constant;
import com.lc.legym.model.vo.RequestVO;
import com.lc.legym.service.EntryService;
import com.lc.legym.util.ResultData;
import com.lc.legym.util.ThreadLocalUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.ExecutionException;


/**
 * @author Aaron Yeung
 * @date 9/4/2022 9:15 PM
 */
@Slf4j
@RequestMapping("/api")
@RestController
public class OneController {

    private EntryService entryService;

    @Autowired
    public void setEntryService(EntryService entryService) {
        this.entryService = entryService;
    }

    @PostMapping("/running/upload")
    public ResultData<?> upload(@RequestBody @Validated RequestVO requestVO, @RequestParam String ak, HttpServletRequest request) {
        log.info("{}", requestVO);
        StringBuilder ip = new StringBuilder();
        String remoteAddr = request.getHeader(Constant.REMOTE_ADD_NAME);
        if (StringUtils.hasText(remoteAddr)) {
            ip.append("Real-IP: ").append(remoteAddr);
        }
        String xForwardedFor = request.getHeader(Constant.X_FORWARDED_IP);
        if (StringUtils.hasText(xForwardedFor)) {
            ip.append("X-Forwarded-For: ").append(xForwardedFor);
        }

        ThreadLocalUtils.set(Constant.REMOTE_ADD_NAME, ip.toString());
        ThreadLocalUtils.set(Constant.AK_NAME, ak);
        return entryService.run(requestVO, ak);
    }

    @GetMapping("/running/query")
    public ResultData<?> result(@RequestParam String id, @RequestParam String ak) throws ExecutionException, InterruptedException {
        return entryService.query(id, ak);
    }

}
