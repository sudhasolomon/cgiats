/**
 * 
 */
package com.uralian.cgiats.model;

/**
 * @author skurapati
 *
 */
public enum ResumeAuditStatus {
	UPDATED("UPDATED");
	private String name;

	ResumeAuditStatus(String name) {
		this.name = name;
	}

	public String toString() {
		return this.name;
	}
}
