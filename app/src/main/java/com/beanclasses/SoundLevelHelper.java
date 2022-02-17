package com.beanclasses;

public class SoundLevelHelper {

	private String InstallationId = "";
	private String StationName = "";
	private String ScheduletimeDate="";
	private String ScheduletimeTime="";
	private String audioOutput = "";
	private String Standard = "";
	private String Actual = "";
	private String Percentage = "";
	


//	public void setcsId(String CSid) {
//		this.CSid = CSid;
//	}
//
//	public String getcsId() {
//		return CSid;
//	}

	public String setInstallationId(String InstallationId) {
		return this.InstallationId = InstallationId;
	}

	public String getInstallationId() {
		return InstallationId;
	}

	public void setStationName(String StationName) {
		this.StationName = StationName;
	}

	public String getStationName() {
		return StationName;
	}

	public void setScheduleDate(String ScheduleDate) {
		this.ScheduletimeDate = ScheduleDate;
	}

	public String getScheduleDate() {
		return ScheduletimeDate;
	}

	public void setScheduleTime(String ScheduleTime) {
		this.ScheduletimeTime = ScheduleTime;
	}

	public String getScheduleTime() {
		return ScheduletimeTime;
	}

	public void setaudioOutput(String audioOutput) {
		this.audioOutput = audioOutput;
	}

	public String getaudioOutput() {
		return audioOutput;
	}

	
	public void setStandard(String Standard) {
		this.Standard = Standard;
	}

	public String getStandard() {
		return Standard;
	}
	
	public void setActual(String Actual) {
		this.Actual = Actual;
	}

	public String getActual() {
		return Actual;
	}

	
	public void setPercentage(String Percentage){
		this.Percentage = Percentage;
	}

	public String getPercentage() {
		return Percentage;
	}

}
