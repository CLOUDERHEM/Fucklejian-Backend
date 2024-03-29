package io.github.clouderhem.legym.service;

import io.github.clouderhem.legym.enums.Constant;
import io.github.clouderhem.legym.model.Device;
import io.github.clouderhem.legym.model.legym.LatLng;
import io.github.clouderhem.legym.model.legym.UploadRunInfoReqVo;
import io.github.clouderhem.legym.util.EncryptUtils;
import io.github.clouderhem.legym.util.RouteLineUtils;
import kotlin.Pair;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * @author Aaron Yeung
 */
@Service
@Slf4j
public class RunInfoGeneratorService {
    /**
     * 49.6 ~ 50
     */
    private static final double CALORIE_PER_MILEAGE = 49.9;

    public UploadRunInfoReqVo getRunningDetail(String semesterId,
                                               String limitationsGoalsSexInfoId,
                                               double validMileage,
                                               String appVersion,
                                               int deviceIndex,
                                               Pair<String, String> school,
                                               List<LatLng> routeLine,
                                               Pair<Integer, Integer> runningPattern) throws Exception {
        DecimalFormat df = new DecimalFormat("#.000");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        int scoringType = 1;
        int effectivePart = 1;
        double totalPart = 0.0D;

        validMileage = Double.parseDouble(df.format(validMileage));
        double totMileage = validMileage;
        double effectiveMileage = Math.min(validMileage, Constant.MAX_MILE);

        long endTimestamp = System.currentTimeMillis() - 1000;
        // 跑一千米需要秒数
        int avePaceSecond = (new Random().nextInt(200) + 374);
        // millis
        double avePace = avePaceSecond * 1000 + new Random().nextInt(100);
        int totalTime = (int) (totMileage * avePaceSecond);
        long startTimestamp = endTimestamp - (long) totalTime * 1000;
        String startTime = simpleDateFormat.format(new Date(startTimestamp));
        String endTime = simpleDateFormat.format(new Date(endTimestamp));
        int calorie = (int) (totMileage * CALORIE_PER_MILEAGE);

        double pace = 0.5 + new Random().nextInt(6) / 10.0;
        int paceNumber = (int) (totMileage * 1000.0D / pace / 2);

        UploadRunInfoReqVo result = new UploadRunInfoReqVo();

        if (runningPattern.getFirst() != null && runningPattern.getFirst().equals(1)) {
            result.setType("自由跑");
        } else if ((runningPattern.getSecond() != null && runningPattern.getSecond().equals(1))) {
            result.setType("范围跑");
        } else {
            throw new Exception("自由跑和范围跑均为关闭! 跑步失败");
        }
        result.setScoringType(scoringType);
        result.setSemesterId(semesterId);
        result.setEffectiveMileage(effectiveMileage);
        result.setTotalMileage(totMileage);
        result.setGpsMileage(totMileage);
        result.setStartTime(startTime);
        result.setEndTime(endTime);
        result.setKeepTime(totalTime);
        result.setCalorie(calorie);
        result.setEffectivePart(effectivePart);
        result.setLimitationsGoalsSexInfoId(limitationsGoalsSexInfoId);
        result.setSignPoint(new ArrayList<>());
        result.setRoutineLine(RouteLineUtils.getRouteLine(routeLine));
        result.setAvePace(avePace);
        result.setTotalPart(totalPart);
        result.setPaceNumber(paceNumber);
        result.setPaceRange(0);
        result.setUneffectiveReason("");

        Device device = Device.getDeviceList().get(deviceIndex);
        result.setDeviceType(device.getType());
        result.setSystemVersion(device.getVersion());
        result.setAppVersion(appVersion);

        String key = result.getEffectiveMileage() + "" +
                result.getEffectivePart() +
                result.getStartTime() +
                result.getCalorie() +
                ((int) result.getAvePace()) +
                result.getKeepTime() +
                result.getPaceNumber() +
                result.getTotalMileage() +
                ((int) result.getTotalPart());

        String hs = EncryptUtils.hs(key);
        result.setSignDigital(hs);

        return result;

    }


}
