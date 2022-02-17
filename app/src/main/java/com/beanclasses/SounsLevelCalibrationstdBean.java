package com.beanclasses;

public class SounsLevelCalibrationstdBean {
	
	
	String AudioMonitorDetailsID, CallibrationDate, CTIME,Standard, CallibrationVolume,SystemVolume,InstallationId,InstallationDesc;
	public SounsLevelCalibrationstdBean(String CallibrationDate) {
		this.CallibrationDate=CallibrationDate;
		// TODO Auto-generated constructor stub
	}

	public SounsLevelCalibrationstdBean() {
		// TODO Auto-generated constructor stub
	}
		public String getAudioMonitorDetailsID() {
			return AudioMonitorDetailsID;
		}

		public void setAudioMonitorDetailsID(String audioMonitorDetailsID) {
			AudioMonitorDetailsID = audioMonitorDetailsID;
		}

		public String getCallibrationDate() {
			return CallibrationDate;
		}

		public void setCallibrationDate(String callibrationDate) {
			CallibrationDate = callibrationDate;
		}

		public String getCTIME() {
			return CTIME;
		}

		public void setCTIME(String cTIME) {
			CTIME = cTIME;
		}

		public String getStandard() {
			return Standard;
		}

		public void setStandard(String standard) {
			Standard = standard;
		}

		public String getCallibrationVolume() {
			return CallibrationVolume;
		}

		public void setCallibrationVolume(String callibrationVolume) {
			CallibrationVolume = callibrationVolume;
		}

		public String getSystemVolume() {
			return SystemVolume;
		}

		public void setSystemVolume(String systemVolume) {
			SystemVolume = systemVolume;
		}

		public String getInstallationId() {
			return InstallationId;
		}

		public void setInstallationId(String installationId) {
			InstallationId = installationId;
		}

		public String getInstallationDesc() {
			return InstallationDesc;
		}

		public void setInstallationDesc(String installationDesc) {
			InstallationDesc = installationDesc;
		}

}
