package com.project.model;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("Station")
public class ChargingStation {
	
	@Id
	private Integer id;
	private String NameDataProvider;
	private String URLDataProvider;
	private String NameOperatorInfo;
	private String URLOperatorInfo;
	private String UsageType;
	private Boolean MembershipRequired;
	private Boolean StatusType;
	private String MediaItemURL;
	private Boolean RecentlyVerified;
    private String uuid;
    private String UsageCost;
    private Address AddressInfo;
    private List<Connection> ConnectionInfo;
    private Integer NumberOfStation;
    private String DateLastStatusUpdate;
    private String DateCreated;
    
    

	public ChargingStation() {
	}

	public ChargingStation(Integer id, String NameDataProvider, String URLDataProvider, String NameOperatorInfo, String URLOperatorInfo, String UsageType, Boolean MembershipRequired, Boolean StatusType, String MediaItemURL, Boolean RecentlyVerified, String uuid, String UsageCost, Integer NumberOfStation, String dateLastStatusUpdate, String dateCreated, Address AddressInfo,List<Connection> ConnectionInfo) {
		this.id = id;
		this.NameDataProvider = NameDataProvider;
		this.URLDataProvider = URLDataProvider;
		this.NameOperatorInfo = NameOperatorInfo;
		this.URLOperatorInfo = URLOperatorInfo;
		this.UsageType = UsageType;
		this.MembershipRequired = MembershipRequired;
		this.StatusType = StatusType;
		this.MediaItemURL = MediaItemURL;
		this.RecentlyVerified = RecentlyVerified;
		this.uuid = uuid;
		this.UsageCost = UsageCost;
		this.AddressInfo = AddressInfo;
		this.ConnectionInfo = ConnectionInfo;
		this.NumberOfStation = NumberOfStation;
		this.DateLastStatusUpdate = dateLastStatusUpdate;
		this.DateCreated = dateCreated;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getNameDataProvider() {
		return NameDataProvider;
	}

	public void setNameDataProvider(String nameDataProvider) {
		NameDataProvider = nameDataProvider;
	}

	public String getURLDataProvider() {
		return URLDataProvider;
	}

	public void setURLDataProvider(String uRLDataProvider) {
		URLDataProvider = uRLDataProvider;
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

	public String getUsageType() {
		return UsageType;
	}

	public void setUsageType(String usageType) {
		UsageType = usageType;
	}

	public Boolean isMembershipRequired() {
		if(MembershipRequired != null)
			return MembershipRequired;
		else
			return false;
	}

	public void setMembershipRequired(Boolean membershipRequired) {
		MembershipRequired = membershipRequired;
	}

	public Boolean isStatusType() {
		if(StatusType != null)
			return StatusType;
		else
			return false;
	}

	public void setStatusType(Boolean statusType) {
		StatusType = statusType;
	}

	public String getMediaItemURL() {
		return MediaItemURL;
	}

	public void setMediaItemURL(String mediaItemURL) {
		MediaItemURL = mediaItemURL;
	}

	public Boolean isRecentlyVerified() {
		return RecentlyVerified;
	}

	public void setRecentlyVerified(Boolean recentlyVerified) {
		this.RecentlyVerified = recentlyVerified;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getUsageCost() {
		return UsageCost;
	}

	public void setUsageCost(String usageCost) {
		UsageCost = usageCost;
	}

	public Integer getNumberOfStation() {
		if(NumberOfStation != null)
			return NumberOfStation;
		else
			return 0;
	}

	public void setNumberOfStation(Integer numberOfStation) {
		NumberOfStation = numberOfStation;
	}

	public String getDateLastStatusUpdate() {
		return DateLastStatusUpdate;
	}

	public void setDateLastStatusUpdate(String dateLastStatusUpdate) {
		this.DateLastStatusUpdate = dateLastStatusUpdate;
	}

	public String getDateCreated() {
		return DateCreated;
	}

	public void setDateCreated(String dateCreated) {
		this.DateCreated = dateCreated;
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
	
}




