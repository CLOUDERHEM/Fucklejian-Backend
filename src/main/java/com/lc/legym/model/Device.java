package com.lc.legym.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Aaron Yeung
 * @date 9/3/2022 12:50 PM
 */
@Data
public class Device {
    private String type;
    private String version;
    private static List<Device> deviceList = new ArrayList<>();

    public static List<Device> getDeviceList() {
        return deviceList;
    }

    public Device(String type, String version) {
        this.type = type;
        this.version = version;
    }

    static {
        deviceList.add(new Device("Xiaomi MIX 2S", "10"));
        deviceList.add(new Device("Xiaomi MI 11", "11"));
        deviceList.add(new Device("Xiaomi MI 11U", "11"));
        deviceList.add(new Device("Xiaomi MI 10", "11"));
        deviceList.add(new Device("samsung SM-G9960", "11"));
        deviceList.add(new Device("samsung SM-S9080", "11"));
        deviceList.add(new Device("HUAWEI LYA-AL00", "11"));
        deviceList.add(new Device("HUAWEI VOG-TL00", "11"));
        deviceList.add(new Device("HUAWEI ELS_AN00", "11"));
        deviceList.add(new Device("OPPO OPPO R17", "11"));
    }
}
