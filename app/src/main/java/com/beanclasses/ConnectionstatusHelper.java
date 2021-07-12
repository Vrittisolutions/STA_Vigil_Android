package com.beanclasses;

public class ConnectionstatusHelper {

	private String CSid = "";
	private String InstallationId = "";
	private String ServerTime = "";
	private String StartTime = "";
	private String EndTime = "";
	private String StartEnd = "";
	private String Remarks = "";
	private String status = "";
	private String version = "";
	private String tymdiff = "";
	private String dateDay = "";
	private String reason = "";
	private String personDetails = "";
	private String personnumber = "";
//	public void setcsId(String CSid) {
//		this.CSid = CSid;
//	}
//
//	public String getcsId() {
//		return CSid;
//	}
	
	public String setpersonnumber(String personnumber) {
		return this.personnumber = personnumber;
	}

	public String getpersonnumber() {
		return personnumber;
	}

	
	public String setpersonDetails(String personDetails) {
		return this.personDetails = personDetails;
	}

	public String getpersonDetails() {
		return personDetails;
	}

	public String setinstallationId(String InstallationId) {
		return this.InstallationId = InstallationId;
	}

	public String getinstallationId() {
		return InstallationId;
	}
	
	public String setreason(String reason) {
		return this.reason = reason;
	}

	public String getreason() {
		return reason;
	}
	
	public void setservertime(String ServerTime) {
		this.ServerTime = ServerTime;
	}

	public String getservertime() {
		return ServerTime;
	}
	public void setdateDay(String dateDay) {
		this.dateDay = dateDay;
	}

	public String getdateDay() {
		return dateDay;
	}

	public void setStartTime(String StartTime) {
		this.StartTime = StartTime;
	}

	public String getStartTime() {
		return StartTime;
	}

	public void setEndTime(String EndTime) {
		this.EndTime = EndTime;
	}

	public String getEndTime() {
		return EndTime;
	}

	public void setStartEnd(String StartEnd) {
		this.StartEnd = StartEnd;
	}

	public String getStartEnd() {
		return StartEnd;
	}

	
	public void setRemarks(String Remarks) {
		this.Remarks = Remarks;
	}

	public String getRemarks() {
		return Remarks;
	}
	public void setStatus(String status) {
		this.status = status;
	}

	public String getStatus() {
		return status;
	}
	public void setVersion(String version){
		this.version = version;
	}

	public String getVersion() {
		return version;
	}
	
	public void settymdiff(String tymdiff){
		this.tymdiff = tymdiff;
	}

	public String gettymdiff() {
		return tymdiff;
	}

}
