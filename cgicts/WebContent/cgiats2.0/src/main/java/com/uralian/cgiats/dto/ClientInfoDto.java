package com.uralian.cgiats.dto;

import java.util.List;

import com.uralian.cgiats.model.Candidate;
import com.uralian.cgiats.model.CandidateInfo;
import com.uralian.cgiats.model.OrderByColumn;

/**
 * 
 * @author Sudha
 *
 */
public class ClientInfoDto {
	
	private String bdmName;
	private String bdmFirstName;
	private String bdmLastName;
	private String recruiterName;
	private String recruiterFirstName;
	private String recruiterLastName;
	private String jobOrderId;
	private String firstName;
	private String lastName;
	private String fullName;
	private String dateOfBirth;
	private String ssn;
	private String source;
	private String street1;
	private String street2;
	private String  city;
	private String state;
	private String country;
	private String zipCode;
	private String candidatePhone;
	private String phoneAlt;
	private String email;
	private String employmentStatus;
	private String immigrationStatus;
	private String salaryRate;
	private String perDiem;
	private String startDate;
	private String endDate;
	private String clientName;
	private String clientContactFirstName;
	private String clientContactLastName;
	private String clientPhone;
	private String extenstion;
	private String fax;
	private String clientEmail;
	private String clientStreet1;
	private String clientStreet2;
	private String clientcity;
	private String clientState;
	private String clientCountry;
	private String clientZipCode;
	private String clientinvoicing;
	private String clientpaymentTerms;
	private String clientbillRate;
	private String clientotEligibility;
	private String clientotRate;
	private String endClientName;
	private String projectManagerName;
	private String invoicingStreet1;
	private String invoicingstreet2;
	private String invoicingcity;
	private String invoicingState;
	private String invoicingCountry;
	private String invoicingZipCode;
	private String projectManagerPhone;
	private String projectManagerExtension;
	private String c2cAgencyName;
	private String c2cContactFirstName;
	private String c2cContactLastName;
	private String contactPersonPhone;
	private String contactPersonExtension;
	private String contactPersonFax;
	private String contactPersonEmail;
	private String c2cstreet1;
	private String c2cstreet2;
	private String c2ccity;
	private String c2cstate;
	private String c2ccountry;
	private String c2czipCode;
	private String paidTimeOff;
	private String medical;
	private String dentalVision;
	private List<String> relocationBenefits;
	private String expections;
	private String specialInstructions;
	private CandidateInfo candidateInfo;
	private List<String> candidateIds;
	private String id;
	private String candidateInfoId;
	private CandidateDto candidateDto;
	private String candidateId;
	
	private String contactPerson;
	private String contactPersonName;
	private String buyRate;
	private String createdBy;
	private String createdOn;
	private Candidate candidate;
	
	
	
	
	
	
	public String getRecruiterLastName() {
		return recruiterLastName;
	}
	public void setRecruiterLastName(String recruiterLastName) {
		this.recruiterLastName = recruiterLastName;
	}
	public String getClientContactFirstName() {
		return clientContactFirstName;
	}
	public void setClientContactFirstName(String clientContactFirstName) {
		this.clientContactFirstName = clientContactFirstName;
	}
	public String getClientContactLastName() {
		return clientContactLastName;
	}
	public void setClientContactLastName(String clientContactLastName) {
		this.clientContactLastName = clientContactLastName;
	}
	public String getC2cContactFirstName() {
		return c2cContactFirstName;
	}
	public void setC2cContactFirstName(String c2cContactFirstName) {
		this.c2cContactFirstName = c2cContactFirstName;
	}
	public String getC2cContactLastName() {
		return c2cContactLastName;
	}
	public void setC2cContactLastName(String c2cContactLastName) {
		this.c2cContactLastName = c2cContactLastName;
	}
	
