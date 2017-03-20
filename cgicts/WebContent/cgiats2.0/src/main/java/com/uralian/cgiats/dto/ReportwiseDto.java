package com.uralian.cgiats.dto;

import java.util.List;

public class ReportwiseDto {

	private String year;
	private String month;
	private String week;
	private String dmName;
	private String name;
	private String startDate;
	private String endDate;
	private Boolean isDateRangeYN;
	private List<String> names;
	private String title;
	private String status;
	private List<Integer> years;
	private List<String> months;
	private List<Integer> weeks;
	private List<String> dmNames;
	private List<String> statuses;
	private String location;
	private  List<String> locations;
	
	

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public List<String> getLocations() {
		return locations;
	}

	public void setLocations(List<String> locations) {
		this.locations = locations;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public Boolean getIsDateRangeYN() {
		return isDateRangeYN;
	}

	public void setIsDateRangeYN(Boolean isDateRangeYN) {
		this.isDateRangeYN = isDateRangeYN;
	}

	public List<Integer> getYears() {
		return years;
	}

	public void setYears(List<Integer> years) {
		this.years = years;
	}

	public List<String> getMonths() {
		return months;
	}

	public void setMonths(List<String> months) {
		this.months = months;
	}

	public List<Integer> getWeeks() {
		return weeks;
	}

	public void setWeeks(List<Integer> weeks) {
		this.weeks = weeks;
	}

	public List<String> getDmNames() {
		return dmNames;
	}

	public void setDmNames(List<String> dmNames) {
		this.dmNames = dmNames;
	}

	public List<String> getStatuses() {
		return statuses;
	}

	public void setStatuses(List<String> statuses) {
		this.statuses = statuses;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getMonth() {
		return month;
	}

	public void setMonth(String month) {
		this.month = month;
	}

	public String getWeek() {
		return week;
	}

	public void setWeek(String week) {
		this.week = week;
	}

	public String getDmName() {
		return dmName;
	}

	public void setDmName(String dmName) {
		this.dmName = dmName;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return the names
	 */
	public List<String> getNames() {
		return names;
	}

	/**
	 * @param names the names to set
	 */
	public void setNames(List<String> names) {
		this.names = names;
	}

}
