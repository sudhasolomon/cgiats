/**
 * 
 */
package com.uralian.cgiats.util;

/**
 * @author Sreenath
 *
 */
public class StatusMessage {

	private String statusCode;
	private String statusMessage;
	
	

	public StatusMessage() {
		super();
		// TODO Auto-generated constructor stub
	}

	public StatusMessage(String statusCode, String statusMessage) {
		super();
		this.statusCode = statusCode;
		this.statusMessage = statusMessage;
	}

	public String getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}

	public String getStatusMessage() {
		return statusMessage;
	}

	public void setStatusMessage(String statusMessage) {
		this.statusMessage = statusMessage;
	}

}
