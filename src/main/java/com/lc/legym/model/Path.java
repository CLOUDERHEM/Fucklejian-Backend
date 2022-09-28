package com.lc.legym.model;

import com.lc.legym.model.legym.LatLng;
import lombok.Data;

import java.util.List;

/**
 * @author Aaron Yeung
 * @date 9/3/2022 8:25 PM
 */
@Data
public class Path {
    private String schoolId;
    private String schoolName;
    private String updateTime;
    private int miles;
    private List<LatLng> routeLine;
}
