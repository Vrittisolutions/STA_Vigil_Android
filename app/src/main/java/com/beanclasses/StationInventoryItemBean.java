package com.beanclasses;

public class StationInventoryItemBean {
	String InventoryId, ItemName, InstallationId, SrNo, AddedBy, AddedDt, Mobile, Remarks, IsDeleted;
	
	public String getItemname() {
		return ItemName;
	}
	public void setItemName(String ItemName) {
		this.ItemName = ItemName;
	}
	
	public String getInventoryId() {
		return InventoryId;
	}
	public void setInventoryId(String InventoryId) {
		this.InventoryId = InventoryId;
	}
	
	public String getMobile() {
		return Mobile;
	}
	public void setMobile(String Mobile) {
		this.Mobile = Mobile;
	}
	
	public String getInstallationId() {
		return InstallationId;
	}
	public void setInstallationId(String InstallationId) {
		this.InstallationId = InstallationId;
	}
	
	public String getSrNo() { return SrNo; }
	public void setSrNo(String SrNo) { this.SrNo = SrNo; }
	
	public String getAddedDt() {
		return AddedDt;
	}
	public void setAddedDt(String AddedDT) {
		this.AddedDt = AddedDT;
	}
	
	public String getAddedBy() {
		return AddedBy;
	}
	public void setAddedBy(String AddedBy) {
		this.AddedBy = AddedBy;
	}
	
	public String getReMark() {
		return Remarks;
	}
	public void setReMark(String Remarks) {
		this.Remarks = Remarks;
	}
	
	public String getIsDeleted() {
		return IsDeleted;
	}
	public void setIsDeleted(String IsDeleted) {
		this.IsDeleted = IsDeleted;
	}

}
