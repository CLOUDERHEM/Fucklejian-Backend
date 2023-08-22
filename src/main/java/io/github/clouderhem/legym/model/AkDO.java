package io.github.clouderhem.legym.model;

import lombok.Data;

/**
 * @author Aaron Yeung
 * @date 9/6/2022 7:45 PM
 */
@Data
public class AkDO {
    private String ak;
    private Integer usageCount;
    private Integer totalCount;
    private Long createTime;
    private Long updateTime;
}
