package com.lc.legym.controller;

import com.lc.legym.model.vo.RequestVO;
import com.lc.legym.service.EntryService;
import com.lc.legym.util.ResultData;
import com.lc.legym.util.ThreadLocalUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.ExecutionException;


/**
 * @author Aaron Yeung
 * @date 9/4/2022 9:15 PM
 */
@Slf4j
@RestController
public class OneController {

    private static final String AK = "lc1010lc1010";

    private EntryService entryService;

    @Autowired
    public void setEntryService(EntryService entryService) {
        this.entryService = entryService;
    }

    @PostMapping("/api/upload")
    public ResultData<?> upload(@RequestBody @Validated RequestVO requestVO, @RequestParam String ak, HttpServletRequest request) {
        log.info("{}", requestVO);
        if (!AK.equals(ak)) {
            return ResultData.error("无效ak");
        }
        ThreadLocalUtils.set(request.getHeader("X-Real-IP"));
        return entryService.run(requestVO);
    }

    @GetMapping("/api/result")
    public ResultData<?> result(@RequestParam String id, @RequestParam String ak) throws ExecutionException, InterruptedException {
        if (!AK.equals(ak)) {
            return ResultData.error("无效ak");
        }
        return entryService.query(id);
    }

}
