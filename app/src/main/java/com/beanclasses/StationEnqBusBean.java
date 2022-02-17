package com.beanclasses;

public class StationEnqBusBean {
	
	String Date;
	int busCnt;
	String Reportingtime, Source, Destination,busno="";

	public String getDate() { return Date; }
	public void setDate(String date) {
		Date = date;
	}
	public int getBusCnt() {
		return busCnt;
	}
	public void setBusCnt(int busCnt) {
		this.busCnt = busCnt;
	}

	public String getReportingtime() {
		return Reportingtime;
	}

	public void setReportingtime(String reportingtime) {
		Reportingtime = reportingtime;
	}

	public String getSource() {
		return Source;
	}

	public void setSource(String source) {
		Source = source;
	}

	public String getDestination() {
		return Destination;
	}

	public void setDestination(String destination) {
		Destination = destination;
	}

	public String getBusno() { return busno; }

	public void setBusno(String busno) { this.busno = busno; }
}
