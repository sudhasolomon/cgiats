package com.uralian.cgiats.model;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.MapKeyColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.hibernate.search.annotations.DocumentId;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.FieldBridge;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.IndexedEmbedded;
import org.hibernate.search.annotations.Store;
import org.hibernate.search.bridge.builtin.EnumBridge;

import com.uralian.cgiats.config.DateSplitBridge;
import com.uralian.cgiats.config.DateToStringConvertBridge;
import com.uralian.cgiats.parser.TextParser;
import com.uralian.cgiats.parser.TextParserFactory;
import com.uralian.cgiats.util.ByteFieldBridge;
import com.uralian.cgiats.util.MapToKeyValueBridge;
import com.uralian.cgiats.util.Utils;

/**
 * This class encapsulates information on a searcheable resume. It includes some
 * personal candidate's information, the original resume data, and a list of
 * keywords to search for.
 * 
 * @author Christian Rebollar
 */
@Entity
@Table(name = "candidate")
@Indexed
public class Candidate implements Serializable {
	private static final long serialVersionUID = -2162621836299612953L;

	@Id
	@GeneratedValue
	@Column(name = "candidate_id")
	@DocumentId
	protected Integer id;

	@Column(name = "status")
	@Field(store = Store.YES, bridge = @FieldBridge(impl = EnumBridge.class) )
	@Enumerated(EnumType.ORDINAL)
	protected CandidateStatus status;

	@Field(store = Store.YES)
	@Column(name = "first_name")
	@Index(name = "candidate_firstname_idx")
	protected String firstName;

	@Field(store = Store.YES)
	@Column(name = "last_name")
	@Index(name = "candidate_lastname_idx")
	protected String lastName;

	@Column(name = "phone")
	@Field(store = Store.YES)
	protected String phone;

	@Column(name = "phone_alt")
	protected String phoneAlt;

	@Field(store = Store.YES)
	@Column(name = "email")
	protected String email;

	@Field(store = Store.YES)
	@Column(name = "category")
	@Index(name = "candidate_category_idx")
	protected String category;

	@Field(store = Store.YES)
	@Column(name = "title")
	@Index(name = "candidate_title_idx")
	protected String title;

	@Enumerated(EnumType.STRING)
	@Column(name = "doctype")
	protected ContentType documentType;

	@Enumerated(EnumType.STRING)
	@Column(name = "procdoctype")
	protected ContentType processedDocumentType;

	@Enumerated(EnumType.STRING)
	@Column(name = "rtrdoctype")
	protected ContentType rtrDocumentType;

	@Embedded
	@IndexedEmbedded(prefix = "")
	protected Address address;

	@IndexedEmbedded(prefix = "")
	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true, optional = false)
	@PrimaryKeyJoinColumn
	private Resume resume;

	@ElementCollection
	@CollectionTable(name = "property", joinColumns = @JoinColumn(name = "candidate_id") )
	@MapKeyColumn(name = "prop_name")
	@Column(name = "prop_value")
	@ForeignKey(name = "fk_property_candidate")
	@Field
	@FieldBridge(impl = MapToKeyValueBridge.class)
	private Map<String, String> properties = new HashMap<String, String>();

	@ManyToOne
	@JoinColumn(name = "submittal_id")
	@ForeignKey(name = "fk_candidate_submittal")
	@Fetch(FetchMode.SELECT)
	@BatchSize(size = 100)
	private Submittal submittal;

	@Column(name = "hot")
	@Field(store = Store.YES)
	private Boolean hot;

	@Column(name = "block")
	@Field(store = Store.YES)
	private Boolean block;

	@Field(store = Store.YES)
	@Column(name = "visa_type")
	protected String visaType;

	@Field(store = Store.YES)
	@Column(name = "security_clearance")
	protected Boolean securityClearance;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "visa_expired_date")
	protected Date visaExpiredDate;
	/**
	 * This is the Source of the resume
	 */
	@Index(name = "created_by_idx")
	@Column(name = "created_by")
	private String createdUser;

	@Field(store = Store.YES)
