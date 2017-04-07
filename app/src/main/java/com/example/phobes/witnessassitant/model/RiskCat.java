package com.example.phobes.witnessassitant.model;

/**
 * Created by YLS on 2016-10-26.
 */
public class RiskCat {
    private int riskCatId;
    private String riskCatName;

    public String getRiskCatName() {
        return riskCatName;
    }

    public void setRiskCatName(String riskCatName) {
        this.riskCatName = riskCatName;
    }

    public int getRiskCatId() {
        return riskCatId;
    }

    public void setRiskCatId(int riskCatId) {
        this.riskCatId = riskCatId;
    }


    @Override
    public String toString() {
        return getRiskCatName();
    }
}
