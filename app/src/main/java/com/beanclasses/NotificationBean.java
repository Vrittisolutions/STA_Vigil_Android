package com.beanclasses;

/**
 * Created by Admin-3 on 7/28/2017.
 */

public class NotificationBean {


    String notificationNumber, InstallationId, StationName, AddedDt, Message, NetworkCode, FormattedAddedDt;

    public NotificationBean(String StationName) {
        this.StationName=StationName;
        // TODO Auto-generated constructor stub
    }

    public NotificationBean() {
        // TODO Auto-generated constructor stub
    }

    public String getNotificationNumber() {
        return notificationNumber;
    }

    public void setNotificationNumber(String notificationNumber) {
        this.notificationNumber = notificationNumber;
    }

    public String getNetworkCode() {
        return NetworkCode;
    }

    public void setNetworkCode(String NetworkCode) {
        this.NetworkCode = NetworkCode;
    }

    public String getInstallationId() {
        return InstallationId;
    }

    public void setInstallationId(String installationId) {
        InstallationId = installationId;
    }

    public String getStationName() {
        return StationName;
    }

    public void setStationName(String stationName) {
        StationName = stationName;
    }

    public String getAddedDt() {
        return AddedDt;
    }

    public void setAddedDt(String addedDt) {
        AddedDt = addedDt;
    }

    public String getFormattedAddedDt() {
        return FormattedAddedDt;
    }

    public void setFormattedAddedDt(String FormattedAddedDt) {  this.FormattedAddedDt = FormattedAddedDt;    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }
}
