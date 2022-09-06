package com.lc.legym.service;

import com.alibaba.fastjson.JSONObject;
import com.lc.legym.model.legym.LatLng;
import com.lc.legym.util.HttpUtils;
import com.lc.legym.util.ResultData;
import com.lc.legym.util.RouteLineUtils;
import kotlin.Pair;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Random;

/**
 * @author Aaron Yeung
 */
@Service
@Slf4j
public class RunningService {
    private static final String HOST = "https://cpes.legym.cn";
    private static final String CODE = "code";

    private RunInfoGeneratorService runInfoGeneratorService;

    @Autowired
    public void setRunInfoGeneratorService(RunInfoGeneratorService runInfoGeneratorService) {
        this.runInfoGeneratorService = runInfoGeneratorService;
    }

    public ResultData<?> uploadDetail(String userId, String userPassword, double validMileage, List<LatLng> routeLine, String remoteAdd) throws Exception {

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("entrance", "1");
        jsonObject.put("password", userPassword);
        jsonObject.put("userName", userId);
        HttpUtils.doGet(HOST + "/education/semester/getCurrent", null);
        String info = HttpUtils.doPost(HOST + "/authorization/user/manage/login", jsonObject.toJSONString(), null);
        if ((jsonObject = JSONObject.parseObject(info)).getInteger(CODE) != 0) {
            return ResultData.error(jsonObject.getString("message"));
        }

        jsonObject = jsonObject.getJSONObject("data");
        String accessToken = jsonObject.getString("accessToken");

        String schoolId = jsonObject.getString("schoolId");
        String schoolName = jsonObject.getString("schoolName");
        Pair<String, String> school = new Pair<>(schoolId, schoolName);
        if (CollectionUtils.isEmpty(routeLine) && !RouteLineUtils.hasRouteLine(school)) {
            return ResultData.error("未找到跑步路线, 跑步失败");
        }

        info = HttpUtils.doGet(HOST + "/authorization/mobileApp/getLastVersion?platform=1", accessToken);
        if ((jsonObject = JSONObject.parseObject(info)).getInteger(CODE) != 0) {
            return ResultData.error("获取版本失败, 为确保安全结束跑步");
        }

        if (jsonObject.getJSONObject("data").getInteger("version") != 3000320) {
            log.info("乐健已更新新版本：" + jsonObject.getJSONObject("data").getInteger("version"));
            return ResultData.error("乐健更新新版本！无法确保安全，跑步失败！");
        }

        String versionLabel = jsonObject.getJSONObject("data").getString("versionLabel");
        info = HttpUtils.doGet(HOST + "/running/app/getHistoryDetails", accessToken);
        if (JSONObject.parseObject(info).getInteger(CODE) != 0) {
            log.info("{}", info);
        }

        info = HttpUtils.doGet(HOST + "/education/semester/getCurrent", accessToken);
        if ((jsonObject = JSONObject.parseObject(info)).getInteger(CODE) != 0 || jsonObject.get("data") == null) {
            return ResultData.error("获取semesterId失败，不在学期中, 跑步失败");
        }

        String semesterId = jsonObject.getJSONObject("data").getString("id");
        JSONObject semJson = new JSONObject();
        semJson.put("semesterId", semesterId);
        info = HttpUtils.doPost(HOST + "/running/app/getRunningLimit", semJson.toJSONString(), accessToken);
        if ((jsonObject = JSONObject.parseObject(info)).getInteger(CODE) != 0) {
            return ResultData.error("获取RunningLimit失败，跑步失败");
        }

        String limitationsGoalsSexInfoId = jsonObject.getJSONObject("data").getString("limitationsGoalsSexInfoId");
        String patternId = jsonObject.getJSONObject("data").getString("patternId");
        DecimalFormat decimalFormat1 = new DecimalFormat("0.0000000000000000");
        DecimalFormat decimalFormat2 = new DecimalFormat("0.0000000000000000");
        Random random = new Random();
        jsonObject = new JSONObject();
        jsonObject.put("latitude", 30.82929175755101 + Double.parseDouble(decimalFormat1.format((random.nextDouble() - 0.5) / 1000000)));
        jsonObject.put("longitude", 104.18384454565867 + Double.parseDouble(decimalFormat2.format((random.nextDouble() - 0.5) / 10000)));
        jsonObject.put("limitationsGoalsSexInfoId", limitationsGoalsSexInfoId);
        jsonObject.put("patternId", patternId);
        jsonObject.put("semesterId", semesterId);
        jsonObject.put("scoringType", 1);
        String runningRange = HttpUtils.doPost(HOST + "/running/app/getRunningRange", jsonObject.toJSONString(), accessToken);
        if (JSONObject.parseObject(runningRange).getInteger(CODE) != 0) {
            return ResultData.error("无法获取跑步范围！跑步失败");
        }

        // 手机号最后一位固定手机类型
        int deviceIndex = userId.charAt(userId.length() - 1) - '0';
        String runningInfo = runInfoGeneratorService.getRunningDetail(semesterId,
                limitationsGoalsSexInfoId,
                validMileage,
                versionLabel,
                deviceIndex,
                school,
                routeLine);


        String endJsonReturn = HttpUtils.doPost(HOST + "/running/app/v2/uploadRunningDetails", runningInfo, accessToken);
        JSONObject endReturn = JSONObject.parseObject(endJsonReturn);
        if (endReturn.getInteger(CODE) != 0) {
            return ResultData.error("数据包发送失败！跑步失败!");
        }

        return ResultData.success("跑步成功!", endReturn.getString("data"));

    }

}
