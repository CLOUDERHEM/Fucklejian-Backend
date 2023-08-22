package io.github.clouderhem.legym.model.vo;

import lombok.Data;

/**
 * @author Aaron Yeung
 * @date 9/15/2022 11:11 AM
 */
@Data
public class TimeVO {
    private Long client;
    private Long server;

    public TimeVO() {
    }

    public TimeVO(Long client, Long server) {
        this.client = client;
        this.server = server;
    }
}
