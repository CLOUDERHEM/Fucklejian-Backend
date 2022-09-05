package com.lc.legym.util;

import com.alibaba.fastjson.JSON;
import com.lc.legym.model.Path;
import com.lc.legym.model.legym.LatLng;
import kotlin.Pair;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author Aaron Yeung
 * @date 9/3/2022 7:51 PM
 */
@Slf4j
public class RouteLineUtils {

    private static long lastTimestamp = System.currentTimeMillis();
    private static final long NEED_UPDATE = 1000 * 60 * 5;


    private static List<Path> pathList;

    public static List<LatLng> getRouteLine(Pair<String, String> school, double mile) throws Exception {
        update();
        List<Path> list = new ArrayList<>();
        pathList.forEach((e) -> {
            if (school.getFirst().equals(e.getSchoolId()) || school.getSecond().equals(e.getSchoolName())) {
                list.add(e);
            }
        });
        int index = new Random().nextInt(list.size() + 1) % list.size();
        Path path = list.get(index);
        List<LatLng> routeLine = path.getRouteLine();

        // 增加路径
        return getRouteLine(routeLine);
    }

    public static List<LatLng> getRouteLine(List<LatLng> routeLine) {
        List<LatLng> result = new ArrayList<>(40);
        for (int i = 1; i < routeLine.size(); i++) {
            LatLng start = routeLine.get(i - 1);
            LatLng end = routeLine.get(i);
            int pieces = new Random().nextInt(5) + 10;
            addRouteLine(result, start, end, pieces);
        }
        return result;
    }

    public static void backRouteLine(List<LatLng> routeLine, List<LatLng> result) {
        // 如果第一次跑完里程还差800, 逆向跑一次 todo
        for (int i = result.size() - 2; i >= 0; i--) {
            LatLng start = result.get(i + 1);
            LatLng end = result.get(i);
            int pieces = new Random().nextInt(2) + 2;
            addRouteLine(result, start, end, pieces);
        }

    }

    public static void addRouteLine(List<LatLng> result, LatLng start, LatLng end, int pieces) {

        double periodLat = (end.getLatitude() - start.getLatitude()) / pieces;
        double periodLng = (end.getLongitude() - start.getLongitude()) / pieces;
        double lastLat = start.getLatitude() + (new Random().nextInt(4) - 2) * 0.000001D;
        double lastLng = start.getLongitude() + (new Random().nextInt(4) - 2) * 0.000001D;
        result.add(new LatLng(lastLat, lastLng));
        for (int j = 0; j < pieces; j++) {
            LatLng latLng = randomOffset(new LatLng(lastLat, lastLng), periodLat, periodLng);
            result.add(latLng);
            lastLat = latLng.getLatitude();
            lastLng = latLng.getLongitude();
        }
    }

    public static LatLng randomOffset(LatLng start, double periodLat, double periodLng) {
        double startLat = start.getLatitude();
        double startLng = start.getLongitude();
        startLat += (periodLat + periodLat * (new Random().nextDouble() - 0.5D) / 50);
        startLng += (periodLng + periodLng * (new Random().nextDouble() - 0.5D) / 50);
        return new LatLng(startLat, startLng);
    }

    public static boolean hasRouteLine(Pair<String, String> school) throws Exception {
        update();
        for (Path path : pathList) {
            if (school.getFirst().equals(path.getSchoolId()) || school.getSecond().equals(school.getSecond())) {
                return true;
            }
        }
        return false;
    }

    public static void update() throws Exception {
        if (pathList == null || lastTimestamp - System.currentTimeMillis() > NEED_UPDATE) {
            for (int i = 0; i < 3; i++) {
                try {
                    String s = HttpUtils.doGet("https://clouderhem.github.io/main.json", null);
                    pathList = JSON.parseArray(s, Path.class);
                    lastTimestamp = System.currentTimeMillis();
                    log.info("updated path sources file");
                    return;
                } catch (IOException e) {
                    log.error("connecting github timeout, will try again after 1s");
                    Thread.sleep(1000);
                }
            }
            throw new Exception("can not connect github for path file");
        }

    }
}
