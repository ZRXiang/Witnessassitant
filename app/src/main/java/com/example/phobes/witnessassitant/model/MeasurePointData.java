package com.example.phobes.witnessassitant.model;

/**
 * Created by YLS on 2016/9/4.
 */
public class MeasurePointData {

    private  int pointId;
    private int dataId;
    private int indexId;
    private int SN;
    private int receiveState;
    private long receivePos;
    private int sendState;
    private int sendPos;
    private String fileName;
    private String pointName;
    private int measurePointStatus;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getPointId() {
        return pointId;
    }

    public void setPointId(int pointId) {
        this.pointId = pointId;
    }

    public int getDataId() {
        return dataId;
    }

    public void setDataId(int dataId) {
        this.dataId = dataId;
    }

    public int getIndexId() {
        return indexId;
    }

    public void setIndexId(int indexId) {
        this.indexId = indexId;
    }

    public int getSN() {
        return SN;
    }

    public void setSN(int SN) {
        this.SN = SN;
    }

    public int getReceiveState() {
        return receiveState;
    }

    public void setReceiveState(int receiveState) {
        this.receiveState = receiveState;
    }

    public long getReceivePos() {
        return receivePos;
    }

    public void setReceivePos(long receivePos) {
        this.receivePos = receivePos;
    }

    public int getSendState() {
        return sendState;
    }

    public void setSendState(int sendState) {
        this.sendState = sendState;
    }

    public int getSendPos() {
        return sendPos;
    }

    public void setSendPos(int sendPos) {
        this.sendPos = sendPos;
    }

    public int getMeasurePointStatus(){
        return this.measurePointStatus;
    }
    public void setMeasurePointStatus(int measurePointStatus){
        this.measurePointStatus=measurePointStatus;
    }

    public String getPointName() {
        return pointName;
    }

    public void setPointName(String pointName) {
        this.pointName = pointName;
    }
}
