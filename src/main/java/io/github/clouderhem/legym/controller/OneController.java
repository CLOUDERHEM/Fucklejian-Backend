package io.github.clouderhem.legym.controller;

import io.github.clouderhem.legym.model.vo.RequestVO;
import io.github.clouderhem.legym.service.EntryService;
import io.github.clouderhem.legym.util.ResultData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
    public ResultData<?> upload(@RequestBody @Validated RequestVO requestVO, HttpServletRequest request) {
        log.info("{}", requestVO);
        return entryService.run(requestVO);
    }

    @GetMapping("/running/query")
    public ResultData<?> result(@RequestParam String id) throws ExecutionException, InterruptedException {
        return entryService.query(id);
    }

}
