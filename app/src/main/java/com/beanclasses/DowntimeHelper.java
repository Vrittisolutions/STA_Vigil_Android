package com.beanclasses;

public class DowntimeHelper {

	private String stnname = "";
	private String current = "";
	private String lastseven = "";
	private String lastthirty = "";
	
	private String dateDay = "";
	public void setdateDay(String dateDay) {
		this.dateDay = dateDay;
	}

	public String getdateDay() {
		return dateDay;
	}
	
	public void setstnname(String stnname) {
		this.stnname = stnname;
	}

	public String getstnname() {
		return stnname;
	}

	public void setcurrent(String current) {
		this.current = current;
	}

	public String getcurrent() {
		return current;
	}

	public void setlastseven(String lastseven) {
		this.lastseven = lastseven;
	}

	public String getlastseven() {
		return lastseven;
	}

	public void setlastthirty(String lastthirty) {
		this.lastthirty = lastthirty;
	}

	public String getlastthirty() {
		return lastthirty;
	}
	
	

	
}
