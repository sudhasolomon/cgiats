package com.uralian.cgiats.ws;

import java.util.Date;


import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.uralian.cgiats.model.ContentType;

@XmlRootElement(name="candidate",namespace="http://ws.cgiats.uralian.com")
@XmlType(name="candidate_type")
@XmlAccessorType(XmlAccessType.FIELD)
public class WebServiceCandidate {
	
	@XmlElement(name="first_name",required=true)
	private String firstName;
	

	@XmlElement(name="last_name",required=true)
	private String lastName;
	
	@XmlElement(name="email",required=true)
	private String email;
	
	@XmlElement(name="phone",required=true)
	private String phoneNumber;
	
	@XmlElement(name="title",required=false)
	private String title;
	
	@XmlElement(name="street",required=false)
	private String street;
	
	@XmlElement(name="city",required=true)
	private String city;
	
	@XmlElement(name="state",required=true)
	private String state;
	
	@XmlElement(name="zip",required=false)
	private String zip;
	
	@XmlElement(name="doc_type",required=true)
	private  ContentType documentType; 
	
	@XmlElement(name="document",required=true)
	private byte[] document;
	
	
	
	@XmlElement(name="visa_type",required=false)
	private String visaType;
	
	@XmlElement(name="visa_expiry_date",required=false)
	private Date visaExpiryDate;
	
	@XmlElement(name="last_updated",required=false)
	private String lastUpdated;
	
	@XmlElement(name="experience",required=false)
	private String experience;
	
	@XmlElement(name="last_position",required=false)
	private String lastPosition;
	
	@XmlElement(name="present_rate",required=false)
	private String presentRate;
	
	@XmlElement(name="expected_rate",required=false)
	private String expectedRate;
	
	@XmlElement(name="employment_status",required=false)
	private String employmentStatus;
	
	
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getStreet() {
		return street;
	}
	public void setStreet(String street) {
		this.street = street;
	}
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
	public String getZip() {
		return zip;
	}
	public void setZip(String zip) {
		this.zip = zip;
	}
	public ContentType getDocumentType() {
		return documentType;
	}
	public void setDocumentType(ContentType documentType) {
		this.documentType = documentType;
	}
	public byte[] getDocument() {
		return document;
	}
	public void setDocument(byte[] document) {
		this.document = document;
	}
	
	public String getVisaType() {
		return visaType;
	}
	public void setVisaType(String visaType) {
		this.visaType = visaType;
	}
	public Date getVisaExpiryDate() {
		return visaExpiryDate;
	}
	public void setVisaExpiryDate(Date visaExpiryDate) {
		this.visaExpiryDate = visaExpiryDate;
	}
	public String getLastUpdated() {
		return lastUpdated;
	}
	public void setLastUpdated(String lastUpdated) {
		this.lastUpdated = lastUpdated;
	}
	public String getExperience() {
		return experience;
	}
	public void setExperience(String experience) {
		this.experience = experience;
	}
	public String getLastPosition() {
		return lastPosition;
	}
	public void setLastPosition(String lastPosition) {
		this.lastPosition = lastPosition;
	}
	public String getPresentRate() {
		return presentRate;
	}
	public void setPresentRate(String presentRate) {
		this.presentRate = presentRate;
	}
	public String getExpectedRate() {
		return expectedRate;
	}
	public void setExpectedRate(String expectedRate) {
		this.expectedRate = expectedRate;
	}
	public String getEmploymentStatus() {
		return employmentStatus;
	}
	public void setEmploymentStatus(String employmentStatus) {
		this.employmentStatus = employmentStatus;
	}
}
