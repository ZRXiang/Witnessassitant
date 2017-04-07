package com.example.phobes.witnessassitant.model;

/**
 * Created by YLS on 2016/9/27.
 */
public class MixMachine {
    private String deviceId;
    private String deviceName;
    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    @Override
    public String toString() {
        return deviceName;
    }
}
