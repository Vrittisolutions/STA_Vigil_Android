package com.beanclasses;

public class SuspectedHelper implements Comparable<SuspectedHelper>{
	
	private String Advertisementcode = "";

	private String AdvertisementName = "";
	private String StationName = "";
	private String InstalationId = "";
	private String EffectiveDateFrom = "";
	private String EffectiveDateTo = "";
	private String DayRepeatitions = "";
	private String ActRept = "";
	private String StationSpots = "";
	private String TotalSpot = "";
	private String  ActRepeatitions="";
	private String SpotWisePercentage = "";
	private String Percentage = "";

private String suscount = "";
private String dates="";
private String dateday="";
private String times="";

//public String settimefrom(String timefrom) {
//	return this.timefrom = timefrom;
//}
//
//public String gettimefrom() {
//	return timefrom;
//}
//
//public String settimeto(String timeto) {
//	return this.timeto = timeto;
//}
//
//public String gettimeto() {
//	return timeto;
//}
//	

public String setdates(String dates) {
	return this.dates = dates;
}

public String getdates() {
	return dates;
}

public String setdateday(String dateday) {
	return this.dateday = dateday;
}

public String getdateday() {
	return dateday;
}

public String settimes(String times) {
	return this.times = times;
}

public String gettimes() {
	return times;
}

public String setAdvertisementcode(String Advertisementcode) {
	return this.Advertisementcode = Advertisementcode;
}

public String getAdvertisementcode() {
	return Advertisementcode;
}
	public String setsuscount(String suscount) {
		return this.suscount = suscount;
	}

	public String getsuscount() {
		return suscount;
	}

//	public void setcsId(String CSid) {
//		this.CSid = CSid;
//	}
//
//	public String getcsId() {
//		return CSid;
//	}

	public String setAdvertisementName(String AdvertisementName) {
		return this.AdvertisementName = AdvertisementName;
	}

	public String getAdvertisementName() {
		return AdvertisementName;
	}

	public String setStationName(String StationName) {
		return this.StationName = StationName;
	}

	public String getStationName() {
		return StationName;
	}

	public void setInstalationId(String InstalationId) {
		this.InstalationId = InstalationId;
	}

	public String getInstalationId() {
		return InstalationId;
	}

	public void setEffectiveDateFrom(String EffectiveDateFrom) {
		this.EffectiveDateFrom = EffectiveDateFrom;
	}

	public String getEffectiveDateFrom() {
		return EffectiveDateFrom;
	}

	public void setEffectiveDateTo(String EffectiveDateTo) {
		this.EffectiveDateTo = EffectiveDateTo;
	}

	public String getEffectiveDateTo() {
		return EffectiveDateTo;
	}

	
	public void setDayRepeatitions(String DayRepeatitions) {
		this.DayRepeatitions = DayRepeatitions;
	}

	public String getDayRepeatitions() {
		return DayRepeatitions;
	}
	
	public void setActRepeatitions(String ActRepeatitions) {
		this.ActRepeatitions = ActRepeatitions;
	}

	public String getActRepeatitions() {
		return ActRepeatitions;
	}
	public void setStationSpots(String StationSpots) {
		this.StationSpots = StationSpots;
	}

	public String getStationSpots() {
		return StationSpots;
	}
	public void setTotalSpot(String TotalSpot){
		this.TotalSpot = TotalSpot;
	}

	public String getTotalSpot() {
		return TotalSpot;
	}
	
	public void setSpotWisePercentage(String SpotWisePercentage){
		this.SpotWisePercentage = SpotWisePercentage;
	}

	public String getSpotWisePercentage() {
		return SpotWisePercentage;
	}

	
	public void setPercentage(String Percentage){
		this.Percentage = Percentage;
	}

	public String getPercentage() {
		return Percentage;
	}

	@Override
	public int compareTo(SuspectedHelper u) {
		if (getSpotWisePercentage() == null || u.getSpotWisePercentage() == null) {
			return 0;
		}
		return getSpotWisePercentage().compareTo(u.getSpotWisePercentage());
	}
}