//	 @DateBridge(resolution = Resolution.DAY)
	@FieldBridge(impl = DateToStringConvertBridge.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "created_on")
	private Date createdOn;

	@Column(name = "updated_by")
	private String updatedBy;

	@Field(store = Store.YES)
	// @DateBridge(resolution = Resolution.DAY)
	@FieldBridge(impl = DateSplitBridge.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "updated_on")
	private Date updatedOn;

	@Column(name = "portal_resume_Id")
	private String portalResumeId;

	@Column(name = "portal_resume_lastUpd")
	private String portalResumeLastUpd;

	@Column(name = "portal_resume_experience")
	@Field(store = Store.YES)
	private String portalResumeExperience;

	@Column(name = "portal_resume_last_comp")
	private String portalResumeLastComp;

	@Column(name = "portal_resume_last_position")
	private String portalResumeLastPosition;

	@Column(name = "portal_resume_qual")
	@Field(store = Store.YES)
	private String portalResumeQual;

	@Version
	@Column(name = "version")
	private long version;

	@Column(name = "reference1")
	private String reference1;

	@Column(name = "reference2")
	private String reference2;

	@Column(name = "reference3")
	private String reference3;

	@Column(name = "delete_flg")
	@Field
	private Integer deleteFlag;

	@Column(name = "portal_email")
	private String portalEmail;
	/**
	 * This is the value of uploaded by field in UI
	 */
	@Column(name = "portal_viewedBy")
	private String portalViewedBy;

	@Column(name = "present_rate")
	@Field(store = Store.YES)
	private String presentRate;

	@Column(name = "expected_rate")
	private String expectedRate;

	@Column(name = "auth_flag")
	private String authFlag;

	@Enumerated(EnumType.STRING)
	@Column(name = "jobtype")
	private JobType jobType;

	@Column(name = "employment_status")
	private String employmentStatus;

	@Column(name = "visa_transfer")
	private String visaTransfer;

	@Column(name = "min_salary")
	private String minimumSalary;

	@Column(name = "travel")
	private String travel;
	@Field(store = Store.YES)
	@Column(name = "key_skills")
	private String keySkill;
	
	@Column(name = "other_resume_source")
	private String otherResumeSource;

	@Lob
	@Field(store = Store.YES)
	@FieldBridge(impl = ByteFieldBridge.class)
	@Column(name = "reason")
	private byte[] reason;

	@Field(store = Store.YES)
	@Column(name = "skills")
	private String skills;

	@OneToMany(mappedBy = "candidate", fetch = FetchType.EAGER)
	private Set<CandidateStatuses> statushistory;

	@Transient
	private List<CandidateStatuses> statusList;

	@Transient
	private boolean flag = true;

	/**
	 */
	public Candidate() {
		resume = new Resume();
		resume.setCandidate(this);
		// setVisaType(VisaType.H1B);
		address = new Address();
	}

	/**
	 * @param id
	 * @param firstName
	 * @param lastName
	 * @param phone
	 * @param phoneAlt
	 * @param email
	 * @param title
	 * @param documentType
	 * @param address
	 * @param createdUser
	 * @param createdOn
	 * @param updatedBy
	 * @param updatedOn
	 * @param version
	 */
	public Candidate(Integer id, String firstName, String lastName, String phone, String phoneAlt, String email, String title, ContentType documentType,
			ContentType processedDocumentType, Address address, String createdUser, Date createdOn, String updatedBy, Date updatedOn, String portalResumeId,
			String portalResumeLastUpd, String portalResumeExperience, String portalResumeLastComp, String portalResumeLastPosition, String portalResumeQual,
			long version, String visaType, Boolean securityClearance, Date visaExpiredDate, String reference1, String reference2, String reference3,
			String portalEmail, String portalViewedBy, String presentRate, String expectedRate, String authFlag, JobType jobType, String employmentStatus,
			String visaTransfer, String minimumSalary, String travel, String keySkills, String skills) {
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
		this.phone = phone;
		this.phoneAlt = phoneAlt;
		this.email = email;
		this.title = title;
		this.documentType = documentType;
		this.processedDocumentType = processedDocumentType;
		this.address = address;
		this.visaType = visaType;
		this.securityClearance = securityClearance;
		this.visaExpiredDate = visaExpiredDate;
		this.createdUser = createdUser;
		this.createdOn = createdOn;
		this.updatedBy = updatedBy;
		this.updatedOn = updatedOn;
		this.portalResumeId = portalResumeId;
		this.version = version;
		this.reference1 = reference1;
		this.reference2 = reference2;
		this.reference3 = reference3;
		this.portalEmail = portalEmail;
		this.portalViewedBy = portalViewedBy;
		this.portalResumeLastUpd = portalResumeLastUpd;
		this.portalResumeExperience = portalResumeExperience;
		this.portalResumeLastComp = portalResumeLastComp;
		this.portalResumeLastPosition = portalResumeLastPosition;
		this.portalResumeQual = portalResumeQual;
		this.presentRate = presentRate;
		this.expectedRate = expectedRate;
		this.authFlag = authFlag;
		this.jobType = jobType;
		this.employmentStatus = employmentStatus;
		this.visaTransfer = visaTransfer;
		this.minimumSalary = minimumSalary;
		this.travel = travel;
		this.skills = skills;
	}

	public Boolean isSecurityClearance() {
		return securityClearance != null ? securityClearance.booleanValue() : false;
	}

	public void setSecurityClearance(boolean securityClearance) {
		this.securityClearance = securityClearance;
	}

	public String getSkills() {
		return skills;
	}

	public void setSkills(String skills) {
		this.skills = skills;
	}

	public String getVisaTransfer() {
		return visaTransfer;
	}

	public void setVisaTransfer(String visaTransfer) {
		this.visaTransfer = visaTransfer;
	}

	/**
	 * @return the status.
	 */
	public CandidateStatus getStatus() {
		return status != null ? status : CandidateStatus.Available;
	}

	/**
	 * @param status
	 *            the status to set.
	 */
	public void setStatus(CandidateStatus status) {
		this.status = status;
	}

	/**
	 * @return
	 */

	public String getFirstName() {
		return firstName;
	}

	/**
	 * @param firstName
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	/**
	 * @return
	 */

	public String getLastName() {
		return lastName;
	}

	/**
	 * @param lastName
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	/**
	 * @return
	 */
	public String getPhone() {
		return phone;
	}

	/**
	 * @param phone
	 */
	public void setPhone(String phone) {
		this.phone = phone;
	}

	/**
	 * @return
	 */
	public String getPhoneAlt() {
		return phoneAlt;
	}

	/**
	 * @param phoneAlt
	 */
	public void setPhoneAlt(String phoneAlt) {
		this.phoneAlt = phoneAlt;
	}

	/**
	 * @return
	 */
	public ContentType getProcessedDocumentType() {
		return processedDocumentType;
	}

	/**
	 * @param processedDocumentType
	 */

	public void setProcessedDocumentType(ContentType processedDocumentType) {
		this.processedDocumentType = processedDocumentType;
	}

	/**
	 * @return
	 */

	public ContentType getRtrDocumentType() {
		return rtrDocumentType;
	}

	/**
	 * @param rtrDocumentType
	 */

	public void setRtrDocumentType(ContentType rtrDocumentType) {
		this.rtrDocumentType = rtrDocumentType;
	}

	/**
	 * @return
	 */

	public String getEmail() {
		return email;
	}

	/**
	 * @param email
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * @return
	 */
	public Address getAddress() {
		return address;
	}

	/**
	 * @param address
	 */
	public void setAddress(Address address) {
		this.address = address;
	}

	/**
	 * @return the resume.
	 */
	public Resume getResume() {
		return resume;
	}

	/**
	 * @param resume
	 *            the resume to set.
	 */
	public void setResume(Resume resume) {
		this.resume = resume;
		resume.setCandidate(this);
	}

	/**
	 * @return the documentType.
	 */
	public ContentType getDocumentType() {
		return documentType;
	}

	/**
	 * @return the document.
	 */
	public byte[] getDocument() {
		return resume.getDocument();
	}

	/**
	 * @param document
	 *            the document to set.
	 */
	public void setDocument(byte[] document, ContentType documentType) {
		this.resume.setDocument(document);
		this.documentType = documentType;
	}

	/**
	 * @return the ProcessedDocument.
	 */
	public byte[] getProcessedDocument() {
		return resume.getProcessedDocument();
	}

	/**
	 * @param document
	 *            the document to set.
	 */
	public void setProcessedDocument(byte[] document, ContentType processedDocumentType) {
		this.resume.setProcessedDocument(document);
		this.processedDocumentType = processedDocumentType;
	}

	/**
	 * @return the RtrDocument.
	 */
	public byte[] getRtrDocument() {
		return resume.getRtrDocument();
	}

	/**
	 * @param RtrDocument
	 *            the RtrDocument to set.
	 */
	public void setRtrDocument(byte[] document, ContentType rtrDocumentType) {
		this.resume.setRtrDocument(document);
		this.rtrDocumentType = rtrDocumentType;
	}

	/**
	 * @return the category.
	 */
	public String getCategory() {
		return category;
	}

	/**
	 * @param category
	 *            the category to set.
	 */
	public void setCategory(String category) {
		this.category = category;
	}

	/**
	 * @return
	 */

	public String getTitle() {
		return title;
	}

	/**
	 * @param title
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return
	 */
	public Date getLastModified() {
		return getUpdatedOn() != null ? getUpdatedOn() : getCreatedOn();
	}

	/**
	 * @param jobType
	 */

	public JobType getJobType() {
		return jobType;
	}

	/**
	 * @return
	 */

	public void setJobType(JobType jobType) {
		this.jobType = jobType;
	}

	public String getMinimumSalary() {
		return minimumSalary;
	}

	public void setMinimumSalary(String minimumSalary) {
		this.minimumSalary = minimumSalary;
	}

	public String getTravel() {
		return travel;
	}

	public void setTravel(String travel) {
		this.travel = travel;
	}

	/**
	 */
	public synchronized void parseDocument() {
		System.out.println("documentType>>>>>" + documentType);
		if (documentType != null && getDocument() != null) {
			TextParserFactory tpf = TextParserFactory.getInstance();
			TextParser parser = tpf.getParser(documentType);

			try {
				ByteArrayInputStream bais = new ByteArrayInputStream(getDocument());
				String parsedText = parser.parseText(bais);
				getResume().setParsedText(parsedText);
			} catch (IOException e) {
				e.printStackTrace();

			}
		}
	}

	/**
	 */
	public synchronized void parseProcessedDocument() {
		if (processedDocumentType != null && getProcessedDocument() != null) {
			TextParserFactory tpf = TextParserFactory.getInstance();
			TextParser parser = tpf.getParser(processedDocumentType);

			try {
				ByteArrayInputStream bais = new ByteArrayInputStream(getProcessedDocument());
				String parsedText = parser.parseText(bais);
				getResume().setParsedText(parsedText);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}

	/**
	 */
	public synchronized void parseRtrDocument() {
		if (rtrDocumentType != null && getRtrDocument() != null) {
			TextParserFactory tpf = TextParserFactory.getInstance();
			TextParser parser = tpf.getParser(rtrDocumentType);

			try {
				ByteArrayInputStream bais = new ByteArrayInputStream(getRtrDocument());
				String parsedText = parser.parseText(bais);
				getResume().setParsedText(parsedText);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}

	/**
	 * @return
	 */
	public String getFullName() {
		StringBuffer sb = new StringBuffer();
		if (!Utils.isBlank(firstName))
			sb.append(firstName);
		sb.append(" ");
		if (!Utils.isBlank(lastName))
			sb.append(lastName);

		String fullName = sb.toString().trim();
		return fullName;
	}

	/**
	 * Returns the city and state part of the address, something like
	 * "Atlanta, GA".
	 * 
	 * @return city and state of the address, if available.
	 */
	public String getCityState() {
		if (address == null)
			return "";

		StringBuffer sb = new StringBuffer();
		if (!Utils.isBlank(address.getCity()))
			sb.append(address.getCity());
		if (!Utils.isBlank(address.getCity()) && !Utils.isBlank(address.getState()))
			sb.append(", ");
		if (!Utils.isBlank(address.getState()))
			sb.append(address.getState());

		String cityState = sb.toString();
		return cityState;
	}

	/**
	 * @return
	 */
	public Map<String, String> getProperties() {
		return properties;
	}

	/**
	 * @param property
	 * @return
	 */
	public String getPropertyValue(String property) {
		return this.properties.get(property);
	}

	/**
	 * @param property
	 * @param value
	 */
	public void addProperty(String property, String value) {
		this.properties.put(property, value);
	}

	/**
	 * @param properties
	 */
	public void addProperties(Map<String, String> properties) {
		this.properties.putAll(properties);
	}

	/**
	 * @return the submittal.
	 */
	public Submittal getSubmittal() {
		return submittal;
	}

	/**
	 * @param submittal
	 *            the submittal to set.
	 */
	public void setSubmittal(Submittal submittal) {
		this.submittal = submittal;
	}

	/**
	 * @return the hot.
	 */
	public boolean isHot() {
		return hot != null ? hot.booleanValue() : false;
	}

	/**
	 * @param hot
	 *            the hot to set.
	 */
	public void setHot(boolean hot) {
		this.hot = hot;
	}

	/**
	 * @return the block.
	 */
	public boolean isBlock() {
		return block != null ? block.booleanValue() : false;
	}

	/**
	 * @param block
	 *            the block to set.
	 */
	public void setBlock(boolean block) {
		this.block = block;
	}

	/**
	 * @return the visaExpiredDate.
	 */

	public Date getVisaExpiredDate() {
		return visaExpiredDate;
	}

	/**
	 * @param visaExpiredDate
	 *            the visaExpiredDate to set.
	 */

	public void setVisaExpiredDate(Date visaExpiredDate) {
		this.visaExpiredDate = visaExpiredDate;
	}

	/**
	 * @return the visaType.
	 */

	public String getVisaType() {
		if (visaType == null) {
			visaType = "";
		}
		return visaType;
	}

	/**
	 * @param visaType
	 *            the visaType to set.
	 */

	public void setVisaType(String visaType) {
		this.visaType = visaType;
	}

	/**
	 * @return the id
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * @return the createdUser
	 */
	public String getCreatedUser() {
		return createdUser;
	}

	/**
	 * @param createdUser
	 *            the createdUser to set
	 */
	public void setCreatedUser(String createdUser) {
		this.createdUser = createdUser;
	}

	/**
	 * @return the createdOn
	 */

	public Date getCreatedOn() {
		return createdOn;
	}

	/**
	 * @param createdOn
	 *            the createdOn to set
	 */
	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	/**
	 * @return the updatedBy
	 */
	public String getUpdatedBy() {
		return updatedBy;
	}

	/**
	 * @param updatedBy
	 *            the updatedBy to set
	 */
	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}

	/**
	 * @return the updatedOn
	 */
	public Date getUpdatedOn() {
		return updatedOn;
	}

	/**
	 * @param updatedOn
	 *            the updatedOn to set
	 */
	public void setUpdatedOn(Date updatedOn) {
		this.updatedOn = updatedOn;
	}

	/**
	 * @return the version
	 */
	public long getVersion() {
		return version;
	}

	/**
	 * @param version
	 *            the version to set
	 */
	public void setVersion(long version) {
		this.version = version;
	}

	/**
	 * @return the reference1
	 */

	public String getReference1() {
		return reference1;
	}

	/**
	 * @param version
	 *            the reference1 to set
	 */

	public void setReference1(String reference1) {
		this.reference1 = reference1;
	}

	/**
	 * @return the reference2
	 */

	public String getReference2() {
		return reference2;
	}

	/**
	 * @param version
	 *            the reference2 to set
	 */

	public void setReference2(String reference2) {
		this.reference2 = reference2;
	}

	/**
	 * @return the reference3
	 */

	public String getReference3() {
		return reference3;
	}

	/**
	 * @param version
	 *            the reference3 to set
	 */

	public void setReference3(String reference3) {
		this.reference3 = reference3;
	}

	/**
	 * @return the deleteFlag
	 */

	public Integer getDeleteFlag() {
		return deleteFlag;
	}

	/**
	 * @param deleteFlag
	 *            the deleteFlag to set
	 */
	public void setDeleteFlag(Integer deleteFlag) {
		this.deleteFlag = deleteFlag;
	}

	public String getPortalEmail() {
		return portalEmail;
	}

	public void setPortalEmail(String portalEmail) {
		this.portalEmail = portalEmail;
	}

	/**
	 * @return the portalViewedBy
	 */

	public String getPortalViewedBy() {
		return portalViewedBy;
	}

	/**
	 * @param portalViewedBy
	 *            the portalViewedBy to set
	 */

	public void setPortalViewedBy(String portalViewedBy) {
		this.portalViewedBy = portalViewedBy;
	}

	/**
	 * @return the employmentStatus
	 */

	public String getEmploymentStatus() {
		return employmentStatus;
	}

	/**
	 * @param employmentStatus
	 *            the employmentStatus to set
	 */
	public void setEmploymentStatus(String employmentStatus) {
		this.employmentStatus = employmentStatus;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Candidate [id=" + id + ", firstName=" + firstName + ", lastName=" + lastName + ", title=" + title + "]";
	}

	public String getPresentRate() {
		return presentRate;
	}

	public String getExpectedRate() {
		return expectedRate;
	}

	public void setPresentRate(String presentRate) {
		this.presentRate = presentRate;
	}

	public void setExpectedRate(String expectedRate) {
		this.expectedRate = expectedRate;
	}

	public String getAuthFlag() {
		return authFlag;
	}

	public void setAuthFlag(String authFlag) {
		this.authFlag = authFlag;
	}

	public String getPortalResumeId() {
		return portalResumeId;
	}

	public void setPortalResumeId(String portalResumeId) {
		this.portalResumeId = portalResumeId;
	}

	public String getPortalResumeLastUpd() {
		return portalResumeLastUpd;
	}

	public void setPortalResumeLastUpd(String portalResumeLastUpd) {
		this.portalResumeLastUpd = portalResumeLastUpd;
	}

	public String getPortalResumeExperience() {
		return portalResumeExperience;
	}

	public void setPortalResumeExperience(String portalResumeExperience) {
		this.portalResumeExperience = portalResumeExperience;
	}

	public String getPortalResumeLastComp() {
		return portalResumeLastComp;
	}

	public void setPortalResumeLastComp(String portalResumeLastComp) {
		this.portalResumeLastComp = portalResumeLastComp;
	}

	public String getPortalResumeLastPosition() {
		return portalResumeLastPosition;
	}

	public String getPortalResumeQual() {
		return portalResumeQual;
	}

	public void setPortalResumeLastPosition(String portalResumeLastPosition) {
		this.portalResumeLastPosition = portalResumeLastPosition;
	}

	public void setPortalResumeQual(String portalResumeQual) {
		this.portalResumeQual = portalResumeQual;
	}

	/**
	 * @return the keySkill
	 */
	public String getKeySkill() {
		return keySkill;
	}

	/**
	 * @param keySkill
	 *            the keySkill to set
	 */
	public void setKeySkill(String keySkill) {
		this.keySkill = keySkill;
	}

	public byte[] getReason() {
		return reason;
	}

	public void setReason(byte[] reason) {
		this.reason = reason;
	}

	public String getComments() {
		String str;
		if (getReason() != null) {

			str = new String(getReason());
			return str;

		} else
			return "";
	}

	public Set<CandidateStatuses> getStatushistory() {

		return statushistory;
	}

	public void setStatushistory(Set<CandidateStatuses> statushistory) {
		this.statushistory = statushistory;
	}

	public boolean isFlag() {
		return flag;
	}

	public void setFlag(boolean flag) {
		this.flag = flag;
	}

	public List<CandidateStatuses> getStatuses() {

		return new ArrayList<CandidateStatuses>(getStatushistory());
	}

	public List<CandidateStatuses> getStatusList() {
		return new ArrayList<CandidateStatuses>(getStatushistory());
	}

	public void setStatusList(List<CandidateStatuses> statusList) {

		this.statusList = statusList;
	}

	public String getOtherResumeSource() {
		return otherResumeSource;
	}

	public void setOtherResumeSource(String otherResumeSource) {
		this.otherResumeSource = otherResumeSource;
	}

}