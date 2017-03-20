package com.uralian.cgiats.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.ForeignKey;
/**
 * This class encapsulates information on a agency details
 * 
 * @author Radhika
 */

@Entity
@Table(name = "agency_details")

public class AgencyDetails  extends AuditableEntity<Integer> implements Serializable
{
	private static final long serialVersionUID = -2162621836299612953L;
	
	@Id
	@SequenceGenerator(name = "generator", sequenceName = "agency_details_agency_id_seq")
	@GeneratedValue
	@Column(name = "agency_id")
	protected Integer id;
	
	@Column(name = "c2c_agency_name")
	private String c2cAgencyName;
	
	@Column(name = "contact_person_name")
	private String contactPersonName;
	
	@Column(name = "contact_person_last_name")
	private String contactPersonLastName;
	
	@Column(name = "contact_person_phone")
	private String contactPersonPhone;
	
	@Column(name = "contact_person_extension")
	private String contactPersonExtension;
	
	@Column(name = "contact_person_fax")
	private String contactPersonFax;
	
	@Column(name = "contact_person_email")
	private String contactPersonEmail;
	
	@Column(name = "street1")
	private String street1;
	
	@Column(name = "street2")
	private String street2;
	
	@Column(name = "city")
	private String city;

	@Column(name = "state")
	private String state;
	
	@Column(name = "country")
	private String country;
	
	@Column(name = "zipcode")
	private String zipcode;
	
	@Column(name = "buy_rate")
	private String buyRate;
	
	@Column(name = "invoicing")
	private String invoicing;

	@Column(name = "payment_terms")
	private String paymentTerms;
	
	@ManyToOne(optional = false)
	@JoinColumn(name = "candidateinfo_id")
	@ForeignKey(name = "fk_agency_details_candidate_info")
	private CandidateInfo candidateinfo;

	@Override
	public Integer getId() {
		// TODO Auto-generated method stub
		return id;
	}

	@Override
	protected Object getBusinessKey() {
		// TODO Auto-generated method stub
		return id;
	}

	
	
	public String getContactPersonLastName() {
		return contactPersonLastName;
	}

	public void setContactPersonLastName(String contactPersonLastName) {
		this.contactPersonLastName = contactPersonLastName;
	}

	public String getC2cAgencyName() {
		return c2cAgencyName;
	}

	public void setC2cAgencyName(String c2cAgencyName) {
		this.c2cAgencyName = c2cAgencyName;
	}

	public String getContactPersonName() {
		return contactPersonName;
	}

	public void setContactPersonName(String contactPersonName) {
		this.contactPersonName = contactPersonName;
	}

	public String getContactPersonPhone() {
		return contactPersonPhone;
	}

	public void setContactPersonPhone(String contactPersonPhone) {
		this.contactPersonPhone = contactPersonPhone;
	}

	public String getContactPersonExtension() {
		return contactPersonExtension;
	}

	public void setContactPersonExtension(String contactPersonExtension) {
		this.contactPersonExtension = contactPersonExtension;
	}

	public String getContactPersonFax() {
		return contactPersonFax;
	}

	public void setContactPersonFax(String contactPersonFax) {
		this.contactPersonFax = contactPersonFax;
	}

	public String getContactPersonEmail() {
		return contactPersonEmail;
	}

	public void setContactPersonEmail(String contactPersonEmail) {
		this.contactPersonEmail = contactPersonEmail;
	}

	public String getStreet1() {
		return street1;
	}

	public void setStreet1(String street1) {
		this.street1 = street1;
	}

	public String getStreet2() {
		return street2;
	}

	public void setStreet2(String street2) {
		this.street2 = street2;
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

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getZipcode() {
		return zipcode;
	}

	public void setZipcode(String zipcode) {
		this.zipcode = zipcode;
	}

	public String getBuyRate() {
		return buyRate;
	}

	public void setBuyRate(String buyRate) {
		this.buyRate = buyRate;
	}

	public String getInvoicing() {
		return invoicing;
	}

	public void setInvoicing(String invoicing) {
		this.invoicing = invoicing;
	}

	public String getPaymentTerms() {
		return paymentTerms;
	}

	public void setPaymentTerms(String paymentTerms) {
		this.paymentTerms = paymentTerms;
	}

	public CandidateInfo getCandidateinfo() {
		return candidateinfo;
	}

	public void setCandidateinfo(CandidateInfo candidateinfo) {
		this.candidateinfo = candidateinfo;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	
	

}
