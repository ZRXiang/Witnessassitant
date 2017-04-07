package com.example.phobes.witnessassitant.model;

/**
 * Created by phobes on 2016/6/27.
 */
public class EntryCheckData {
    private int entryId;
    private int objectId;
    private int entrustId;
    private String orgId;
    private String batchId;
    private String outputDate;
    private String quantity;
    private String strength;
    private String sampleSpec;
    private String sampleSize;
    private String factory;
    private String productName;
    private String entryDate;
    private String reportId;
    private int accepted;
    private String labComment;
    private String labCheckDate;
    private String superComment;
    private String superCheckDate;
    private String labPerson;
    private String superPerson;
    private String witness;

    public int getEntryId() {
        return entryId;
    }

    public void setEntryId(int entryId) {
        this.entryId = entryId;
    }

    public int getAccepted() {
        return accepted;
    }

    public void setAccepted(int accepted) {
        this.accepted = accepted;
    }

    public String getBatchId() {
        return batchId;
    }

    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }

    public String getEntryDate() {
        return entryDate;
    }

    public void setEntryDate(String entryDate) {
        this.entryDate = entryDate;
    }

    public int getEntrustId() {
        return entrustId;
    }

    public void setEntrustId(int entrustId) {
        this.entrustId = entrustId;
    }

    public String getFactory() {
        return factory;
    }

    public void setFactory(String factory) {
        this.factory = factory;
    }

    public int getObjectId() {
        return objectId;
    }

    public void setObjectId(int objectId) {
        this.objectId = objectId;
    }

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    public String getOutputDate() {
        return outputDate;
    }

    public void setOutputDate(String outputDate) {
        this.outputDate = outputDate;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getReportId() {
        return reportId;
    }

    public void setReportId(String reportId) {
        this.reportId = reportId;
    }

    public String getSampleSpec() {
        return sampleSpec;
    }

    public void setSampleSpec(String sampleSpec) {
        this.sampleSpec = sampleSpec;
    }

    public String getSampleSize() {
        return sampleSize;
    }

    public void setSampleSize(String sampleSize) {
        this.sampleSize = sampleSize;
    }

    public String getStrength() {
        return strength;
    }

    public void setStrength(String strength) {
        this.strength = strength;
    }

    public String getLabCheckDate() {
        return labCheckDate;
    }

    public void setLabCheckDate(String labCheckDate) {
        this.labCheckDate = labCheckDate;
    }

    public String getLabComment() {
        return labComment;
    }

    public void setLabComment(String labComment) {
        this.labComment = labComment;
    }

    public String getSuperCheckDate() {
        return superCheckDate;
    }

    public void setSuperCheckDate(String superCheckDate) {
        this.superCheckDate = superCheckDate;
    }

    public String getSuperComment() {
        return superComment;
    }

    public void setSuperComment(String superComment) {
        this.superComment = superComment;
    }

    public String getLabPerson() {
        return labPerson;
    }

    public void setLabPerson(String labPerson) {
        this.labPerson = labPerson;
    }

    public String getSuperPerson() {
        return superPerson;
    }

    public void setSuperPerson(String superPerson) {
        this.superPerson = superPerson;
    }

    public String getWitness() {
        return witness;
    }

    public void setWitness(String witness) {
        this.witness = witness;
    }
}
