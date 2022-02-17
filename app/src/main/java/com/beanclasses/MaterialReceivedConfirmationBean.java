package com.beanclasses;

public class MaterialReceivedConfirmationBean {
	String materialname, reason, reporteename, pkdispatchid, fkorderid, mode,
			docketno, date, imagename, imagepath, mobileno, stationname, stationmasterid, sendername;


	public String getStationmasterid() {
		return stationmasterid;
	}

	public void setStationmasterid(String stationmasterid) {
		this.stationmasterid = stationmasterid;
	}

	public String getSendername() {
		return sendername;
	}

	public void setSendername(String sendername) {
		this.sendername = sendername;
	}

	public void setStationname(String stationname) {this.stationname = stationname;	}

	public String getStationname() {return stationname;	}

	public String getMaterialname() {
		return materialname;
	}

	public void setMaterialname(String materialname) {
		this.materialname = materialname;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getReporteename() {
		return reporteename;
	}

	public void setReporteename(String reporteename) {
		this.reporteename = reporteename;
	}

	public String getPkdispatchid() {
		return pkdispatchid;
	}

	public void setPkdispatchid(String pkdispatchid) {
		this.pkdispatchid = pkdispatchid;
	}

	public String getFkorderid() {
		return fkorderid;
	}

	public void setFkorderid(String fkorderid) {
		this.fkorderid = fkorderid;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public String getDocketno() {
		return docketno;
	}

	public void setDocketno(String docketno) {
		this.docketno = docketno;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getImagename() {
		return imagename;
	}

	public void setImagename(String imagename) {
		this.imagename = imagename;
	}

	public String getImagepath() {
		return imagepath;
	}

	public void setImagepath(String imagepath) {
		this.imagepath = imagepath;
	}

	public String getMobileno() {
		return mobileno;
	}

	public void setMobileno(String mobileno) {
		this.mobileno = mobileno;
	}

}
