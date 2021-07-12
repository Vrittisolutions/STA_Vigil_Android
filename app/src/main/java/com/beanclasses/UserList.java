package com.beanclasses;

public class UserList {

    String Installation;
    String StationName;
    String Mobile;
    int WorkTypeMasterId;
    String WorkType;
    String Remarks;
    String currentDate;
    String currentLocation;
    Double latitude;
    Double longitude;

    String Serial;
    String SetItem;

    public String getInstallation() {
        return Installation;
    }
    public void setInstallation(String installation) {
        Installation = installation;
    }
    public String getStationName() {
        return StationName;
    }
    public void setStationName(String stationName) {
        StationName = stationName;
    }
    public String getMobile() {
        return Mobile;
    }
    public void setMobile(String mobile) {
        Mobile = mobile;
    }
    public int getWorkTypeMasterId() {
        return WorkTypeMasterId;
    }
    public void setWorkTypeMasterId(int workTypeMasterId) {
        WorkTypeMasterId = workTypeMasterId;
    }
    public String getWorkType() {
        return WorkType;
    }
    public void setWorkType(String workType) {
        WorkType = workType;
    }
    public String getRemarks() {
        return Remarks;
    }
    public void setRemarks(String remarks) {
        Remarks = remarks;
    }
    public String getCurrentDate() {
        return currentDate;
    }
    public void setCurrentDate(String currentDate) {
        this.currentDate = currentDate;
    }
    public String getCurrentLocation() {
        return currentLocation;
    }
    public void setCurrentLocation(String currentLocation) {
        this.currentLocation = currentLocation;
    }
    public Double getLatitude() {
        return latitude;
    }
    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }
    public Double getLongitude() {
        return longitude;
    }
    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }


    public String getSerial() {
        return Serial;
    }

    public void setSerial(String serial) {
        Serial = serial;
    }

    public String getSetItem() {
        return SetItem;
    }

    public void setSetItem(String setItem) {
        SetItem = setItem;
    }
}


