package com.lc.legym.controller;

import com.lc.legym.model.NoticeDO;
import com.lc.legym.util.ResultData;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Aaron Yeung
 * @date 9/8/2022 9:28 PM
 */
@RestController
@RequestMapping("/api")
public class NoticeController {

    @GetMapping("/notice")
    public ResultData<NoticeDO> notice() {
        NoticeDO noticeDO = new NoticeDO();
        noticeDO.setShow(0);
        return ResultData.success(null, noticeDO);
    }
}
