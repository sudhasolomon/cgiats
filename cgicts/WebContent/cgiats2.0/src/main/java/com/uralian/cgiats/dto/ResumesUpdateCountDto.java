package com.uralian.cgiats.dto;

public class ResumesUpdateCountDto {
	private String name;
	private String date;
	private String day;
	private double resumes_count;
	private double avg_count;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getDay() {
		return day;
	}
	public void setDay(String day) {
		this.day = day;
	}
	public double getResumes_count() {
		return resumes_count;
	}
	public void setResumes_count(double resumes_count) {
		this.resumes_count = resumes_count;
	}
	public double getAvg_count() {
		return avg_count;
	}
	public void setAvg_count(double avg_count) {
		this.avg_count = avg_count;
	}

}
