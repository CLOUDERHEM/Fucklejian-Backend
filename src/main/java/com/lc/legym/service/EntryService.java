package com.lc.legym.service;

import com.lc.legym.config.SystemConfig;
import com.lc.legym.enums.Constant;
import com.lc.legym.mapper.AkMapper;
import com.lc.legym.model.AkDO;
import com.lc.legym.model.vo.JobVO;
import com.lc.legym.model.vo.RequestVO;
import com.lc.legym.util.CacheMap;
import com.lc.legym.util.ResultData;
import com.lc.legym.util.RunningLimitUtils;
import com.lc.legym.util.ThreadLocalUtils;
import kotlin.Pair;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * @author Aaron Yeung
 * @date 9/4/2022 10:03 PM
 */
@Slf4j
@Service
public class EntryService {
    private static final Map<String, Future<ResultData<?>>> JOB_LIST = new CacheMap<>(SystemConfig.MAX_OBJECT_EXIST_TIME);

    private RunningService runningService;
    private ThreadPoolTaskExecutor threadPoolExecutor;
    private AkMapper akMapper;

    @Autowired
    public void setAkMapper(AkMapper akMapper) {
        this.akMapper = akMapper;
    }

    @Autowired
    public void setRunningService(RunningService runningService) {
        this.runningService = runningService;
    }

    @Autowired
    public void setThreadPoolExecutor(ThreadPoolTaskExecutor asyncServiceExecutor) {
        this.threadPoolExecutor = asyncServiceExecutor;
    }

    public ResultData<?> run(RequestVO requestVO, String ak) {

        AkDO akDo = akMapper.getAkDo(ak);
        if (akDo == null) {
            return ResultData.error("无效邀请码");
        }
        if (akDo.getUsageCount() >= akDo.getTotalCount()) {
            return ResultData.error("邀请码使用次数已用尽!");
        }
        Pair<String, Long> stringLongPair = RunningLimitUtils.tryRun(requestVO.getUsername());
        if (stringLongPair != null) {
            return ResultData.error(stringLongPair.getFirst(), stringLongPair.getSecond());
        }

        JobVO job = new JobVO();
        String jobId = UUID.randomUUID().toString();
        job.setId(jobId);
        job.setTimestamp(System.currentTimeMillis());
        String remoteAdd = (String) ThreadLocalUtils.get(Constant.REMOTE_ADD_NAME);

        Future<ResultData<?>> submit = threadPoolExecutor.submit(
                () -> {
                    ResultData<?> resultData = runningService.uploadDetail(requestVO.getUsername(),
                            requestVO.getPassword(),
                            requestVO.getMile(),
                            requestVO.getRouteLine(),
                            remoteAdd,
                            ak);

                    // 使用次数加一
                    if (resultData.getCode().equals(0)) {
                        akMapper.useAkDo(ak);
                    }
                    return resultData;
                });

        JOB_LIST.put(jobId, submit);

        return ResultData.success("添加成功, 后台正在运行中", job);

    }


    public ResultData<?> query(String id, String ak) throws ExecutionException, InterruptedException {


        if (JOB_LIST.isEmpty()) {
            return ResultData.error("任务不存在");
        }

        Future<ResultData<?>> future = JOB_LIST.get(id);
        if (future == null) {
            return ResultData.error("任务不存在");
        }

        AkDO akDo = akMapper.getAkDo(ak);
        if (akDo == null) {
            return ResultData.error("无效邀请码");
        }

        if (!future.isDone()) {
            return ResultData.error(2, "跑步任务仍在运行中, 请等待", null);
        }

        return future.get();
    }
}
