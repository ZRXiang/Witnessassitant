package com.example.phobes.witnessassitant.model;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by phobes on 2016/6/8.
 */
public class WitenessDetail implements Serializable {
    private int witness_id;
    private int meta_id;
    private String value;
    private Date edit_time;

    private String meta_name;
    public Date getEdit_time() {
        return edit_time;
    }

    public void setEdit_time(Date edit_time) {
        this.edit_time = edit_time;
    }

    public int getMeta_id() {
        return meta_id;
    }

    public void setMeta_id(int meta_id) {
        this.meta_id = meta_id;
    }

    public String getMeta_name() {
        return meta_name;
    }

    public void setMeta_name(String meta_name) {
        this.meta_name = meta_name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getWitness_id() {
        return witness_id;
    }

    public void setWitness_id(int witness_id) {
        this.witness_id = witness_id;
    }

}
