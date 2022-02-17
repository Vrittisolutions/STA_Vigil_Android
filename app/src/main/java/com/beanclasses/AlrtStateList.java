package com.beanclasses;

public class AlrtStateList {
	String Networkcode;
	String InstallationId;
	String StatioName;
	String AllStation;
	int count,ocount=0;
	String AdsDesc, AdsCode, AddedDate, clrDate, Status, token = null;

	public AlrtStateList() {
	}

	public void setInstallationId(String InstallationId) {
		this.InstallationId = InstallationId;
	}

	public String getInstallationId() {
		return InstallationId;
	}

	public String getStatioName() {
		return StatioName;
	}

	public void setStatioName(String statioName) {
		StatioName = statioName;
	}

	public void SetNetworkCode(String Networkcode) {
		this.Networkcode = Networkcode;
	}

	public String getNetworkcode() {
		return Networkcode;
	}

	public void Setcount(int count) {
		this.count = count;
	}

	public int Getcount() {
		return count;
	}

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

	public void Setact(String s) {
		this.token = s;
	}

	public String Getact() {
		return token;
	}

	public void SetStatus(String s) {
		this.Status = s;
	}

	public String GetStatus() {
		return Status;
	}

	public int GetOverdueCnt() {
		return ocount;
	}

	public void SetOverdueCnt(int o) {
		this.ocount = o;
	}

}
