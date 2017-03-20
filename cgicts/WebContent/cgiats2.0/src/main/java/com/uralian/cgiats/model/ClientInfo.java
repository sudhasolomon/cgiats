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
 * This class encapsulates information on a client details
 * 
 * @author Radhika
 */


@Entity
@Table(name = "client_info")
public class ClientInfo extends AuditableEntity<Integer> implements Serializable
{
	private static final long serialVersionUID = -2162621836299612953L;
	
	@Id
	@SequenceGenerator(name = "generator", sequenceName = "client_info_clientinfo_id_seq")
	@GeneratedValue
	@Column(name = "clientinfo_id")
	protected Integer id;

	@Column(name = "client_name")
	private String clientName;
	
	@Column(name = "contact_person")
	private String contactPerson;
	
	@Column(name = "person_last_name")
	private String PersonLastName;
	
	@Column(name = "phone")
	protected String phone;
	
	@Column(name = "extension")
	protected String extension;
	
	@Column(name = "fax")
	protected String fax;
		
	@Column(name = "email")
	protected String email;
	
	@Column(name = "bill_rate")
	protected String billRate;
		
	@Column(name = "ot_eligibility")
	protected Boolean otEligibility;
		
	@Column(name = "ot_rate")
	protected String otRate;

	@Column(name = "invoicing")
	protected String invoicing;
	
	@Column(name = "payment_terms")
	protected String paymentTerms;

	@Column(name = "end_client_name")
	protected String endClientName;
	
	@Column(name = "client_street1")
	private String clientstreet1;
	
	@Column(name = "client_street2")
	private String clientstreet2;
	
	@Column(name = "client_city")
	private String clientcity;

	@Column(name = "client_state")
	private String clientstate;
	
	@Column(name = "client_country")
	private String clientcountry;
	
	@Column(name = "client_zipcode")
	private String clientzipcode;
	
	@Column(name = "invoicing_street1")
	private String invoicingstreet1;
	
	@Column(name = "invoicing_street2")
	private String invoicingstreet2;
	
	@Column(name = "invoicing_city")
	private String invoicingcity;

	@Column(name = "invoicing_state")
	private String invoicingstate;
	
	@Column(name = "invoicing_country")
	private String invoicingcountry;
	
	@Column(name = "invoicing_zipcode")
	private String invoicingzipcode;

	@Column(name = "project_manager_name")
	protected String projectManagerName;
	

	@Column(name = "project_manager_phone")
	protected String projectManagerPhone;
	
	@Column(name = "project_manager_extension")
	protected String projectManagerExtension;
	
	@ManyToOne(optional = false)
	@JoinColumn(name = "candidateinfo_id")
	@ForeignKey(name = "fk_client_info_candidate_info")
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
	
	
	
	
	public String getPersonLastName() {
		return PersonLastName;
	}

	public void setPersonLastName(String personLastName) {
		PersonLastName = personLastName;
	}

	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

	public String getContactPerson() {
		return contactPerson;
	}

	public void setContactPerson(String contactPerson) {
		this.contactPerson = contactPerson;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getExtension() {
		return extension;
	}

	public void setExtension(String extension) {
		this.extension = extension;
	}

	public String getFax() {
		return fax;
	}

	public void setFax(String fax) {
		this.fax = fax;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getBillRate() {
		return billRate;
	}

	public void setBillRate(String billRate) {
		this.billRate = billRate;
	}

	public Boolean getOtEligibility() {
		return otEligibility;
	}

	public void setOtEligibility(Boolean otEligibility) {
		this.otEligibility = otEligibility;
	}

	public String getOtRate() {
		return otRate;
	}

	public void setOtRate(String otRate) {
		this.otRate = otRate;
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

	public String getEndClientName() {
		return endClientName;
	}

	public void setEndClientName(String endClientName) {
		this.endClientName = endClientName;
	}

	

	public String getClientstreet1() {
		return clientstreet1;
	}

	public void setClientstreet1(String clientstreet1) {
		this.clientstreet1 = clientstreet1;
	}

	public String getClientstreet2() {
		return clientstreet2;
	}

	public void setClientstreet2(String clientstreet2) {
		this.clientstreet2 = clientstreet2;
	}

	public String getClientcity() {
		return clientcity;
	}

	public void setClientcity(String clientcity) {
		this.clientcity = clientcity;
	}

	public String getClientstate() {
		return clientstate;
	}

	public void setClientstate(String clientstate) {
		this.clientstate = clientstate;
	}

	public String getClientcountry() {
		return clientcountry;
	}

	public void setClientcountry(String clientcountry) {
		this.clientcountry = clientcountry;
	}

	public String getClientzipcode() {
		return clientzipcode;
	}

	public void setClientzipcode(String clientzipcode) {
		this.clientzipcode = clientzipcode;
	}

	public String getInvoicingstreet1() {
		return invoicingstreet1;
	}

	public void setInvoicingstreet1(String invoicingstreet1) {
		this.invoicingstreet1 = invoicingstreet1;
	}

	public String getInvoicingstreet2() {
		return invoicingstreet2;
	}

	public void setInvoicingstreet2(String invoicingstreet2) {
		this.invoicingstreet2 = invoicingstreet2;
	}

	public String getInvoicingcity() {
		return invoicingcity;
	}

	public void setInvoicingcity(String invoicingcity) {
		this.invoicingcity = invoicingcity;
	}

	public String getInvoicingstate() {
		return invoicingstate;
	}

	public void setInvoicingstate(String invoicingstate) {
		this.invoicingstate = invoicingstate;
	}

	public String getInvoicingcountry() {
		return invoicingcountry;
	}

	public void setInvoicingcountry(String invoicingcountry) {
		this.invoicingcountry = invoicingcountry;
	}

	public String getInvoicingzipcode() {
		return invoicingzipcode;
	}

	public void setInvoicingzipcode(String invoicingzipcode) {
		this.invoicingzipcode = invoicingzipcode;
	}

	public String getProjectManagerName() {
		return projectManagerName;
	}

	public void setProjectManagerName(String projectManagerName) {
		this.projectManagerName = projectManagerName;
	}

	public String getProjectManagerPhone() {
		return projectManagerPhone;
	}

	public void setProjectManagerPhone(String projectManagerPhone) {
		this.projectManagerPhone = projectManagerPhone;
	}

	public String getProjectManagerExtension() {
		return projectManagerExtension;
	}

	public void setProjectManagerExtension(String projectManagerExtension) {
		this.projectManagerExtension = projectManagerExtension;
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
