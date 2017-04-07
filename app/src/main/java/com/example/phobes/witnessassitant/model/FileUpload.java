package com.example.phobes.witnessassitant.model;

/**
 * Created by YLS on 2016/9/16.
 */
public class FileUpload {

    private String fileName;
    private int sendState;
    private int sendPosition;
    private int  receiveState;
    private int  pointId;
    private int objectId;

    public int getSendPosition() {
        return sendPosition;
    }

    public void setSendPosition(int sendPosition) {
        this.sendPosition = sendPosition;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getSendState() {
        return sendState;
    }

    public void setSendState(int sendState) {
        this.sendState = sendState;
    }

    public int getReceiveState() {
        return receiveState;
    }

    public void setReceiveState(int receiveState) {
        this.receiveState = receiveState;
    }

    public int getPointId() {
        return pointId;
    }

    public void setPointId(int pointId) {
        this.pointId = pointId;
    }

    public int getObjectId() {
        return objectId;
    }

    public void setObjectId(int objectId) {
        this.objectId = objectId;
    }
}
