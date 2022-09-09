package com.lc.legym.model;

import lombok.Data;

/**
 * @author Aaron Yeung
 * @date 9/8/2022 8:33 PM
 */
@Data
public class NoticeDO {
    private Integer id;
    private String title;
    private String msg;
    private String author;
    private Integer type;
    private Integer show;
    private Long createTime;
    private Long updateTime;
}
