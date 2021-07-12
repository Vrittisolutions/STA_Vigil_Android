package com.beanclasses;

public class StationCall {
	private String name;
	private String number;
	private String SetItem;
	public StationCall() {
		// TODO Auto-generated constructor stub
	}
	public StationCall(String name, String number) {
		this.name = name;
		this.number = number;
	}

	public String getName() {
		return this.name;
	}

	public String getnumber() {
		return this.number;
	}
	
	public String getSetItem() {
		return SetItem;
	}

	public void setSetItem(String setItem) {
		SetItem = setItem;
	}
}
