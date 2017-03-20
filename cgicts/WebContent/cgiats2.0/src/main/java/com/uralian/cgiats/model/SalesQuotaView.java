package com.uralian.cgiats.model;

public class SalesQuotaView {
	
	private int submittedCount;
	private int dmrejCount;
	private int acceptedCount;
	private int interviewingCount;
	private int confirmedCount;
	private int rejectedCount;
	private int salesquota;
	private Integer acceptedPerc;
	private Integer interPerc;
	private String recruiterName;
	
	
	public String getRecruiterName() {
		return recruiterName;
	}
	public void setRecruiterName(String recruiterName) {
		this.recruiterName = recruiterName;
	}
	public Integer getAcceptedPerc() {
		if(acceptedPerc==null)
			acceptedPerc=0;
		return acceptedPerc;
	}
	public void setAcceptedPerc(Integer acceptedPerc) {
		this.acceptedPerc = acceptedPerc;
	}
	public Integer getInterPerc() {
		if(interPerc==null)
			interPerc=0;
		return interPerc;
	}
	public void setInterPerc(Integer interPerc) {
		this.interPerc = interPerc;
	}
	public int getSubmittedCount() {
		return submittedCount;
	}
	public void setSubmittedCount(int submittedCount) {
		this.submittedCount = submittedCount;
	}
	public int getDmrejCount() {
		return dmrejCount;
	}
	public void setDmrejCount(int dmrejCount) {
		this.dmrejCount = dmrejCount;
	}
	public int getAcceptedCount() {
		return acceptedCount;
	}
	public void setAcceptedCount(int acceptedCount) {
		this.acceptedCount = acceptedCount;
	}
	public int getInterviewingCount() {
		return interviewingCount;
	}
	public void setInterviewingCount(int interviewingCount) {
		this.interviewingCount = interviewingCount;
	}
	public int getConfirmedCount() {
		return confirmedCount;
	}
	public void setConfirmedCount(int confirmedCount) {
		this.confirmedCount = confirmedCount;
	}
	public int getRejectedCount() {
		return rejectedCount;
	}
	public void setRejectedCount(int rejectedCount) {
		this.rejectedCount = rejectedCount;
	}
	public int getSalesquota() {
		return salesquota;
	}
	public void setSalesquota(int salesquota) {
		this.salesquota = salesquota;
	}
	@Override
	public String toString() {
		return "SalesQuotaView [submittedCount=" + submittedCount
				+ ", dmrejCount=" + dmrejCount + ", acceptedCount="
				+ acceptedCount + ", interviewingCount=" + interviewingCount
				+ ", confirmedCount=" + confirmedCount + ", rejectedCount="
				+ rejectedCount + ", salesquota=" + salesquota
				+ ", recruiterName=" + recruiterName + "]";
	}
	
}
