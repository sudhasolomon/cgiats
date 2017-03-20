package com.uralian.cgiats.dto;

public class JobOrderStatusDto {
	
	private String userName;
	private String assigned;
	private String filled;
	private String closed;
	private String reopen;
	private String total;
	private String open;
	private String userId;
	
	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	public String getUserId() {
		return userId;
	}
	
	public void setOpen(String open) {
		this.open = open;
	}
	
	public String getOpen() {
		return open;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getAssigned() {
		return assigned;
	}
	public void setAssigned(String assigned) {
		this.assigned = assigned;
	}
	public String getFilled() {
		return filled;
	}
	public void setFilled(String filled) {
		this.filled = filled;
	}
	public String getClosed() {
		return closed;
	}
	public void setClosed(String closed) {
		this.closed = closed;
	}
	public String getReopen() {
		return reopen;
	}
	public void setReopen(String reopen) {
		this.reopen = reopen;
	}
	public String getTotal() {
		return total;
	}
	public void setTotal(String total) {
		this.total = total;
	}
	
	

}
