package com.project.model;

import java.util.List;

public class ChargingStationMarker {
	private Integer Id;
    private Address AddressInfo;
    private List<Connection> ConnectionInfo;
    private String NameOperatorInfo;
    private String URLOperatorInfo;
    private String UsageCost;
	private boolean MembershipRequired;
	private boolean StatusType;
	private String MediaItemURL;
	private boolean RecentlyVerified;
	private int NumberOfStation;

    public ChargingStationMarker(Integer Id, Address AddressInfo, List<Connection> ConnectionInfo, String NameOperatorInfo, String URLOperatorInfo, String UsageCost, boolean MembershipRequired, boolean StatusType, String MediaItemURL, boolean RecentlyVerified, int NumberOfStation) {
        this.Id = Id;
    	this.AddressInfo = AddressInfo;
        this.ConnectionInfo = ConnectionInfo;
        this.NameOperatorInfo = NameOperatorInfo;
        this.URLOperatorInfo = URLOperatorInfo;
        this.UsageCost = UsageCost;
        this.MembershipRequired = MembershipRequired;
        this.StatusType = StatusType;
        this.MediaItemURL = MediaItemURL;
        this.RecentlyVerified = RecentlyVerified;
        this.NumberOfStation = NumberOfStation;
    }



	public Address getAddressInfo() {
		return AddressInfo;
	}



	public void setAddressInfo(Address addressInfo) {
		AddressInfo = addressInfo;
	}



	public List<Connection> getConnectionInfo() {
		return ConnectionInfo;
	}



	public void setConnectionInfo(List<Connection> connectionInfo) {
		ConnectionInfo = connectionInfo;
	}

	public Integer getId() {
		return Id;
	}

	public void setId(Integer id) {
		Id = id;
	}

	public String getNameOperatorInfo() {
		return NameOperatorInfo;
	}

	public void setNameOperatorInfo(String nameOperatorInfo) {
		NameOperatorInfo = nameOperatorInfo;
	}

	public String getURLOperatorInfo() {
		return URLOperatorInfo;
	}

	public void setURLOperatorInfo(String uRLOperatorInfo) {
		URLOperatorInfo = uRLOperatorInfo;
	}



	public String getUsageCost() {
		return UsageCost;
	}



	public void setUsageCost(String usageCost) {
		UsageCost = usageCost;
	}



	public boolean isMembershipRequired() {
		return MembershipRequired;
	}



	public void setMembershipRequired(boolean membershipRequired) {
		MembershipRequired = membershipRequired;
	}



	public boolean isStatusType() {
		return StatusType;
	}



	public void setStatusType(boolean statusType) {
		StatusType = statusType;
	}



	public String getMediaItemURL() {
		return MediaItemURL;
	}



	public void setMediaItemURL(String mediaItemURL) {
		MediaItemURL = mediaItemURL;
	}



	public boolean isRecentlyVerified() {
		return RecentlyVerified;
	}



	public void setRecentlyVerified(boolean recentlyVerified) {
		RecentlyVerified = recentlyVerified;
	}



	public int getNumberOfStation() {
		return NumberOfStation;
	}



	public void setNumberOfStation(int numberOfStation) {
		NumberOfStation = numberOfStation;
	}
    
    
}