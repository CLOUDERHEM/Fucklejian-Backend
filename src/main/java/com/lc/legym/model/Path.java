package com.lc.legym.model;

import com.alibaba.fastjson.JSON;
import com.lc.legym.model.legym.LatLng;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Aaron Yeung
 * @date 9/3/2022 8:25 PM
 */
@Slf4j
@Data
public class Path {
    private String schoolId;
    private String schoolName;
    private String updateTime;
    private int miles;
    private List<LatLng> routeLine;

    public static Path getInstance() {
        Path path = new Path();
        path.setSchoolId("402881ea7c39c5d5017c39d134c5039b");
        path.setSchoolName("西南石油大学（南充校区）");
        path.setMiles(1000);
        List<LatLng> list = new ArrayList<>();
        list.add(new LatLng(30.821386, 104.181161));
        list.add(new LatLng(30.821386, 104.181161));
        list.add(new LatLng(30.821386, 104.181161));
        list.add(new LatLng(30.821386, 104.181161));
        path.setRouteLine(list);

        return path;
    }

    public static void main(String[] args) {
        List<Path> pathList = new ArrayList<>();
        pathList.add(getInstance());
        pathList.add(getInstance());
        String s = JSON.toJSONString(pathList);
        log.info(s);
        List<Path> pathList1 = JSON.parseArray(s, Path.class);
        log.info("{}", pathList1);
    }
}