	public Candidate getCandidate() {
		return candidate;
	}
	public void setCandidate(Candidate candidate) {
		this.candidate = candidate;
	}
	public String getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	public String getCreatedOn() {
		return createdOn;
	}
	public void setCreatedOn(String createdOn) {
		this.createdOn = createdOn;
	}
	public String getCandidateId() {
		return candidateId;
	}
	public void setCandidateId(String candidateId) {
		this.candidateId = candidateId;
	}
	public CandidateDto getCandidateDto() {
		return candidateDto;
	}
	public void setCandidateDto(CandidateDto candidateDto) {
		this.candidateDto = candidateDto;
	}
	public String getCandidateInfoId() {
		return candidateInfoId;
	}
	public void setCandidateInfoId(String candidateInfoId) {
		this.candidateInfoId = candidateInfoId;
	}
	public String getBdmFirstName() {
		return bdmFirstName;
	}
	public void setBdmFirstName(String bdmFirstName) {
		this.bdmFirstName = bdmFirstName;
	}
	public String getBdmLastName() {
		return bdmLastName;
	}
	public void setBdmLastName(String bdmLastName) {
		this.bdmLastName = bdmLastName;
	}
	public String getRecruiterFirstName() {
		return recruiterFirstName;
	}
	public void setRecruiterFirstName(String recruiterFirstName) {
		this.recruiterFirstName = recruiterFirstName;
	}
	
	public String getSsn() {
		return ssn;
	}
	public void setSsn(String ssn) {
		this.ssn = ssn;
	}
	
