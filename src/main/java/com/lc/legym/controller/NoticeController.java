package com.lc.legym.controller;

import com.lc.legym.mapper.NoticeMapper;
import com.lc.legym.model.NoticeDO;
import com.lc.legym.util.ResultData;
import org.springframework.beans.factory.annotation.Autowired;
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

    private NoticeMapper noticeMapper;

    @Autowired
    public void setNoticeMapper(NoticeMapper noticeMapper) {
        this.noticeMapper = noticeMapper;
    }

    @GetMapping("/notice")
    public ResultData<NoticeDO> notice() {
        return ResultData.success(null, noticeMapper.getNotice());
    }
}
