package com.lc.legym.util;

import com.lc.legym.model.legym.LatLng;
import lombok.extern.slf4j.Slf4j;
import org.gavaghan.geodesy.Ellipsoid;
import org.gavaghan.geodesy.GeodeticCalculator;
import org.gavaghan.geodesy.GlobalCoordinates;

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

}
