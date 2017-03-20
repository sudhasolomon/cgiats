package com.uralian.cgiats.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author Christian Rebollar
 */
public class JobViewOrder 
{
	
	private String month;

	
	private String year;

	
	private int openCount;

	
	private int closedCount;

	public String getMonth() {
		return month;
	}

	public void setMonth(String month) {
		this.month = month;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public int getOpenCount() {
		return openCount;
	}

	public void setOpenCount(int openCount) {
		this.openCount = openCount;
	}

	public int getClosedCount() {
		return closedCount;
	}

	public void setClosedCount(int closedCount) {
		this.closedCount = closedCount;
	}

	@Override
	public String toString() {
		return "JobViewOrder [month=" + month + ", year=" + year
				+ ", openCount=" + openCount + ", closedCount=" + closedCount
				+ "]";
	}

	
}