/**
 * 
 */
package com.uralian.cgiats.util;

/**
 * @author skurapati
 *
 */
public enum WorkStatusEnum {
	USCITIZEN("US Citizen", "US Citizen"), HAVEVISA("Have H1 Visa", "H1B Visa"), GREENCARD("Green Card Holder",
			"Green Card"), NEEDH1VISA("Need H1 Visa Sponsor", "Not Available"), NOTSPECIFIED("Not Specified", "Not Available"), TNPERMITTED("TN Permit Holder",
					"TN Visa"), EMPAUTHORIZATION("Employment Authorization Document", "EAD");
	private String cbType, dbType;

	WorkStatusEnum(String cbType, String dbType) {
		this.cbType = cbType;
		this.dbType = dbType;
	}

	public String getCBType() {
		return this.cbType;
	}

	public String getDBType() {
		return this.dbType;
	}
}
