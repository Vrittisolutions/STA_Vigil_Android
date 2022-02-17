package com.beanclasses;

public class GPSLocationTimeBean {

	private String GPSID = "";
	private String MobileNo = "";
	private String latitude = "";
	private String longitude = "";
	private String locationName = "";
	private String AddedDt = "";
	private String num = "";

	public GPSLocationTimeBean(String GPSID, String MobileNo, String latitude,
			String longitude, String locationName, String AddedDt, String num) {
		this.GPSID = GPSID;
		this.MobileNo = MobileNo;
		this.latitude = latitude;
		this.longitude = longitude;
		this.locationName = locationName;
		this.AddedDt = AddedDt;
		this.num = num;

	}

	/*
	 * public String getGPSID() { return GPSID; } public String getMobileNo() {
	 * return MobileNo; } public String getlatitude() { return latitude; }
	 * public String getlongitude() { return longitude; } public String
	 * getlocationName() { return locationName; } public String getAddedDt() {
	 * return AddedDt; } public String getnum() { return num; }
	 */

	public String getGPSID() {
		return GPSID;
	}

	public void setGPSID(String gPSID) {
		GPSID = gPSID;
	}

	public String getMobileNo() {
		return MobileNo;
	}

	public void setMobileNo(String mobileNo) {
		MobileNo = mobileNo;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getLocationName() {
		return locationName;
	}

	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}

	public String getAddedDt() {
		return AddedDt;
	}

	public void setAddedDt(String addedDt) {
		AddedDt = addedDt;
	}

	public String getNum() {
		return num;
	}

	public void setNum(String num) {
		this.num = num;
	}

}
