package com.project.model;

public class Connection {
	
	private Integer PowerKW;
	private String ConnectionType;
	private Boolean IsOperationalConnection;
	private Boolean FastChargeCapable;
	private Integer NumberOfConnections;
	
	public Integer getPowerKW() {
		return PowerKW;
	}
	public void setPowerKW(Integer powerKW) {
		this.PowerKW = powerKW;
	}
	public String getConnectionType() {
		return ConnectionType;
	}
	public void setConnectionType(String connectionType) {
		this.ConnectionType = connectionType;
	}
	public Boolean isIsOperationalConnection() {
		return IsOperationalConnection;
	}
	public void setIsOperationalConnection(Boolean isOperationalConnection) {
		this.IsOperationalConnection = isOperationalConnection;
	}
	public Boolean isFastChargeCapable() {
		return FastChargeCapable;
	}
	public void setFastChargeCapable(Boolean fastChargeCapable) {
		this.FastChargeCapable = fastChargeCapable;
	}
	public Integer getNumberOfConnections() {
		return NumberOfConnections;
	}
	public void setNumberOfConnections(Integer numberOfConnections) {
		this.NumberOfConnections = numberOfConnections;
	}
	
	

}
