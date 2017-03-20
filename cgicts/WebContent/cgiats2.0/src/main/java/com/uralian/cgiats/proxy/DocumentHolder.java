/*
 * DocumentHolder.java May 2, 2012
 *
 * Copyright 2012 Uralian, LLC. All rights reserved.
 */
package com.uralian.cgiats.proxy;

import java.io.Serializable;

import com.uralian.cgiats.model.Address;
import com.uralian.cgiats.model.ContentType;

/**
 * @author Vlad Orzhekhovskiy
 */
public class DocumentHolder implements Serializable {
	private static final long serialVersionUID = 1L;

	private ContentType contentType;
	private String name;
	private String lastName;
	private String phone;
	private String email;
	private String location;
	private byte[] data;
	private Address address;

	// Added
	private String workStatus;
	private String lastModified;
	private String recentJobTitle;
	private String totalYearsOfExperience;
	private String securityClearance;
	private String languagesSpoken;
	private String city;
	private String state;
	private String zipcode;
	private String keySkills;

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getZipcode() {
		return zipcode;
	}

	public void setZipcode(String zipcode) {
		this.zipcode = zipcode;
	}

	public String getWorkStatus() {
		return workStatus;
	}

	public void setWorkStatus(String workStatus) {
		this.workStatus = workStatus;
	}

	public String getLastModified() {
		return lastModified;
	}

	public void setLastModified(String lastModified) {
		this.lastModified = lastModified;
	}

	public String getRecentJobTitle() {
		return recentJobTitle;
	}

	public void setRecentJobTitle(String recentJobTitle) {
		this.recentJobTitle = recentJobTitle;
	}

	public String getTotalYearsOfExperience() {
		return totalYearsOfExperience;
	}

	public void setTotalYearsOfExperience(String totalYearsOfExperience) {
		this.totalYearsOfExperience = totalYearsOfExperience;
	}

	public String getSecurityClearance() {
		return securityClearance;
	}

	public void setSecurityClearance(String securityClearance) {
		this.securityClearance = securityClearance;
	}

	public String getLanguagesSpoken() {
		return languagesSpoken;
	}

	public void setLanguagesSpoken(String languagesSpoken) {
		this.languagesSpoken = languagesSpoken;
	}

	/**
	*/
	public DocumentHolder() {
		address = new Address();
	}

	/**
	 * @return the address.
	 */
	public Address getAddress() {
		return address;
	}

	/**
	 * @return the contentType.
	 */
	public ContentType getContentType() {
		return contentType;
	}

	/**
	 * @param contentType
	 *            the contentType to set.
	 */
	public void setContentType(ContentType contentType) {
		this.contentType = contentType;
	}

	/**
	 * @return the data.
	 */
	public byte[] getData() {
		return data;
	}

	/**
	 * @param data
	 *            the data to set.
	 */
	public void setData(byte[] data) {
		this.data = data;
	}

	/**
	 * 
	 * @return name
	 */
	public String getName() {
		return name;
	}

	/**
	 * 
	 * @param name
	 *            to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 
	 * @return lastName
	 */
	public String getLastName() {
		return lastName;
	}

	/**
	 * 
	 * @param lastName
	 *            to set
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	/**
	 * 
	 * @return phone
	 */
	public String getPhone() {
		return phone;
	}

	/**
	 * 
	 * @param phone
	 *            to set
	 */
	public void setPhone(String phone) {
		this.phone = phone;
	}

	/**
	 * 
	 * @return email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * 
	 * @param email
	 *            to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * 
	 * @return location
	 */
	public String getLocation() {
		return location;
	}

	/**
	 * 
	 * @param location
	 *            to set
	 */
	public void setLocation(String location) {
		this.location = location;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "DocumentHolder [contentType=" + contentType + ", data size=" + (data != null ? data.length : 0) + "bytes]";
	}

	/**
	 * @return the keySkills
	 */
	public String getKeySkills() {
		return keySkills;
	}

	/**
	 * @param keySkills the keySkills to set
	 */
	public void setKeySkills(String keySkills) {
		this.keySkills = keySkills;
	}
}