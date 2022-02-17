package com.beanclasses;

public class StateDetailsList {
    String DMDesc;
    String EffectiveDate;
    String ActualStartDate;
    String ActualEndDate;
    String dmcstatus;
    String ActivityId;
    String InstallationId;
    String StationName;
    String AdvertisementPlayURL;
    String GenrateFileName;
    String SONumber;
    String IssuedToName;
    String IsFileDownload;
    String NetworkCode;

    public StateDetailsList() {
    }

    public String getNetworkCode() {
        return NetworkCode;
    }

    public void setNetworkCode(String networkCode) {
        NetworkCode = networkCode;
    }

    public String GetIssuedToName() {
        return IssuedToName;
    }

    public void SetIssuedToName(String IssuedToName) {
        this.IssuedToName = IssuedToName;
    }

    public String GetEffectiveDate() {
        return EffectiveDate;
    }

    public void SetEffectiveDate(String EffectiveDate) {
        this.EffectiveDate = EffectiveDate;
    }

    public String GetAdvertisementPlayURL() {
        return AdvertisementPlayURL;
    }

    public void SetAdvertisementPlayURL(String advertisementPlayURL) {
        AdvertisementPlayURL = advertisementPlayURL;
    }

    public String GetGenrateFileName() {
        return GenrateFileName;
    }

    public void SetGenrateFileName(String genrateFileName) {
        GenrateFileName = genrateFileName;
    }

    public String GetActivityId() {
        return ActivityId;
    }

    public void SetActivityId(String activityId) {
        ActivityId = activityId;
    }

    public String GetInstallationIdForStateDetailsList() {
        return InstallationId;
    }

    public void SetInstallationIdForStateDetailsList(String installationId) {
        InstallationId = installationId;
    }

    public String GetSONumber() {
        return SONumber;
    }

    public void SetSONumber(String s) {
        this.SONumber = s;
    }

    public String GetDMDesc() {
        return DMDesc;
    }

    public void SetDMDesc(String s) {
        this.DMDesc = s;
    }

    public String GetActualStartDate() {
        return ActualStartDate;
    }

    public void SetActualStartDate(String s) {
        this.ActualStartDate = s;
    }

    public String GetActualEndDate() {
        return ActualEndDate;
    }

    public void SetActualEndDate(String s) {
        this.ActualEndDate = s;
    }

    public String Getdmcstatus() {
        return dmcstatus;
    }

    public void Setdmcstatus(String s) {
        this.dmcstatus = s;
    }

    public String getStationName() {
        return StationName;
    }

    public void setStationName(String stationName) {
        StationName = stationName;
    }
}
