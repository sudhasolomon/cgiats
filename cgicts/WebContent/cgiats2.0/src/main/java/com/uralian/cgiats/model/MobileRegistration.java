package com.uralian.cgiats.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.search.annotations.Indexed;



@Entity
@Table(name = "mobile_registration")
public class MobileRegistration implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1227792018334486984L;

	@Id
	@Column(name="user_id")
	private String userId;
	
	@Column(name="register_id")
	private String registerId;
	
	
	@Column(name="device_type")
	private String deviceType;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getRegisterId() {
		return registerId;
	}

	public void setRegisterId(String registerId) {
		this.registerId = registerId;
	}

	public String getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}
	
	
	
	
}
