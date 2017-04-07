package com.example.phobes.witnessassitant.model;

/**
 * Created by YLS on 2016/9/17.
 */
public class SendTestValue {

    private int dataId;
    private int mateId;
    private String value;
    private int pointId;
    private int indexId;
    private int uploadStatus;

    public int getUploadStatus() {
        return uploadStatus;
    }

    public void setUploadStatus(int uploadStatus) {
        this.uploadStatus = uploadStatus;
    }

    public int getIndexId() {
        return indexId;
    }

    public void setIndexId(int indexId) {
        this.indexId = indexId;
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

    public int getMateId() {
        return mateId;
    }

    public void setMateId(int mateId) {
        this.mateId = mateId;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
