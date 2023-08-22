package io.github.clouderhem.legym.model.legym;


import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author Aaron Yeung
 */
@Data
@NoArgsConstructor
public final class UploadRunInfoReqVo {
    private String appVersion;
    private double avePace;
    private int calorie;
    private String deviceType;
    private double effectiveMileage;
    private int effectivePart;
    private String endTime;
    private double gpsMileage;
    private int keepTime;
    private String limitationsGoalsSexInfoId;
    private int paceNumber;
    private int paceRange;
    private List<LatLng> routineLine;
    private int scoringType;
    private String semesterId;
    private String signDigital;
    private List<SignPoint> signPoint;
    private String startTime;
    private String systemVersion;
    private double totalMileage;
    private double totalPart;
    private String type;
    private String uneffectiveReason;

    //    public final UploadRunInfoReqVo create(RunRealTimeInfo info, BigDecimal totalDistance,
//                                      BigDecimal effectivePart, int kcal,
//                                      String endTime, UniUserInfoBean userInfo,
//                                      ServerRuleCondition serverRuleCondition,
//                                      boolean dataIsEffective, String unEffectiveReason) {
//        double totalPart;
//        d dVar;
//        String d2;
//        int i2 = 1;
//        int scoringType = serverRuleCondition == null ? 1 : serverRuleCondition.i();
//        if (serverRuleCondition == null || scoringType != 2) {
//            totalPart = 0.0d;
//        } else {
//            g.h.c.e.a aVar = g.h.c.e.a.a;
//            BigDecimal j2 = serverRuleCondition.j();
//            double totalPart2 = aVar.j(totalDistance, j2).doubleValue();
//            totalPart = totalPart2;
//        }
//        double averagePace = info.getAveragePace();
//        double doubleValue = info.getEffectiveDistance().doubleValue();
//        if (scoringType != 1) {
//            i2 = effectivePart.intValue();
//        }
//        int i3 = i2;
//        String j3 = LocationService.a.j();
//        double doubleValue2 = info.getGpsTotalDistance().doubleValue();
//        String str = (serverRuleCondition == null || (d2 = serverRuleCondition.d()) == null) ? "" : d2;
//        int intValue = info.getStepCount().intValue();
//        int paceRange = (int) userInfo.getPaceRange();
//        ArrayList<LatLng> latLngList = info.getLatLngList();
//        String semesterId = userInfo.getSemesterId();
//        String str2 = semesterId == null ? "" : semesterId;
//        UploadRunInfoReqVo result = new UploadRunInfoReqVo(averagePace, kcal, doubleValue, i3, endTime, j3, doubleValue2, str, intValue, paceRange, latLngList, scoringType, str2, info.createSignIDMap(), totalDistance.doubleValue(), totalPart, "自由跑", "", "7.1.2", "3.3.2", "MI8", "", Math.floor((info.getRuntimeMillis() / 1000.0d)));
//        String key = result.getEffectiveMileage() + result.getEffectivePart() + result.getStartTime() + result.getCalorie() + ((int) result.getAvePace()) + result.getKeepTime() + result.getPaceNumber() + result.getTotalMileage() + ((int) result.getTotalPart());
//        String hs = EncryptUtils.getSha1(key);
//        result.setSignDigital(hs);
//        return result;
//    }


}
