package com.beanclasses;

import java.util.Date;

public class NonRepStateBean {

	String StateName;
	int scount;
	int AdCnt;

	public NonRepStateBean() {
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

	public int GetAdCnt() {
		return AdCnt;
	}

	public void SetAdCnt(int s) {
		this.AdCnt = s;
	}

}
