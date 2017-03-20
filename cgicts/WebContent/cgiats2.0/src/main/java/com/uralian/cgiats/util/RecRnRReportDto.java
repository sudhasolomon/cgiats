package com.uralian.cgiats.util;

import com.uralian.cgiats.model.SubmittalStatus;

public class RecRnRReportDto {

	private String recName;
	private String dmName;
	private SubmittalStatus status;
	private int count;

	public String getRecName() {
		return recName;
	}

	public void setRecName(String recName) {
		this.recName = recName;
	}

	public String getDmName() {
		return dmName;
	}

	public void setDmName(String dmName) {
		this.dmName = dmName;
	}

	

	public SubmittalStatus getStatus() {
		return status;
	}

	public void setStatus(SubmittalStatus status) {
		this.status = status;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

}
