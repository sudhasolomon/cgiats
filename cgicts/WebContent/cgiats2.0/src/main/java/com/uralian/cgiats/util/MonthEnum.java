/**
 * 
 */
package com.uralian.cgiats.util;

/**
 * @author skurapati
 *
 */
public enum MonthEnum {
	JAN("Jan", 0), FEB("Feb", 1), MAR("Mar", 2), APR("Apr", 3), MAY("May", 4), JUN("Jun", 5), JUL("Jul", 6), AUG("Aug", 7), SEP("Sep", 8), OCT("Oct",
			9), NOV("Nov", 10), DEC("Dec", 11);
	private String month;
	private Integer index;

	MonthEnum(String month, Integer index) {
		this.month = month;
		this.index = index;
	}

	public String getMonth() {
		return this.month;
	}

	public Integer getMonthIndex() {
		return this.index;
	}
}
