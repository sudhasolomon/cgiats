package com.uralian.cgiats.model;

public class ExecutiveResumeView {
	
	private String portalCount;
	private String PortalName;
	private String year;
	private Integer toDayResumesCounts;
	private Integer totalResumeCounts;
	
	public String getPortalCount() {
		return portalCount;
	}
	public void setPortalCount(String portalCount) {
		this.portalCount = portalCount;
	}
	public String getPortalName() {
		return PortalName;
	}
	public void setPortalName(String portalName) {
		PortalName = portalName;
	}
	public String getYear() {
		return year;
	}
	public void setYear(String year) {
		this.year = year;
	}
	public Integer getToDayResumesCounts() {
		return toDayResumesCounts;
	}
	public void setToDayResumesCounts(Integer toDayResumesCounts) {
		this.toDayResumesCounts = toDayResumesCounts;
	}
	public Integer getTotalResumeCounts() {
		return totalResumeCounts;
	}
	public void setTotalResumeCounts(Integer totalResumeCounts) {
		this.totalResumeCounts = totalResumeCounts;
	}
	@Override
	public String toString() {
		return "ExecutiveResumeView [portalCount=" + portalCount
				+ ", PortalName=" + PortalName + ", year=" + year
				+ ", toDayResumesCounts=" + toDayResumesCounts
				+ ", totalResumeCounts=" + totalResumeCounts + "]";
	}
	
}
