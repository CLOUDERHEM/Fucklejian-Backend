package io.github.clouderhem.legym.controller;

import io.github.clouderhem.legym.mapper.NoticeMapper;
import io.github.clouderhem.legym.model.NoticeDO;
import io.github.clouderhem.legym.util.ResultData;
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
