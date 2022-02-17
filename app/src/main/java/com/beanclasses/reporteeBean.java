package com.beanclasses;

public class reporteeBean {
	String ReporteeName;
	String ReporteeID;
	String ReporteeEmail;
	String Reporting_managerName, Reporting_managerID;

	public String getReporting_managerName() { return Reporting_managerName; }
	public void setReporting_managerName(String reporting_managerName) {Reporting_managerName = reporting_managerName; }

	public String getReporting_managerID() { return Reporting_managerID; }
	public void setReporting_managerID(String reporting_managerID) { Reporting_managerID = reporting_managerID;	}

	public String getReporteeName() {
	return ReporteeName;
}
	public void setReporteeName(String reporteeName) {	ReporteeName = reporteeName; }

	public String getReporteeID() {
	return ReporteeID;
}
	public void setReporteeID(String reporteeID) {	ReporteeID = reporteeID; }

	public String getReporteeEmail() { return ReporteeEmail; }
	public void setReporteeEmail(String reporteeEmail) { ReporteeEmail = reporteeEmail;	}
}
