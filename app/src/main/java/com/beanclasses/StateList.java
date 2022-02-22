package com.beanclasses;

import java.util.Date;

public class StateList {
	String Networkcode;
	String InstallationId;
	String StatioName;
	String StateName;
	String AllStation;
	String Diff;
	int count, TotDisconCnt, TotDiscn_30minCntinst;
	int scount;
	String StnSupName;
	String AlrtAddedByName, AlertDesc;
	String AdsDesc, AdsCode, AddedDate, clrDate, Status;
	String lsecdate, lsectime, lsecampm, DownTime, StartTime, EndTime;
	String LS_EC_Key;
	String notificationCnt = "0";
	String userId = "";
	String alertId="";
	String AlertType="";


	public String getAlertType() {
		return AlertType;
	}

	public void setAlertType(String alertType) {
		AlertType = alertType;
	}

	public Date getDdate() {
		return ddate;
	}

	public void setDdate(Date ddate) {
		this.ddate = ddate;
	}

	Date ddate;

	public String getLS_EC_Key() { return LS_EC_Key; }
	public void setLS_EC_Key(String LS_EC_Key) { this.LS_EC_Key = LS_EC_Key; }

	public int getTotDisconCnt() { return TotDisconCnt;	}
	public void setTotDisconCnt(int totDisconCnt) {	TotDisconCnt = totDisconCnt;}

	public int getTotDiscn_30minCntinst() { return TotDiscn_30minCntinst; }
	public void setTotDiscn_30minCntinst(int totDiscn_30minCntinst) { TotDiscn_30minCntinst = totDiscn_30minCntinst; }

	public String getDiff() {
		return Diff;
	}
	public void setDiff(String diff) {
		Diff = diff;
	}
	
	public String getAlertDesc() {
		return AlertDesc;
	}
	public void setAlertDesc(String AlertDesc) {
		this.AlertDesc = AlertDesc;
	}

	public String getDownTime() {return DownTime; }
	public void setDownTime(String downTime) {DownTime = downTime; }

	public String getStartTime() {return StartTime; }
	public void setStartTime(String startTime) {StartTime = startTime; }

	public String getEndTime() {return EndTime; }
	public void setEndTime(String endTime) {EndTime = endTime; }

	public String getLsecdate() {return lsecdate;	}
	public void setLsecdate(String lsecdate) { this.lsecdate = lsecdate; }

	public String getLsectime() { return lsectime; }
	public void setLsectime(String lsectime) { this.lsectime = lsectime; }

	public String getLsecampm() { return lsecampm; }
	public void setLsecampm(String lsecampm) { this.lsecampm = lsecampm; }

	public String getStnSupName() {
		return StnSupName;
	}
	public void setStnSupName(String StnSupName) {
		this.StnSupName = StnSupName;
	}
	
	public String getAlrtAddedByName() {
		return AlrtAddedByName;
	}
	public void setAlrtAddedByName(String AlrtAddedByName) {
		this.AlrtAddedByName = AlrtAddedByName;
	}

	public StateList() {
	}

	public void setInstallationId(String InstallationId) {
		this.InstallationId = InstallationId;
	}

	public String getInstallationId() {
		return InstallationId;
	}

	public String getStatioName() { return StatioName; }

	public void setStatioName(String statioName) {
		StatioName = statioName;
	}

	public void SetNetworkCode(String Networkcode) {
		this.Networkcode = Networkcode;
	}

	public String getNetworkcode() {
		return Networkcode;
	}

	public void Setcount(int count) {this.count = count;}

	public int Getcount() {	return count;}

	public String getAllStation() {
		return AllStation;
	}

	public void setAllStation(String allStation) {
		AllStation = allStation;
	}

	public String getClrDate() {
		return clrDate;
	}

	public void setClrDate(String clrDate) {
		this.clrDate = clrDate;
	}

	public String GetAdsDesc() {
		return AdsDesc;
	}

	public void SetAdsDesc(String s) {
		this.AdsDesc = s;
	}

	public String GetAdsCode() {
		return AdsCode;
	}

	public void SetAdsCode(String s) {
		this.AdsCode = s;
	}

	public void SetAddedDate(String s) {
		this.AddedDate = s;
	}

	public String GetAddedDate() {
		return AddedDate;
	}

	public void SetStatus(String s) {
		this.Status = s;
	}

	public String GetStatus() {
		return Status;
	}

	public String getStateName() {
		return StateName;
	}

	public void setStateName(String stateName) {
		StateName = stateName;
	}

	public int GetSCount() {
		return scount;
	}

	public void SetCount(int s) {
		this.scount = s;
	}

	public String getNotificationCnt() {
		return notificationCnt;
	}

	public void setNotificationCnt(String notificationCnt) {
		this.notificationCnt = notificationCnt;
	}

	public String getUserId() { return userId; }

	public void setUserId(String userId) { this.userId = userId; }

	public String getAlertId() {
		return alertId;
	}

	public void setAlertId(String alertId) {
		this.alertId = alertId;
	}
}
