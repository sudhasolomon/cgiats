/*
 * CandidateStatus.java Jan 21, 2013
 *
 * Copyright 2012 Uralian, LLC. All rights reserved.
 */
package com.uralian.cgiats.model;

/**
 * 
 * @author skurapati
 *
 */
public enum JobExpireStatus {
	HOURS12("12 Hours"),HOURS24("24 Hours"), HOURS48("48 Hours"), WEEK1("1 Week"), ONGOING("On Going");
	private String status;

	JobExpireStatus(String status) {
		this.status = status;
	}

	public String getStatus() {
		return this.status;
	}
}
