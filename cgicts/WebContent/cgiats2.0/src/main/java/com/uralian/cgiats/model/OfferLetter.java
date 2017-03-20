package com.uralian.cgiats.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.ForeignKey;

import com.uralian.cgiats.util.Utils;


@Entity
@Table(name = "offer_letter")
public class OfferLetter extends AuditableEntity<Integer> implements Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1731432566270319724L;
	
	@Id
	@SequenceGenerator(name = "generator", sequenceName = "offer_letter_offerLetter_id_seq")
	@GeneratedValue
	@Column(name = "offerLetter_id")
	protected Integer id;
	
	@ManyToOne(optional = false)
	@JoinColumn(name = "candidate_id")
	@ForeignKey(name = "fk_offer_letter_candidate")
	private Candidate candidate;
	
	
	@Column(name="jobOrder_id")
	protected Integer jobOrderId;
	
	
	@OneToMany(fetch=FetchType.EAGER,mappedBy = "offerletter", cascade = CascadeType.ALL, orphanRemoval = true)
	@OrderBy("createdDate")
	private List<OfferLetterHistory> history = new ArrayList<OfferLetterHistory>();
	
	
	@Column(name = "first_name")
	protected String firstName;

	@Column(name = "last_name")
	protected String lastName;

	@Column(name = "gender")
	protected String gender;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "date_of_birth")
	private Date dateofbirth;
	
	@Column(name = "phone")
	protected String phone;

	@Column(name = "email")
	protected String email;
	
	
	@Column(name = "street1")
	private String street1;
	
	@Column(name = "city")
	private String city;

	@Column(name = "state")
	private String state;
	
	@Column(name = "country")
	private String country;
	
	@Column(name = "zipcode")
	private String zipcode;
	
	@Column(name = "ssn")
	private String ssn;
	
	@Column(name = "immigration_status")
	private String immigrationStatus;
	
	@Column(name = "position_title")
	 private String positionTitle;
	
	@Column(name = "salary_rate")
	private String salaryRate;
	
	@Column(name = "end_client_name")
	 private String endclientName;
	
	@Column(name = "start_date_of_assignment")
	private Date startdateOfAssignment;
	
	
	@Column (name="overtime")
	private String overtime;
	
	@Column(name="bonus_description")
	private String bonusDescription;
	
	@Column(name="notes")
	private String notes;
	
	@Column(name = "work_location")
	 private String workLocation;
	
	@Column(name = "commi_bonus_eligible")
	 private String commibonusEligible;
	
	@Column(name = "relocation_benefits")
	private String relocationBenefits;
	
	@Column(name = "delete_flg")
	private Integer deleteFlag;
	
	@Column(name = "reason")
	private String reason;
	
	@Column(name = "status")
	@Enumerated(EnumType.STRING)
	private OfferLetterStatus status;
	
	@Column(name = "bdm_name")
	 private String bdmName;
	
	@Column(name = "recruiter_name")
	 private String recruiterName;
	
	@Column(name = "company_name")
	 private String companyName;
	
	@Column(name = "tax_id")
	 private String taxId;
	
	@Column(name = "contact_person")
	 private String contactPerson;
	
	@Column(name = "contact_person_firstname")
	 private String contactPersonFirstName;
	
	@Column(name = "street2")
	private String street2;
	
	@Column(name = "work_location_state")
	private String workLocationState;
	
	@Column(name = "contact_person_lastname")
	private String contactPersonLastName;
	
	@Column(name = "title_of_name")
	 private String titleOfName;
	
	@Column(name = "payment_terms")
	protected String paymentTerms;
	
	@Column(name = "client_phone")
	protected String clientphone;
	
	@Column(name = "client_extension")
	protected String client_extension;
	
	@Column(name = "client_fax")
	protected String client_fax;
		
	@Column(name = "client_email")
	protected String clientemail;
	 
	@Column(name = "client_street")
	private String clientstreet;
	
	
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
	
	 @Column(name = "background_check")
	 private String backgroundcheck;
	 
	 @Column(name = "drug_check")
	 private String drugcheck;
	 
	 @Column(name = "benifits_category")
	 private String benifitsCategory;
	 
	 
	 @Column(name = "electing_medical_hourly")
	private boolean electingMedicalHourly;
			
	@Column(name = "wavining_medical_hourly")
	private boolean waviningMedicalHourly;
	
	@Column(name = "electing_medical_salired")
	private boolean electingMedicalSalired;
			
	@Column(name = "wavining_medical_salired")
	private boolean waviningMedicalSalired;
		
	@Column(name = "expections")
	private String expections;

	@Column(name = "special_instructions")
	private String specialInstructions;
	
	@Column(name = "offer_from")
	private String offerFrom;
	
	@Column(name = "individual")
	private String individual;

	
	
	
	public String getStreet2() {
		return street2;
	}

	public void setStreet2(String street2) {
		this.street2 = street2;
	}

	public String getWorkLocationState() {
		return workLocationState;
	}

	public void setWorkLocationState(String workLocationState) {
		this.workLocationState = workLocationState;
	}

	public String getContactPersonFirstName() {
		return contactPersonFirstName;
	}

	public void setContactPersonFirstName(String contactPersonFirstName) {
		this.contactPersonFirstName = contactPersonFirstName;
	}

	public String getContactPersonLastName() {
		return contactPersonLastName;
	}

	public void setContactPersonLastName(String contactPersonLastName) {
		this.contactPersonLastName = contactPersonLastName;
	}

	public String getClientstreet2() {
		return clientstreet2;
	}

	public void setClientstreet2(String clientstreet2) {
		this.clientstreet2 = clientstreet2;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	
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

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public Date getDateofbirth() {
		return dateofbirth;
	}

	public void setDateofbirth(Date dateofbirth) {
		this.dateofbirth = dateofbirth;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getStreet1() {
		return street1;
	}

	public void setStreet1(String street1) {
		this.street1 = street1;
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

	public String getSsn() {
		return ssn;
	}

	public void setSsn(String ssn) {
		this.ssn = ssn;
	}

	public String getImmigrationStatus() {
		return immigrationStatus;
	}

	public void setImmigrationStatus(String immigrationStatus) {
		this.immigrationStatus = immigrationStatus;
	}

	public String getPositionTitle() {
		return positionTitle;
	}

	public void setPositionTitle(String positionTitle) {
		this.positionTitle = positionTitle;
	}

	public String getSalaryRate() {
		return salaryRate;
	}

	public void setSalaryRate(String salaryRate) {
		this.salaryRate = salaryRate;
	}

	public String getEndclientName() {
		return endclientName;
	}

	public void setEndclientName(String endclientName) {
		this.endclientName = endclientName;
	}

	public Date getStartdateOfAssignment() {
		return startdateOfAssignment;
	}

	public void setStartdateOfAssignment(Date startdateOfAssignment) {
		this.startdateOfAssignment = startdateOfAssignment;
	}

	public String getWorkLocation() {
		return workLocation;
	}

	public void setWorkLocation(String workLocation) {
		this.workLocation = workLocation;
	}

	public String getCommibonusEligible() {
		return commibonusEligible;
	}

	public void setCommibonusEligible(String commibonusEligible) {
		this.commibonusEligible = commibonusEligible;
	}

	public String getRelocationBenefits() {
		return relocationBenefits;
	}

	public void setRelocationBenefits(String relocationBenefits) {
		this.relocationBenefits = relocationBenefits;
	}

	public String getBdmName() {
		return bdmName;
	}

	public void setBdmName(String bdmName) {
		this.bdmName = bdmName;
	}

	public String getRecruiterName() {
		return recruiterName;
	}

	public void setRecruiterName(String recruiterName) {
		this.recruiterName = recruiterName;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getTaxId() {
		return taxId;
	}

	public void setTaxId(String taxId) {
		this.taxId = taxId;
	}

	public String getContactPerson() {
		return contactPerson;
	}

	public void setContactPerson(String contactPerson) {
		this.contactPerson = contactPerson;
	}

	public String getTitleOfName() {
		return titleOfName;
	}

	public void setTitleOfName(String titleOfName) {
		this.titleOfName = titleOfName;
	}

	public String getPaymentTerms() {
		return paymentTerms;
	}

	public void setPaymentTerms(String paymentTerms) {
		this.paymentTerms = paymentTerms;
	}

	public String getClientphone() {
		return clientphone;
	}

	public void setClientphone(String clientphone) {
		this.clientphone = clientphone;
	}

	public String getClient_extension() {
		return client_extension;
	}

	public void setClient_extension(String client_extension) {
		this.client_extension = client_extension;
	}

	public String getClient_fax() {
		return client_fax;
	}

	public void setClient_fax(String client_fax) {
		this.client_fax = client_fax;
	}

	public String getClientemail() {
		return clientemail;
	}

	public void setClientemail(String clientemail) {
		this.clientemail = clientemail;
	}

	public String getClientstreet() {
		return clientstreet;
	}

	public void setClientstreet(String clientstreet) {
		this.clientstreet = clientstreet;
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

	public String getBackgroundcheck() {
		return backgroundcheck;
	}

	public void setBackgroundcheck(String backgroundcheck) {
		this.backgroundcheck = backgroundcheck;
	}

	public String getDrugcheck() {
		return drugcheck;
	}

	public void setDrugcheck(String drugcheck) {
		this.drugcheck = drugcheck;
	}
	
	public String getBenifitsCategory() {
		return benifitsCategory;
	}

	public void setBenifitsCategory(String benifitsCategory) {
		this.benifitsCategory = benifitsCategory;
	}

	public boolean isElectingMedicalHourly() {
		return electingMedicalHourly;
	}

	public void setElectingMedicalHourly(boolean electingMedicalHourly) {
		this.electingMedicalHourly = electingMedicalHourly;
	}

	public boolean isWaviningMedicalHourly() {
		return waviningMedicalHourly;
	}

	public void setWaviningMedicalHourly(boolean waviningMedicalHourly) {
		this.waviningMedicalHourly = waviningMedicalHourly;
	}

	public boolean isElectingMedicalSalired() {
		return electingMedicalSalired;
	}

	public void setElectingMedicalSalired(boolean electingMedicalSalired) {
		this.electingMedicalSalired = electingMedicalSalired;
	}

	public boolean isWaviningMedicalSalired() {
		return waviningMedicalSalired;
	}

	public void setWaviningMedicalSalired(boolean waviningMedicalSalired) {
		this.waviningMedicalSalired = waviningMedicalSalired;
	}

	public String getExpections() {
		return expections;
	}

	public void setExpections(String expections) {
		this.expections = expections;
	}

	public String getSpecialInstructions() {
		return specialInstructions;
	}

	public void setSpecialInstructions(String specialInstructions) {
		this.specialInstructions = specialInstructions;
	}

	@Override
	protected Object getBusinessKey() {
		// TODO Auto-generated method stub
		return id;
	}


	public Candidate getCandidate() {
		return candidate;
	}

	public void setCandidate(Candidate candidate) {
		this.candidate = candidate;
	}

	public Integer getDeleteFlag() {
		return deleteFlag;
	}

	public void setDeleteFlag(Integer deleteFlag) {
		this.deleteFlag = deleteFlag;
	}
	
	public String getFullName()
	{
		StringBuffer sb = new StringBuffer();
		if (!Utils.isBlank(firstName))
			sb.append(firstName);
		sb.append(" ");
		if (!Utils.isBlank(lastName))
			sb.append(lastName);

		String fullName = sb.toString().trim();
		return fullName;
	}

	public String getOvertime() {
		return overtime;
	}

	public void setOvertime(String overtime) {
		this.overtime = overtime;
	}

	public String getBonusDescription() {
		return bonusDescription;
	}

	public void setBonusDescription(String bonusDescription) {
		this.bonusDescription = bonusDescription;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public String getOfferFrom() {
		return offerFrom;
	}

	public void setOfferFrom(String offerFrom) {
		this.offerFrom = offerFrom;
	}

	public String getIndividual() {
		return individual;
	}

	public void setIndividual(String individual) {
		this.individual = individual;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public OfferLetterStatus getStatus() {
		return status;
	}

	public void setStatus(OfferLetterStatus status) {
		this.status = status;
	}

	public List<OfferLetterHistory> getHistory() {
		return history;
	}

	public void setHistory(List<OfferLetterHistory> history) {
		this.history = history;
	}
	
	public void addEvent(OfferLetterHistory event)
	{		
		this.history.add(event);
		event.setOfferletter(this);
		
	}

	@Override
	public String toString() {
		return "OfferLetter [id=" + id + ", candidate=" + candidate
				+ ", history=" + history + ", firstName=" + firstName
				+ ", lastName=" + lastName + ", gender=" + gender
				+ ", dateofbirth=" + dateofbirth + ", phone=" + phone
				+ ", email=" + email + ", street1=" + street1 + ", city="
				+ city + ", state=" + state + ", country=" + country
				+ ", zipcode=" + zipcode + ", ssn=" + ssn
				+ ", immigrationStatus=" + immigrationStatus
				+ ", positionTitle=" + positionTitle + ", salaryRate="
				+ salaryRate + ", endclientName=" + endclientName
				+ ", startdateOfAssignment=" + startdateOfAssignment
				+ ", overtime=" + overtime + ", bonusDescription="
				+ bonusDescription + ", notes=" + notes + ", workLocation="
				+ workLocation + ", commibonusEligible=" + commibonusEligible
				+ ", relocationBenefits=" + relocationBenefits
				+ ", deleteFlag=" + deleteFlag + ", reason=" + reason
				+ ", status=" + status + ", bdmName=" + bdmName
				+ ", recruiterName=" + recruiterName + ", companyName="
				+ companyName + ", taxId=" + taxId + ", contactPerson="
				+ contactPerson + ", titleOfName=" + titleOfName
				+ ", paymentTerms=" + paymentTerms + ", clientphone="
				+ clientphone + ", client_extension=" + client_extension
				+ ", client_fax=" + client_fax + ", clientemail=" + clientemail
				+ ", clientstreet=" + clientstreet + ", clientcity="
				+ clientcity + ", clientstate=" + clientstate
				+ ", clientcountry=" + clientcountry + ", clientzipcode="
				+ clientzipcode + ", backgroundcheck=" + backgroundcheck
				+ ", drugcheck=" + drugcheck + ", electingMedicalHourly="
				+ electingMedicalHourly + ", waviningMedicalHourly="
				+ waviningMedicalHourly + ", electingMedicalSalired="
				+ electingMedicalSalired + ", waviningMedicalSalired="
				+ waviningMedicalSalired + ", expections=" + expections
				+ ", specialInstructions=" + specialInstructions
				+ ", offerFrom=" + offerFrom + ", individual=" + individual
				+ "]";
	}

	public Integer getJobOrderId() {
		return jobOrderId;
	}

	public void setJobOrderId(Integer jobOrderId) {
		this.jobOrderId = jobOrderId;
	}

}
