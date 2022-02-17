package com.beanclasses;

public class NonreportedList {
	private String InstallationDesc;
	private String InstallationCount;
	private String LastServerTime;
	
	public NonreportedList(String InstallationDesc,String InstallationCount,String LastServerTime)
	{
		this.InstallationDesc=InstallationDesc;
		this.InstallationCount=InstallationCount;
		this.LastServerTime=LastServerTime;
	}
	public String getInstallationDesc()
	{
		return InstallationDesc;
	}
	public String getInstallationCount(){ return InstallationCount;	}
	public String getLastServerTime()
	{
		return LastServerTime;
	}
}
