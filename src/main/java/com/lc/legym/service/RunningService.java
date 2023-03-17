package com.lc.legym.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lc.legym.model.legym.LatLng;
import com.lc.legym.model.legym.UploadRunInfoReqVo;
import com.lc.legym.util.HttpUtils;
import com.lc.legym.util.ResultData;
import kotlin.Pair;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import static com.lc.legym.enums.Constant.APP_VERSION;

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

    public ResultData<?> uploadDetail(String username, String password, double validMileage, List<LatLng> routeLine, String ak) throws Exception {

        // seq, 模拟
        String uuid = UUID.randomUUID().toString();
        HttpUtils.doGet(HOST + "/education/semester/getCurrent", uuid, false);
        HttpUtils.doGet(HOST + "/running/app/getHistoryDetails", uuid, false);
        HttpUtils.doGet(HOST + "/authorization/mobileApp/getLastVersion?platform=1", uuid, false);

        JSONObject loginRequestParam = new JSONObject();
        loginRequestParam.put("entrance", "1");
        loginRequestParam.put("password", password);
        loginRequestParam.put("userName", username);
        String loginResponseRaw = HttpUtils.doPost(HOST + "/authorization/user/manage/login", loginRequestParam.toJSONString(), null, true);
        JSONObject loginResponse = JSONObject.parseObject(loginResponseRaw);
        if (loginResponse.getInteger(CODE) != 0) {
            return ResultData.error(loginResponse.getString("message"));
        }

        JSONObject loginResponseData = loginResponse.getJSONObject("data");
        String accessToken = loginResponseData.getString("accessToken");

        String schoolId = loginResponseData.getString("schoolId");
        String schoolName = loginResponseData.getString("schoolName");
        Pair<String, String> school = new Pair<>(schoolId, schoolName);

        String versionResponseRaw = HttpUtils.doGet(HOST + "/authorization/mobileApp/getLastVersion?platform=1", accessToken, true);
        JSONObject versionResponse = JSONObject.parseObject(versionResponseRaw);
        if (versionResponse.getInteger(CODE) != 0) {
            return ResultData.error("获取乐健版本失败, 跑步失败!");
        }

        JSONObject versionResponseData = versionResponse.getJSONObject("data");
        Integer version = versionResponseData.getInteger("version");
        if (version != APP_VERSION) {
            log.error("乐健已更新新版本：" + version);
            return ResultData.error("乐健更新新版本! 跑步失败!");
        }

        String versionLabel = versionResponseData.getString("versionLabel");

        String historyDetailResponseRaw = HttpUtils.doGet(HOST + "/running/app/getHistoryDetails", accessToken, true);
        // JSONObject historyDetailResponse = JSONObject.parseObject(historyDetailResponseRaw)
        // todo 里程校验


        String currentResponseRaw = HttpUtils.doGet(HOST + "/education/semester/getCurrent", accessToken, true);
        JSONObject currentSemesterResponse = JSONObject.parseObject(currentResponseRaw);
        JSONObject currentSemesterResponseData = currentSemesterResponse.getJSONObject("data");
        if (currentSemesterResponse.getInteger(CODE) != 0
            || currentSemesterResponseData == null
            || !StringUtils.hasText(currentSemesterResponseData.getString("id"))) {
            log.error("未在学期中! : {}", schoolName);
            return ResultData.error("获取semesterId失败，不在学期中, 跑步失败");
        }

        String semesterId = currentSemesterResponseData.getString("id");
        JSONObject runningLimitRequestParam = new JSONObject();
        runningLimitRequestParam.put("semesterId", semesterId);
        String runningLimitResponseRaw = HttpUtils.doPost(HOST + "/running/app/getRunningLimit", runningLimitRequestParam.toJSONString(), accessToken, true);
        JSONObject runningLimitResponse = JSONObject.parseObject(runningLimitResponseRaw);
        if (runningLimitResponse.getInteger(CODE) != 0) {
            return ResultData.error("获取RunningLimit失败，跑步失败");
        }

        JSONObject runningLimitResponseData = runningLimitResponse.getJSONObject("data");
        Integer freeRunningPattern = runningLimitResponseData.getInteger("freePattern");
        Integer scopeRunningPattern = runningLimitResponseData.getInteger("scopePattern");
        Pair<Integer, Integer> runningPattern = new Pair<>(freeRunningPattern, scopeRunningPattern);
        String limitationsGoalsSexInfoId = runningLimitResponseData.getString("limitationsGoalsSexInfoId");
        String patternId = runningLimitResponseData.getString("patternId");

        DecimalFormat decimalFormat1 = new DecimalFormat("0.0000000000000000");
        DecimalFormat decimalFormat2 = new DecimalFormat("0.0000000000000000");
        Random random = new Random();
        JSONObject getRunningRangeParam = new JSONObject();
        getRunningRangeParam.put("latitude", 30.82929 + Double.parseDouble(decimalFormat1.format((random.nextDouble() - 0.5) / 1000000)));
        getRunningRangeParam.put("longitude", 104.183844 + Double.parseDouble(decimalFormat2.format((random.nextDouble() - 0.5) / 10000)));
        getRunningRangeParam.put("limitationsGoalsSexInfoId", limitationsGoalsSexInfoId);
        getRunningRangeParam.put("patternId", patternId);
        getRunningRangeParam.put("semesterId", semesterId);
        getRunningRangeParam.put("scoringType", 1);

        String runningRangeResponseRaw = HttpUtils.doPost(HOST + "/running/app/getRunningRange", getRunningRangeParam.toJSONString(), accessToken, false);
        JSONObject runningRangeResponse = JSONObject.parseObject(runningRangeResponseRaw);
        if (runningRangeResponse.getInteger(CODE) != 0) {
            return ResultData.error("无法获取跑步范围！跑步失败");
        }

        // 手机号最后一位固定手机类型
        int deviceIndex = username.charAt(username.length() - 1) - '0';
        UploadRunInfoReqVo uploadRunInfoReqVo = runInfoGeneratorService.getRunningDetail(semesterId,
                limitationsGoalsSexInfoId,
                validMileage,
                versionLabel,
                deviceIndex,
                school,
                routeLine,
                runningPattern);


        String body = JSON.toJSONString(uploadRunInfoReqVo);
        String uploadDetailResponseRaw = HttpUtils.doPost(HOST + "/running/app/v2/uploadRunningDetails", body, accessToken, true);
        JSONObject uploadDetailResponse = JSONObject.parseObject(uploadDetailResponseRaw);
        if (uploadDetailResponse.getInteger(CODE) != 0) {
            return ResultData.error("跑步失败! 未知原因");
        }

        return ResultData.success("跑步成功!");

    }

}
