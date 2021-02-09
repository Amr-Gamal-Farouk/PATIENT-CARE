package com.example.amrgamal.weartracker.models;

/**
 * Created by amrga on 04/02/2018.
 */

public class Drug_Model {

    String dDrug, dTime;

    public Drug_Model(String dDrug, String dTime) {
        this.dDrug = dDrug;
        this.dTime = dTime;
    }

    public Drug_Model() {
    }

    public String getdDrug() {
        return dDrug;
    }

    public void setdDrug(String dDrug) {
        this.dDrug = dDrug;
    }

    public String getdTime() {
        return dTime;
    }

    public void setdTime(String dTime) {
        this.dTime = dTime;
    }
}


