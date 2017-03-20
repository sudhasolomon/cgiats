package com.uralian.cgiats.dto;

import java.util.ArrayList;
import java.util.List;

public class OfferLetterDto {

	private Integer offerLetterId;
	private CandidateDto candidateDto;
	private Integer candidateId;
	private Integer jobOrderId;
	private String firstName;
	private String lastName;
	private String gender;
	private String strDateofbirth;
	private String strUpdatedOn;
	private String phone;
	private String email;
	private String street1;
	private String street2;
	private String city;
	private String state;
	private String workLocationState;
	private String country;
	private String zipcode;
	private String ssn;
	private String immigrationStatus;
	private String positionTitle;
	private String salaryRate;
	private String endclientName;
	private String strStartdateOfAssignment;
	private String overtime;
	private String bonusDescription;
	private String notes;
	private String workLocation;
	private String commibonusEligible;
	private String relocationBenefits;
	private String[] relocationBenefitArray;
	private Integer deleteFlag;
	private String reason;
	private String status;
	private String bdmName;
	private String recruiterName;
	private String companyName;
	private String taxId;
	private String contactPerson;
	private String contactPersonFirstName;
	private String contactPersonLastName;
	private String titleOfName;
	private String paymentTerms;
	private String clientphone;
	private String client_extension;
	private String client_fax;
	private String clientemail;
	private String clientstreet;
	private String clientstreet1;
	private String clientstreet2;
	private String clientcity;
	private String clientstate;
	private String clientcountry;
	private String clientzipcode;
	private String backgroundcheck;
	private String drugcheck;
	private String benifitsCategory;
	private boolean electingMedicalHourly;
	private boolean waviningMedicalHourly;
	private boolean electingMedicalSalired;
	private boolean waviningMedicalSalired;
	private String expections;
	private String specialInstructions;
	private String offerFrom;
	private String individual;
	private String createdBy;
	private String updatedBy;
	private String fullName;
	private String address;
	private List<OfferLetterHistoryDto> history = new ArrayList<OfferLetterHistoryDto>();

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}

	public Integer getOfferLetterId() {
		return offerLetterId;
	}

	public void setOfferLetterId(Integer offerLetterId) {
		this.offerLetterId = offerLetterId;
	}

	public CandidateDto getCandidateDto() {
		return candidateDto;
	}

	public void setCandidateDto(CandidateDto candidateDto) {
		this.candidateDto = candidateDto;
	}

	public Integer getJobOrderId() {
		return jobOrderId;
	}

	public void setJobOrderId(Integer jobOrderId) {
		this.jobOrderId = jobOrderId;
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

	public String getStrDateofbirth() {
		return strDateofbirth;
	}

	public void setStrDateofbirth(String strDateofbirth) {
		this.strDateofbirth = strDateofbirth;
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

	public String getStrStartdateOfAssignment() {
		return strStartdateOfAssignment;
	}

	public void setStrStartdateOfAssignment(String strStartdateOfAssignment) {
		this.strStartdateOfAssignment = strStartdateOfAssignment;
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

	public Integer getDeleteFlag() {
		return deleteFlag;
	}

	public void setDeleteFlag(Integer deleteFlag) {
		this.deleteFlag = deleteFlag;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
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

	public List<OfferLetterHistoryDto> getHistory() {
		return history;
	}

	public void setHistory(List<OfferLetterHistoryDto> history) {
		this.history = history;
	}

	/**
	 * @return the workLocationState
	 */
	public String getWorkLocationState() {
		return workLocationState;
	}

	/**
	 * @param workLocationState
	 *            the workLocationState to set
	 */
	public void setWorkLocationState(String workLocationState) {
		this.workLocationState = workLocationState;
	}

	/**
	 * @return the relocationBenefitArray
	 */
	public String[] getRelocationBenefitArray() {
		return relocationBenefitArray;
	}

	/**
	 * @param relocationBenefitArray
	 *            the relocationBenefitArray to set
	 */
	public void setRelocationBenefitArray(String[] relocationBenefitArray) {
		this.relocationBenefitArray = relocationBenefitArray;
	}

	/**
	 * @return the candidateId
	 */
	public Integer getCandidateId() {
		return candidateId;
	}

	/**
	 * @param candidateId
	 *            the candidateId to set
	 */
	public void setCandidateId(Integer candidateId) {
		this.candidateId = candidateId;
	}

	/**
	 * @return the street2
	 */
	public String getStreet2() {
		return street2;
	}

	/**
	 * @param street2
	 *            the street2 to set
	 */
	public void setStreet2(String street2) {
		this.street2 = street2;
	}

	/**
	 * @return the contactPersonFirstName
	 */
	public String getContactPersonFirstName() {
		return contactPersonFirstName;
	}

	/**
	 * @param contactPersonFirstName
	 *            the contactPersonFirstName to set
	 */
	public void setContactPersonFirstName(String contactPersonFirstName) {
		this.contactPersonFirstName = contactPersonFirstName;
	}

	/**
	 * @return the contactPersonLastName
	 */
	public String getContactPersonLastName() {
		return contactPersonLastName;
	}

	/**
	 * @param contactPersonLastName
	 *            the contactPersonLastName to set
	 */
	public void setContactPersonLastName(String contactPersonLastName) {
		this.contactPersonLastName = contactPersonLastName;
	}

	/**
	 * @return the clientstreet1
	 */
	public String getClientstreet1() {
		return clientstreet1;
	}

	/**
	 * @param clientstreet1
	 *            the clientstreet1 to set
	 */
	public void setClientstreet1(String clientstreet1) {
		this.clientstreet1 = clientstreet1;
	}

	/**
	 * @return the clientstreet2
	 */
	public String getClientstreet2() {
		return clientstreet2;
	}

	/**
	 * @param clientstreet2
	 *            the clientstreet2 to set
	 */
	public void setClientstreet2(String clientstreet2) {
		this.clientstreet2 = clientstreet2;
	}

	/**
	 * @return the fullName
	 */
	public String getFullName() {
		return fullName;
	}

	/**
	 * @param fullName
	 *            the fullName to set
	 */
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	/**
	 * @return the address
	 */
	public String getAddress() {
		return address;
	}

	/**
	 * @param address
	 *            the address to set
	 */
	public void setAddress(String address) {
		this.address = address;
	}

	/**
	 * @return the strUpdatedOn
	 */
	public String getStrUpdatedOn() {
		return strUpdatedOn;
	}

	/**
	 * @param strUpdatedOn
	 *            the strUpdatedOn to set
	 */
	public void setStrUpdatedOn(String strUpdatedOn) {
		this.strUpdatedOn = strUpdatedOn;
	}

}
