package com.example.phobes.witnessassitant.model;

/**
 * Created by phobes on 2016/6/9.
 */
public class WitenessTask {
    private int witenessId;
    private int objectId;
    private String objectName;
    private String sampleId;
    private String Time;
    private String OrgName;
    private String UserName;
    public int getObjectId() {
        return objectId;
    }

    public void setObjectId(int objectId) {
        this.objectId = objectId;
    }

    public String getObjectName() {
        return objectName;
    }

    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    public String getOrgName() {
        return OrgName;
    }

    public void setOrgName(String orgName) {
        OrgName = orgName;
    }

    public String getSampleId() {
        return sampleId;
    }

    public void setSampleId(String sampleId) {
        this.sampleId = sampleId;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }

    public int getWitenessId() {
        return witenessId;
    }

    public void setWitenessId(int witenessId) {
        this.witenessId = witenessId;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }
}
