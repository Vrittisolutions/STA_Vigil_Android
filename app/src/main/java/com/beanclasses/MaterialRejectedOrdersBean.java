package com.beanclasses;

public class MaterialRejectedOrdersBean {
	String PKmaterialId, StationName, Reason, ScrapRepair, ReporteeName,
			AddedBy, AddedtDt, StatusFlag, deliveryDate, senderMobNo,
			rejectedOrder, DispatchedOrder,MaterialName;

	public String getMaterialName() {
		return MaterialName;
	}

	public void setMaterialName(String materialName) {
		MaterialName = materialName;
	}

	public String getPKmaterialId() {
		return PKmaterialId;
	}

	public void setPKmaterialId(String pKmaterialId) {
		PKmaterialId = pKmaterialId;
	}

	public String getStationName() {
		return StationName;
	}

	public void setStationName(String stationName) {
		StationName = stationName;
	}

	public String getReason() {
		return Reason;
	}

	public void setReason(String reason) {
		Reason = reason;
	}

	public String getScrapRepair() {
		return ScrapRepair;
	}

	public void setScrapRepair(String scrapRepair) {
		ScrapRepair = scrapRepair;
	}

	public String getReporteeName() {
		return ReporteeName;
	}

	public void setReporteeName(String reporteeName) {
		ReporteeName = reporteeName;
	}

	public String getAddedBy() {
		return AddedBy;
	}

	public void setAddedBy(String addedBy) {
		AddedBy = addedBy;
	}

	public String getAddedtDt() {
		return AddedtDt;
	}

	public void setAddedtDt(String addedtDt) {
		AddedtDt = addedtDt;
	}

	public String getStatusFlag() {
		return StatusFlag;
	}

	public void setStatusFlag(String statusFlag) {
		StatusFlag = statusFlag;
	}

	public String getDeliveryDate() {
		return deliveryDate;
	}

	public void setDeliveryDate(String deliveryDate) {
		this.deliveryDate = deliveryDate;
	}

	public String getSenderMobNo() {
		return senderMobNo;
	}

	public void setSenderMobNo(String senderMobNo) {
		this.senderMobNo = senderMobNo;
	}

	public String getRejectedOrder() {
		return rejectedOrder;
	}

	public void setRejectedOrder(String rejectedOrder) {
		this.rejectedOrder = rejectedOrder;
	}

	public String getDispatchedOrder() {
		return DispatchedOrder;
	}

	public void setDispatchedOrder(String dispatchedOrder) {
		DispatchedOrder = dispatchedOrder;
	}

	}
