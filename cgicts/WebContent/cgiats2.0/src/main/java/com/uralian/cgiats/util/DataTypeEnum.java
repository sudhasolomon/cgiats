/**
 * 
 */
package com.uralian.cgiats.util;

/**
 * @author Sreenath
 *
 */
public enum DataTypeEnum {
	INTEGER("INTEGER"), STRING("STRING"), DOUBLE("DOUBLE");
	private String strValue;

	DataTypeEnum(String str) {
		this.strValue = str;
	}

	public String getValue() {
		return strValue;
	}

}
