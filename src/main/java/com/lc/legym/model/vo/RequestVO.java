package com.lc.legym.model.vo;

import com.lc.legym.model.legym.LatLng;
import lombok.Data;

import javax.validation.constraints.*;
import java.util.List;

/**
 * @author Aaron Yeung
 * @date 9/4/2022 9:16 PM
 */
@Data
public class RequestVO {

    @DecimalMax(value = "19999999999", message = "手机号格式错误")
    @DecimalMin(value = "11000000000",message = "手机号格式错误")
    private String username;
    @Size(max = 20, min = 5, message = "密码长度不正确")
    @NotEmpty(message = "密码不能为空")
    private String password;
    @NotNull(message = "公里数不能为空")
    @Max(value = 5, message = "跑步里程不能超过3km")
    @Min(value = 1, message = "跑步里程不能小于1km")
    private Double mile;
    private List<LatLng> routeLine;
}
