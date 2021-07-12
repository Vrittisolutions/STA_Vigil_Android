package com.beanclasses;

public class TvStatusStateList {
	String StateName;
	int scount;
	int col;
	String totaltv = "", TVReason = "", CSNStatus = "";

	public TvStatusStateList() {
	}

	public String GetStateName() {
		return StateName;
	}

	public void SetStateName(String s) {
		this.StateName = s;
	}

	public String Gettotaltv() {
		return totaltv;
	}

	public void Settotaltv(String s) {
		this.totaltv = s;
	}

	public int GetSCount() {
		return scount;
	}

	public void SetCount(int s) {
		this.scount = s;
	}

	public String GetTVReason() { return TVReason; }

	public void SetTVReason(String TVReason) {
		this.TVReason = TVReason;
	}

	public String getCSNStatus() { return CSNStatus; }

	public void setCSNStatus(String CSNStatus) { this.CSNStatus = CSNStatus; }
}
