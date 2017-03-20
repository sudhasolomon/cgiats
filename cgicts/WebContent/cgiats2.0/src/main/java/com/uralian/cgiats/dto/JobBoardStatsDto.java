package com.uralian.cgiats.dto;

public class JobBoardStatsDto {
	
	private String users;
	private String downloadCount;
	private String monthlyViews;
	private String totalCount;
	private String resumes;
	
	public void setUsers(String users) {
		this.users = users;
	}

	public String getDownloadCount() {
		return downloadCount;
	}

	public void setDownloadCount(String downloadCount) {
		this.downloadCount = downloadCount;
	}

	public String getUsers() {
		return users;
	}

	public String getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(String totalCount) {
		this.totalCount = totalCount;
	}
	
	public void setMonthlyViews(String monthlyViews) {
		this.monthlyViews = monthlyViews;
	}
	
	public String getMonthlyViews() {
		return monthlyViews;
	}
	
	public void setResumes(String resumes) {
		this.resumes = resumes;
	}
	
	public String getResumes() {
		return resumes;
	}

}
