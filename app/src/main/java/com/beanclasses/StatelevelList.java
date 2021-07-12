package com.beanclasses;

public class StatelevelList {

	String Networkcode;
	String SubNetworkcode;
	String InstallationId;
	String StatioName;
	String TimeData;
	String servertime;
	int count;

	public StatelevelList() {
	}

	public void setInstallationId(String InstallationId) {
		this.InstallationId = InstallationId;
	}

	public String getInstallationId() {
		return InstallationId;
	}

	public void SetNetworkCode(String Networkcode) {
		this.Networkcode = Networkcode;
	}

	public String getNetworkcode() {
		return Networkcode;
	}

	public String getSubNetworkcode() {
		return SubNetworkcode;
	}

	public void setSubNetworkcode(String subNetworkcode) {
		SubNetworkcode = subNetworkcode;
	}

	public void Setcount(int count) {
		this.count = count;
	}

	public int Getcount() {
		return count;
	}

	public String getTimeData() {
		return TimeData;
	}

	public void setTimeData(String timeData) {
		TimeData = timeData;
	}

	public String getStatioName() {
		return StatioName;
	}

	public void setStatioName(String statioName) {
		StatioName = statioName;
	}

	public String getServertime() {
		return servertime;
	}

	public void setServertime(String servertime) {
		this.servertime = servertime;
	}

}
