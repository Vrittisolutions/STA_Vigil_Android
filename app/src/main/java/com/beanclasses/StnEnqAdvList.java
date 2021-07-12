package com.beanclasses;

public class StnEnqAdvList {
	private String InstallationDesc;
	private String InstallationCount;
	private String LastServerTime;
	private String AudioOutPut;

	public StnEnqAdvList(String InstallationDesc, String InstallationCount, String LastServerTime, String AudioOutPut)
	{
		this.InstallationDesc=InstallationDesc;
		this.InstallationCount=InstallationCount;
		this.LastServerTime=LastServerTime;
		this.AudioOutPut=AudioOutPut;
	}
	public String getInstallationDesc()
	{
		return InstallationDesc;
	}
	public String getInstallationCount()
	{
		return InstallationCount;
	}
	public String getLastServerTime()
	{
		return LastServerTime;
	}
	public String getAudioOutPut()
	{
		return AudioOutPut;
	}
}
