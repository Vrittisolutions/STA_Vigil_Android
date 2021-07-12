package com.beanclasses;

public class AdvFirstPlayClipRprt {

    String StationName = "", InstallationID = "", FileName = "", Remark="", ScheduleTime="", FirstPlayTime="", AdvertisementDesc="", DayCount ="" ;
    boolean IsDownloaded;
    String Schedule_date="", Schedule_time_date="", Actual_time_date="", AmPm = "";
    String SchDate_tosort = "";

    public String getStationName() {   return StationName;    }

    public void setStationName(String stationName) {    StationName = stationName;    }

    public String getInstallationID() {   return InstallationID;    }

    public void setInstallationID(String installationID) {    InstallationID = installationID;    }

    public String getFileName() {   return FileName;    }

    public void setFileName(String fileName) {        FileName = fileName;    }

    public String getRemark() {        return Remark;    }

    public void setRemark(String remark) {        Remark = remark;    }

    public String getScheduleTime() {        return ScheduleTime;    }

    public void setScheduleTime(String scheduleTime) {        ScheduleTime = scheduleTime;    }

    public String getFirstPlayTime() {        return FirstPlayTime;    }

    public void setFirstPlayTime(String firstPlayTime) {        FirstPlayTime = firstPlayTime;    }

    public boolean isDownloaded() {        return IsDownloaded;    }

    public void setDownloaded(boolean downloaded) {        IsDownloaded = downloaded;    }

    public String getSchedule_date() {   return Schedule_date;    }

    public void setSchedule_date(String schedule_date) {    Schedule_date = schedule_date;    }

    public String getSchedule_time_date() {      return Schedule_time_date;   }

    public void setSchedule_time_date(String schedule_time_date) {    Schedule_time_date = schedule_time_date;    }

    public String getActual_time_date() {     return Actual_time_date;    }

    public void setActual_time_date(String actual_time_date) {     Actual_time_date = actual_time_date;    }

    public String getAmPm() {     return AmPm;   }

    public void setAmPm(String amPm) {     AmPm = amPm;   }

    public String getAdvertisementDesc() {        return AdvertisementDesc;    }

    public void setAdvertisementDesc(String advertisementDesc) {        AdvertisementDesc = advertisementDesc;    }

    public String getDayCount() {      return DayCount;    }

    public void setDayCount(String dayCount) {        DayCount = dayCount;    }

    public String getSchDate_tosort() {
        return SchDate_tosort;
    }

    public void setSchDate_tosort(String schDate_tosort) {
        SchDate_tosort = schDate_tosort;
    }
}
