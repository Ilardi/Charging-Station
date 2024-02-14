package com.project.model;


public class Address {

    private String AddressTitle;
    private String AddressLine;
    private String Town;
    private String StateOrProvince;
    private String PostCode;
    private double Latitude;
    private double Longitude;
    private String CountryIsoCode;
    private String ContinentCode;
    
	public String getAddressTitle() {
		return AddressTitle;
	}
	public void setAddressTitle(String addressTitle) {
		this.AddressTitle = addressTitle;
	}
	public String getAddressLine() {
		return AddressLine;
	}
	public void setAddressLine(String addressLine) {
		this.AddressLine = addressLine;
	}
	public String getTown() {
		return Town;
	}
	public void setTown(String town) {
		this.Town = town;
	}
	public String getStateOrProvince() {
		return StateOrProvince;
	}
	public void setStateOrProvince(String stateOrProvince) {
		this.StateOrProvince = stateOrProvince;
	}
	public String getPostCode() {
		return PostCode;
	}
	public void setPostCode(String postCode) {
		this.PostCode = postCode;
	}
	public double getLatitude() {
		return Latitude;
	}
	public void setLatitude(double latitude) {
		this.Latitude = latitude;
	}
	public double getLongitude() {
		return Longitude;
	}
	public void setLongitude(double longitude) {
		this.Longitude = longitude;
	}
	public String getCountryIsoCode() {
		return CountryIsoCode;
	}
	public void setCountryIsoCode(String countryIsoCode) {
		this.CountryIsoCode = countryIsoCode;
	}
	public String getContinentCode() {
		return ContinentCode;
	}
	public void setContinentCode(String continentCode) {
		this.ContinentCode = continentCode;
	}
    
    
    
}
