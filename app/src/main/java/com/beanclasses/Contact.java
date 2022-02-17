package com.beanclasses;

public class Contact {

	String _serialNumber;
	String _phone_number;
	String _number;
	String _environment;
	String _url;
	String _plant;

	// Empty constructor
	public Contact() {

	}

	// constructor
	public Contact(String serialNumber, String phone_number, String number,
			String url, String environment, String plant) {

		this._serialNumber = serialNumber;
		this._phone_number = phone_number;
		this._number = number;
		this._url = url;
		this._environment = environment;
		this._plant = plant;
	}

	public String getSerailNumber() {
		return this._serialNumber;
	}

	// setting name
	public void setSerialNumber(String serialNumber) {
		this._serialNumber = serialNumber;
	}

	// getting phone number
	public String getPhoneNumber() {
		return this._phone_number;
	}

	// setting phone number
	public void setPhoneNumber(String phone_number) {
		this._phone_number = phone_number;
	}

	public String getNumber() {
		return this._number;
	}

	// setting phone number
	public void setNumber(String number) {
		this._number = number;
	}

	// /////////////////////

	public String geturl() {
		return this._url;
	}

	// setting name
	public void seturl(String url) {
		this._url = url;
	}

	// getting phone number
	public String getenvironment() {
		return this._environment;
	}

	// setting phone number
	public void setenvironment(String environment) {
		this._environment = environment;
	}

	public String getplant() {
		return this._plant;
	}

	// setting phone number
	public void setplant(String plant) {
		this._plant = plant;
	}

}