	public String getClientinvoicing() {
		return clientinvoicing;
	}
	public void setClientinvoicing(String clientinvoicing) {
		this.clientinvoicing = clientinvoicing;
	}
	public String getClientpaymentTerms() {
		return clientpaymentTerms;
	}
	public void setClientpaymentTerms(String clientpaymentTerms) {
		this.clientpaymentTerms = clientpaymentTerms;
	}
	public String getClientbillRate() {
		return clientbillRate;
	}
	public void setClientbillRate(String clientbillRate) {
		this.clientbillRate = clientbillRate;
	}
	public String getClientotEligibility() {
		return clientotEligibility;
	}
	public void setClientotEligibility(String clientotEligibility) {
		this.clientotEligibility = clientotEligibility;
	}
	public String getClientotRate() {
		return clientotRate;
	}
	public void setClientotRate(String clientotRate) {
		this.clientotRate = clientotRate;
	}
	public List<String> getCandidateIds() {
		return candidateIds;
	}
	public void setCandidateIds(List<String> candidateIds) {
		this.candidateIds = candidateIds;
	}
	public String getSalaryRate() {
		return salaryRate;
	}
	public void setSalaryRate(String salaryRate) {
		this.salaryRate = salaryRate;
	}
	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
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
	public String getStartDate() {
		return startDate;
	}
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	public String getEndDate() {
		return endDate;
	}
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	public String getJobOrderId() {
		return jobOrderId;
	}
	public void setJobOrderId(String jobOrderId) {
		this.jobOrderId = jobOrderId;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
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
	public String getExtenstion() {
		return extenstion;
	}
	public void setExtenstion(String extenstion) {
		this.extenstion = extenstion;
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
	public String getEndClientName() {
		return endClientName;
	}
	public void setEndClientName(String endClientName) {
		this.endClientName = endClientName;
	}
	public String getClientStreet1() {
		return clientStreet1;
	}
	public void setClientStreet1(String clientStreet1) {
		this.clientStreet1 = clientStreet1;
	}
	public String getClientStreet2() {
		return clientStreet2;
	}
	public void setClientStreet2(String clientStreet2) {
		this.clientStreet2 = clientStreet2;
	}
	public String getClientcity() {
		return clientcity;
	}
	public void setClientcity(String clientcity) {
		this.clientcity = clientcity;
	}
	public String getClientState() {
		return clientState;
	}
	public void setClientState(String clientState) {
		this.clientState = clientState;
	}
	public String getClientCountry() {
		return clientCountry;
	}
	public void setClientCountry(String clientCountry) {
		this.clientCountry = clientCountry;
	}
	public String getClientZipCode() {
		return clientZipCode;
	}
	public void setClientZipCode(String clientZipCode) {
		this.clientZipCode = clientZipCode;
	}
	public String getInvoicingStreet1() {
		return invoicingStreet1;
	}
	public void setInvoicingStreet1(String invoicingStreet1) {
		this.invoicingStreet1 = invoicingStreet1;
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
	public String getInvoicingState() {
		return invoicingState;
	}
	public void setInvoicingState(String invoicingState) {
		this.invoicingState = invoicingState;
	}
	public String getInvoicingCountry() {
		return invoicingCountry;
	}
	public void setInvoicingCountry(String invoicingCountry) {
		this.invoicingCountry = invoicingCountry;
	}
	public String getInvoicingZipCode() {
		return invoicingZipCode;
	}
	public void setInvoicingZipCode(String invoicingZipCode) {
		this.invoicingZipCode = invoicingZipCode;
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
	public CandidateInfo getCandidateInfo() {
		return candidateInfo;
	}
	public void setCandidateInfo(CandidateInfo candidateInfo) {
		this.candidateInfo = candidateInfo;
	}
	public String getClientPhone() {
		return clientPhone;
	}
	public void setClientPhone(String clientPhone) {
		this.clientPhone = clientPhone;
	}
	public String getClientEmail() {
		return clientEmail;
	}
	public void setClientEmail(String clientEmail) {
		this.clientEmail = clientEmail;
	}
	public String getCandidatePhone() {
		return candidatePhone;
	}
	public void setCandidatePhone(String candidatePhone) {
		this.candidatePhone = candidatePhone;
	}
	public String getPhoneAlt() {
		return phoneAlt;
	}
	public void setPhoneAlt(String phoneAlt) {
		this.phoneAlt = phoneAlt;
	}
	public String getDateOfBirth() {
		return dateOfBirth;
	}
	public void setDateOfBirth(String dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
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
	public String getZipCode() {
		return zipCode;
	}
	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}
	public String getEmploymentStatus() {
		return employmentStatus;
	}
	public void setEmploymentStatus(String employmentStatus) {
		this.employmentStatus = employmentStatus;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public String getImmigrationStatus() {
		return immigrationStatus;
	}
	public void setImmigrationStatus(String immigrationStatus) {
		this.immigrationStatus = immigrationStatus;
	}
	public String getPerDiem() {
		return perDiem;
	}
	public void setPerDiem(String perDiem) {
		this.perDiem = perDiem;
	}
	public String getPaidTimeOff() {
		return paidTimeOff;
	}
	public void setPaidTimeOff(String paidTimeOff) {
		this.paidTimeOff = paidTimeOff;
	}
	public String getMedical() {
		return medical;
	}
	public void setMedical(String medical) {
		this.medical = medical;
	}
	public String getDentalVision() {
		return dentalVision;
	}
	public void setDentalVision(String dentalVision) {
		this.dentalVision = dentalVision;
	}
	public List<String> getRelocationBenefits() {
		return relocationBenefits;
	}
	public void setRelocationBenefits(List<String> relocationBenefits) {
		this.relocationBenefits = relocationBenefits;
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
	public String getC2cstreet1() {
		return c2cstreet1;
	}
	public void setC2cstreet1(String c2cstreet1) {
		this.c2cstreet1 = c2cstreet1;
	}
	public String getC2cstreet2() {
		return c2cstreet2;
	}
	public void setC2cstreet2(String c2cstreet2) {
		this.c2cstreet2 = c2cstreet2;
	}
	public String getC2ccity() {
		return c2ccity;
	}
	public void setC2ccity(String c2ccity) {
		this.c2ccity = c2ccity;
	}
	public String getC2cstate() {
		return c2cstate;
	}
	public void setC2cstate(String c2cstate) {
		this.c2cstate = c2cstate;
	}
	public String getC2ccountry() {
		return c2ccountry;
	}
	public void setC2ccountry(String c2ccountry) {
		this.c2ccountry = c2ccountry;
	}
	public String getC2czipCode() {
		return c2czipCode;
	}
	public void setC2czipCode(String c2czipCode) {
		this.c2czipCode = c2czipCode;
	}
	public String getBuyRate() {
		return buyRate;
	}
	public void setBuyRate(String buyRate) {
		this.buyRate = buyRate;
	}
	
	

}
