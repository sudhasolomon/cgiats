package com.uralian.cgiats.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.hibernate.search.annotations.Field;

import com.uralian.cgiats.util.Utils;

/**
 * This class encapsulates information on a candidate project details
 * 
 * @author Radhika
 */
@Entity
@Table(name = "candidate_info")

public class CandidateInfo extends AuditableEntity<Integer> implements Serializable
{
	private static final long serialVersionUID = -2162621836299612953L;
	
	@Id
	@SequenceGenerator(name = "generator", sequenceName = "candidate_info_candidateInfo_id_seq")
	@GeneratedValue
	@Column(name = "candidateInfo_id")
	protected Integer id;
	
	@ManyToOne(optional = false)
	@JoinColumn(name = "candidate_id")
	@ForeignKey(name = "fk_candidate_info_candidate")
	private Candidate candidate;
	
	@Field
	@Column(name = "first_name")
	protected String firstName;

	@Field
	@Column(name = "last_name")
	@Index(name = "candidate_lastname_idx")
	protected String lastName;
	
	@Column(name = "phone")
	protected String phone;

	@Column(name = "phone_alt")
	protected String phoneAlt;

	@Column(name = "email")
	protected String email;
	@Temporal(TemporalType.TIMESTAMP)
	
	@Column(name = "date_of_birth")
	private Date dateofbirth;
	
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
	
	@Column(name = "ssn")
	private String ssn;
	
	@Column(name = "source")
	private String source;
	
	@Column(name = "employment_status")
	private String employmentStatus;
	
	@Column(name = "immigration_status")
	private String immigrationStatus;
	
	@Column(name = "per_diem")
	private String perDiem;
	
	@Column(name = "salary_rate")
	private String salaryRate;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "start_date")
	private Date startDate;
	

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "end_date")
	private Date endDate;

	@Column(name = "paid_timeoff")
	private String paidTimeOff;
	
	@Column(name = "medical")
	private String medical;
	
	@Column(name = "dental_vision")
	private String dentalVision;
	
	@Column(name = "relocation_benefits")
	private String relocationBenefits;
	
	@Column(name = "expections")
	private String expections;

	@Column(name = "special_instructions")
	private String specialInstructions;
	
	@Column(name = "bdm_name")
	 private String bdmName;
	
	@Column(name ="bdm_lastname")
	private String bdmLastName;
	 
	 @Column(name = "recruiter_name")
	 private String recruiterName;
	 
	 public String getRecruiterLastName() {
		return recruiterLastName;
	}




	public void setRecruiterLastName(String recruiterLastName) {
		this.recruiterLastName = recruiterLastName;
	}

	@Column(name="recruiter_last_name")
	 private String recruiterLastName;
	 
	 @Column(name = "job_orderId")
	 private Integer jobOrderId;
	
	@Override
	public Integer getId() {
		// TODO Auto-generated method stub
		return id;
	}

	
	
	
	public String getBdmLastName() {
		return bdmLastName;
	}




	public void setBdmLastName(String bdmLastName) {
		this.bdmLastName = bdmLastName;
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

	public Date getDateofbirth() {
		return dateofbirth;
	}

	public void setDateofbirth(Date dateofbirth) {
		this.dateofbirth = dateofbirth;
	}

	public String getSsn() {
		return ssn;
	}

	public void setSsn(String ssn) {
		this.ssn = ssn;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getEmploymentStatus() {
		return employmentStatus;
	}

	public void setEmploymentStatus(String employmentStatus) {
		this.employmentStatus = employmentStatus;
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

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
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

	

	public String getRelocationBenefits() {
		return relocationBenefits;
	}

	public void setRelocationBenefits(String relocationBenefits) {
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

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getPhoneAlt() {
		return phoneAlt;
	}

	public void setPhoneAlt(String phoneAlt) {
		this.phoneAlt = phoneAlt;
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

	public String getSalaryRate() {
		return salaryRate;
	}

	public void setSalaryRate(String salaryRate) {
		this.salaryRate = salaryRate;
	}

	/**
	 * @return the bdmName
	 */
	public String getBdmName() {
		return bdmName;
	}

	/**
	 * @param bdmName the bdmName to set
	 */
	public void setBdmName(String bdmName) {
		this.bdmName = bdmName;
	}

	/**
	 * @return the recruiterName
	 */
	public String getRecruiterName() {
		return recruiterName;
	}

	/**
	 * @param recruiterName the recruiterName to set
	 */
	public void setRecruiterName(String recruiterName) {
		this.recruiterName = recruiterName;
	}

	/**
	 * @return the jobOrderId
	 */
	public Integer getJobOrderId() {
		return jobOrderId;
	}

	/**
	 * @param jobOrderId the jobOrderId to set
	 */
	public void setJobOrderId(Integer jobOrderId) {
		this.jobOrderId = jobOrderId;
	}

	/**
	 * @return
	 */
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

}
