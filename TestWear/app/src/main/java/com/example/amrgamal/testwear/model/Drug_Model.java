package com.example.amrgamal.testwear.model;

import android.net.Uri;

/**
 * Created by amrga on 04/02/2018.
 */

public class Drug_Model {

    String dDrug, dTime;
    long millsecond;

    public Drug_Model(String dDrug, String dTime) {
        this.dDrug = dDrug;
        this.dTime = dTime;
    }

    public Drug_Model() {
    }

    public long getMillsecond() {
        return millsecond;
    }

    public void setMillsecond(long millsecond) {
        this.millsecond = millsecond;
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


