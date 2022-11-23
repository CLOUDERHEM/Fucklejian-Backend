package com.lc.legym.util;

import com.alibaba.fastjson.JSON;
import com.lc.legym.model.Path;
import com.lc.legym.model.legym.LatLng;
import kotlin.Pair;
import lombok.extern.slf4j.Slf4j;
import org.gavaghan.geodesy.Ellipsoid;
import org.gavaghan.geodesy.GeodeticCalculator;
import org.gavaghan.geodesy.GlobalCoordinates;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.lc.legym.enums.Constant.DISTANCE_BETWEEN_POINT;
import static com.lc.legym.enums.Constant.MAX_ROUTE_LINE_SIZE;

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
        if (list.isEmpty()) {
            throw new Exception("没有跑步路线, 跑步失败");
        }
        int index = new Random().nextInt(list.size()) % list.size();
        Path path = list.get(index);
        List<LatLng> routeLine = path.getRouteLine();

        // 增加路径
        return getRouteLine(routeLine);
    }

    public static List<LatLng> getRouteLine(List<LatLng> routeLine) throws Exception {
        List<LatLng> result = new ArrayList<>(250);
        for (int i = 1; i < routeLine.size(); i++) {
            LatLng start = routeLine.get(i - 1);
            LatLng end = routeLine.get(i);
            int pieces = (int) getDistance(start, end) / DISTANCE_BETWEEN_POINT;
            if (pieces == 0) {
                continue;
            }
            addRouteLine(result, start, end, pieces);
        }
        return result;
    }

    public static double getDistance(LatLng a, LatLng b) {
        return new GeodeticCalculator().calculateGeodeticCurve(
                Ellipsoid.Sphere,
                new GlobalCoordinates(a.getLatitude(), a.getLongitude()),
                new GlobalCoordinates(b.getLatitude(), b.getLongitude())
        ).getEllipsoidalDistance();
    }

    public static void addRouteLine(List<LatLng> result, LatLng start, LatLng end, int pieces) throws Exception {

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

            // 跑步点数过多乐健会异常
            if (result.size() > MAX_ROUTE_LINE_SIZE) {
                throw new Exception("跑步距离过大, 偏移后点数量过多, 跑步错误");
            }
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
            if (school.getFirst().equals(path.getSchoolId()) || school.getSecond().equals(path.getSchoolName())) {
                return true;
            }
        }
        return false;
    }

    public static void update() throws Exception {
        if (pathList == null || lastTimestamp - System.currentTimeMillis() > NEED_UPDATE) {
            for (int i = 0; i < 3; i++) {
                try {
                    String s = HttpUtils.doGet("https://clouderhem.github.io/main.json", null, false);
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
