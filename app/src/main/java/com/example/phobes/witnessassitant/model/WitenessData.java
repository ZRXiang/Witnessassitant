package com.example.phobes.witnessassitant.model;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by phobes on 2016/6/6.
 */
public class WitenessData{

    private int witness_id;
    private int object_id;
    private int entry_id;
    private String sample_id; //?
    private int sample_person;
    private String sample_person_name;
    private String sample_org_id;
    private String sample_org_name;
    private String sample_image;
    private double sample_longitude;
    private double sample_latitude;
    private String sample_time;
    private int witness_person;
    private String witness_person_name;
    private String witness_org_id;
    private String witness_org_name;
    private double witness_longitude;
    private double witness_latitude;
    private String witness_image;
    private int uploadded=0;
    //    private Date witness_time;
//    private Date apply_time;
    private String witness_time;
    private String apply_time;
    private int data_id;
    private String object_name;
    private int uploaded=0;
    private int apply_from;
    private String comment;
    private String test_image;
    private double test_latitude;
    private double test_longitude;
    private String test_time;
    private String test_comment;
    private int witnessType;
    private String batch_id;
    private String test_items;
    private String test_item;
    private int nTestItems;
    public WitenessData(){

    }
    public int getTestItems() {
        return nTestItems;
    }

    public void setTestItems(int test_items) {
        this.nTestItems = test_items;
    }
    public String getTest_item() {
        return test_item;
    }

    public void setTest_item(String test_item) {
        this.test_item = test_item;
    }

    public String getTest_items() {
        return test_items;
    }

    public void setTest_items(String test_items) {
        this.test_items = test_items;
    }

    public String getApply_time() {
        return apply_time;
    }

    public void setApply_time(String apply_time) {
        this.apply_time = apply_time;
    }

    public String getWitness_time() {
        return witness_time;
    }

    public void setWitness_time(String witness_time) {
        this.witness_time = witness_time;
    }




    public int getApply_from() {
        return apply_from;
    }

    public void setApply_from(int apply_from) {
        this.apply_from = apply_from;
    }

   /* public Date getApply_time() {
        return apply_time;
    }

    public void setApply_time(Date apply_time) {
        this.apply_time = apply_time;
    }*/

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getData_id() {
        return data_id;
    }

    public void setData_id(int data_id) {
        this.data_id = data_id;
    }

    public int getObject_id() {
        return object_id;
    }

    public void setObject_id(int object_id) {
        this.object_id = object_id;
    }

    public String getObject_name() {
        return object_name;
    }

    public void setObject_name(String object_name) {
        this.object_name = object_name;
    }

    public String getSample_id() {
        return sample_id;
    }

    public void setSample_id(String sample_id) {
        this.sample_id = sample_id;
    }

    public String getSample_image() {
        return sample_image;
    }

    public void setSample_image(String sample_image) {
        this.sample_image = sample_image;
    }

    public double getSample_latitude() {
        return sample_latitude;
    }

    public void setSample_latitude(double sample_latitude) {
        this.sample_latitude = sample_latitude;
    }

    public String getSample_org_id() {
        return sample_org_id;
    }

    public void setSample_org_id(String sample_org_id) {
        this.sample_org_id = sample_org_id;
    }

    public double getSample_longitude() {
        return sample_longitude;
    }

    public void setSample_longitude(double sample_longitude) {
        this.sample_longitude = sample_longitude;
    }

    public String getSample_org_name() {
        return sample_org_name;
    }

    public void setSample_org_name(String sample_org_name) {
        this.sample_org_name = sample_org_name;
    }

    public String getSample_person_name() {
        return sample_person_name;
    }

    public void setSample_person_name(String sample_person_name) {
        this.sample_person_name = sample_person_name;
    }

    public int getSample_person() {
        return sample_person;
    }

    public void setSample_person(int sample_person) {
        this.sample_person = sample_person;
    }

    public String getSample_time() {
        return sample_time;
    }

    public void setSample_time(String sample_time) {
        this.sample_time = sample_time;
    }

    public int getUploaded() {
        return uploaded;
    }

    public void setUploaded(int uploaded) {
        this.uploaded = uploaded;
    }

    public int getWitness_id() {
        return witness_id;
    }

    public void setWitness_id(int witness_id) {
        this.witness_id = witness_id;
    }

    public int getEntry_id() {
        return entry_id;
    }

    public void setEntry_id(int entry_id) {
        this.entry_id = entry_id;
    }

    public String getWitness_image() {
        return witness_image;
    }

    public void setWitness_image(String witness_image) {
        this.witness_image = witness_image;
    }

    public double getWitness_latitude() {
        return witness_latitude;
    }

    public void setWitness_latitude(double witness_latitude) {
        this.witness_latitude = witness_latitude;
    }

    public double getWitness_longitude() {
        return witness_longitude;
    }

    public void setWitness_longitude(double witness_longitude) {
        this.witness_longitude = witness_longitude;
    }

    public String getWitness_org_id() {
        return witness_org_id;
    }

    public void setWitness_org_id(String witness_org_id) {
        this.witness_org_id = witness_org_id;
    }

    public String getWitness_org_name() {
        return witness_org_name;
    }

    public void setWitness_org_name(String witness_org_name) {
        this.witness_org_name = witness_org_name;
    }

    public int getWitness_person() {
        return witness_person;
    }

    public void setWitness_person(int witness_person) {
        this.witness_person = witness_person;
    }

    public String getWitness_person_name() {
        return witness_person_name;
    }

    public void setWitness_person_name(String witness_person_name) {
        this.witness_person_name = witness_person_name;
    }

/*    public Date getWitness_time() {
        return witness_time;
    }
    public void setWitness_time(Date witness_time) {
        this.witness_time = witness_time;
    }*/

    public String getTest_image() {
        return test_image;
    }

    public void setTest_image(String test_image) {
        this.test_image = test_image;
    }

    public String getTest_comment() {
        return test_comment;
    }

    public void setTest_comment(String test_comment) {
        this.test_comment = test_comment;
    }

    public double getTest_latitude() {
        return test_latitude;
    }

    public void setTest_latitude(double test_latitude) {
        this.test_latitude = test_latitude;
    }

    public double getTest_longitude() {
        return test_longitude;
    }

    public void setTest_longitude(double test_longitude) {
        this.test_longitude = test_longitude;
    }

    public String getTest_time() {
        return test_time;
    }

    public void setTest_time(String test_time) {
        this.test_time = test_time;
    }

    public int getUploadded() {
        return uploadded;
    }

    public void setUploadded(int uploadded) {
        this.uploadded = uploadded;
    }

    public int getWitnessType() {
        return witnessType;
    }

    public void setWitnessType(int witnessType) {
        this.witnessType = witnessType;
    }

    public String getBatch_id() {
        return batch_id;
    }

    public void setBatch_id(String batch_id) {
        this.batch_id = batch_id;
    }
}
