package com.lc.legym.config;

/**
 * @author Aaron Yeung
 * @date 1/21/2022 4:05 PM
 */
public final class SystemConfig {


    /**
     * 运行结果保存的最大时长, 默认15s
     */
    public static final int MAX_OBJECT_EXIST_TIME = 30 * 1000;

    /**
     * 核心线程数量
     */
    public static final int CORE_POOL_SIZE = 5;

    /**
     * 应靠近cpu核心数
     */
    public static final int MAX_POOL_SIZE = 13;

    /**
     * 提交的任务数目超过 QUEUE_CAPACITY + MAX_POOL_SIZE 时候, 任务将被丢弃
     */
    public static final int QUEUE_CAPACITY = 100;


}
