package com.beanclasses;

public class PendingStateList {
	String StateName;
	int scount;
	String ServerTime;
	String StationName;
	String Installation;
	String AddedDate;
	String ClrDate;
	String remark;


	public String getServerTime() {
		return ServerTime;
	}

	public void setServerTime(String serverTime) {
		ServerTime = serverTime;
	}

	public PendingStateList() {
	}

	public String GetStateName() {
		return StateName;
	}

	public void SetStateName(String s) {
		this.StateName = s;
	}

	public int GetSCount() {
		return scount;
	}

	public void SetCount(int s) {
		this.scount = s;
	}

	public String getStationName() {
		return StationName;
	}

	public void setStationName(String stationName) {
		StationName = stationName;
	}

	public String getAddedDate() {
		return AddedDate;
	}

	public void setAddedDate(String addedDate) {
		AddedDate = addedDate;
	}

	public String getClrDate() {
		return ClrDate;
	}

	public void setClrDate(String clrDate) {
		ClrDate = clrDate;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getInstallation() {
		return Installation;
	}

	public void setInstallation(String installation) {
		Installation = installation;
	}
	
	
}
