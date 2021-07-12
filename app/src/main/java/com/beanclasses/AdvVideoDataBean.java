package com.beanclasses;

import java.util.ArrayList;

public class AdvVideoDataBean {
    String NetworkCode = "", InstalationId = "", InstalationName = "", AdvertisementCode = "", AdvertisementDesc = "",
    ClipPath_URL = "",EffectiveDateTo = "", EffectiveDatefrom = "", ApproveDate="",SOPReleaseDate="",SOHeaderStatus="",
            SoNumber="",Statuschangedate="";

    ArrayList<AdvVideoDataBean> listStns;

    public String getNetworkCode() {
        return NetworkCode;
    }

    public void setNetworkCode(String networkCode) {
        NetworkCode = networkCode;
    }

    public String getInstalationId() {
        return InstalationId;
    }

    public void setInstalationId(String instalationId) {
        InstalationId = instalationId;
    }

    public String getInstalationName() {
        return InstalationName;
    }

    public void setInstalationName(String instalationName) {
        InstalationName = instalationName;
    }

    public String getAdvertisementCode() {
        return AdvertisementCode;
    }

    public void setAdvertisementCode(String advertisementCode) {
        AdvertisementCode = advertisementCode;
    }

    public String getAdvertisementDesc() {
        return AdvertisementDesc;
    }

    public void setAdvertisementDesc(String advertisementDesc) {
        AdvertisementDesc = advertisementDesc;
    }

    public String getClipPath_URL() {
        return ClipPath_URL;
    }

    public void setClipPath_URL(String clipPath_URL) {
        ClipPath_URL = clipPath_URL;
    }

    public String getEffectiveDateTo() {
        return EffectiveDateTo;
    }

    public void setEffectiveDateTo(String effectiveDateTo) {
        EffectiveDateTo = effectiveDateTo;
    }

    public String getEffectiveDatefrom() {
        return EffectiveDatefrom;
    }

    public void setEffectiveDatefrom(String effectiveDatefrom) {
        EffectiveDatefrom = effectiveDatefrom;
    }

    public ArrayList<AdvVideoDataBean> getListStns() {
        return listStns;
    }

    public void setListStns(ArrayList<AdvVideoDataBean> listStns) {
        this.listStns = listStns;
    }

    public String getApproveDate() {
        return ApproveDate;
    }

    public void setApproveDate(String approveDate) {
        ApproveDate = approveDate;
    }

    public String getSOPReleaseDate() {
        return SOPReleaseDate;
    }

    public void setSOPReleaseDate(String SOPReleaseDate) {
        this.SOPReleaseDate = SOPReleaseDate;
    }

    public String getSOHeaderStatus() {
        return SOHeaderStatus;
    }

    public void setSOHeaderStatus(String SOHeaderStatus) {
        this.SOHeaderStatus = SOHeaderStatus;
    }

    public String getSoNumber() {
        return SoNumber;
    }

    public void setSoNumber(String soNumber) {
        SoNumber = soNumber;
    }

    public String getStatuschangedate() {
        return Statuschangedate;
    }

    public void setStatuschangedate(String statuschangedate) {
        Statuschangedate = statuschangedate;
    }
}
