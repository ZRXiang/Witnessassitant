package com.example.phobes.witnessassitant.model;

/**
 * Created by YLS on 2016/9/27.
 */
public class MixStation {

    private String orgId;
    private String mixStation;

    public String getMixStation() {
        return mixStation;
    }

    public void setMixStation(String mixStation) {
        this.mixStation = mixStation;
    }

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }
    @Override
    public String toString() {
        // 为什么要重写toString()呢？因为适配器在显示数据的时候，如果传入适配器的对象不是字符串的情况下，直接就使用对象.toString()
        // TODO Auto-generated method stub
        return mixStation;
    }

}
