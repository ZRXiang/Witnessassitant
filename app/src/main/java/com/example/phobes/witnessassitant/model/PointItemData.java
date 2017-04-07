package com.example.phobes.witnessassitant.model;

/**
 * Created by YLS on 2016/9/9.
 */
public class PointItemData {

    private int pointId;
    private int dataId;
    private int metaId;
    private int metaType;
    private String metaName;
    private String itemValue;


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

    public int getMetaId() {
        return metaId;
    }

    public void setMetaId(int metaId) {
        this.metaId = metaId;
    }

    public int getMetaType() {
        return metaType;
    }

    public void setMetaType(int metaType) {
        this.metaType = metaType;
    }

    public String getMetaName() {
        return metaName;
    }

    public void setMetaName(String metaName) {
        this.metaName = metaName;
    }

    public String getItemValue() {
        return itemValue;
    }

    public void setItemValue(String itemValue) {
        this.itemValue = itemValue;
    }

}
