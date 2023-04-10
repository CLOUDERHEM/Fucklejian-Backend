package com.lc.legym.model.vo;


import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;


/**
 * @author Aaron Yeung
 * @date 9/6/2022 9:21 PM
 */
@Data
public class RequestAkVO {
    @Min(value = 1, message = "次数范围不正确, 次数最小为1")
    @Max(value = 50, message = "次数范围不正确, 次数最大为50")
    private Integer count;
    @Size(min = 5, max = 50, message = "私人秘钥格式错误")
    @NotEmpty(message = "私人秘钥不能为空")
    private String ak;
}
