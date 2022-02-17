package com.beanclasses;

public class SoundLevelBeanSort {

	String InstallationId="";
	String StationName="";
	String CallibrationDate="";
	String ScheduleTime="";
	String AO="";
	String Standard="";
	String Actual="";
	String Percentage="";
	String NetworkCode="";
	String ServerTime="";
	String Rank="";
	String ScheduleDate="";

	public SoundLevelBeanSort() {
		// TODO Auto-generated constructor stub
	}
	

	

	public SoundLevelBeanSort(String InstallationId, String StationName,
			String CallibrationDate, String ScheduleTime, String AO,
			String Standard, String Actual, String Percentage,
			String NetworkCode, String ServerTime, String Rank,
			String ScheduleDate) {
		// TODO Auto-generated constructor stub

		this.InstallationId = InstallationId;
		this.StationName = StationName;
		this.CallibrationDate = CallibrationDate;
		this.ScheduleTime = ScheduleTime;
		this.AO = AO;
		this.Standard = Standard;
		this.Actual = Actual;
		this.Percentage = Percentage;
		this.NetworkCode = NetworkCode;
		this.ServerTime = ServerTime;
		this.Rank = Rank;
		this.ScheduleDate = ScheduleDate;
	}

	public String getInstallationId() {
		return InstallationId;
	}

	public void setInstallationId(String installationId) {
		this.InstallationId = installationId;
	}

	public String getStationName() {
		return StationName;
	}

	public void setStationName(String stationName) {
		this.StationName = stationName;
	}

	public String getCallibrationDate() {
		return CallibrationDate;
	}

	public void setCallibrationDate(String callibrationDate) {
		this.CallibrationDate = callibrationDate;
	}

	public String getScheduleTime() {
		return ScheduleTime;
	}

	public void setScheduleTime(String scheduleTime) {
		this.ScheduleTime = scheduleTime;
	}

	public String getAO() {
		return AO;
	}

	public void setAO(String aO) {
		this.AO = aO;
	}

	public String getStandard() {
		return Standard;
	}

	public void setStandard(String standard) {
		this.Standard = standard;
	}

	public String getActual() {
		return Actual;
	}

	public void setActual(String actual) {
		this.Actual = actual;
	}

	public String getPercentage() {
		return Percentage;
	}

	public void setPercentage(String percentage) {
		this.Percentage = percentage;
	}

	public String getNetworkCode() {
		return NetworkCode;
	}

	public void setNetworkCode(String networkCode) {
		this.NetworkCode = networkCode;
	}

	public String getServerTime() {
		return ServerTime;
	}

	public void setServerTime(String serverTime) {
		this.ServerTime = serverTime;
	}

	public String getRank() {
		return Rank;
	}

	public void setRank(String rank) {
		this.Rank = rank;
	}
	public String getScheduleDate() {
		return ScheduleDate;
	}

	public void setScheduleDate(String scheduleDate) {
		this.ScheduleDate = scheduleDate;
	}

}
