package com.uralian.cgiats.model;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
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
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.hibernate.search.annotations.DateBridge;
import org.hibernate.search.annotations.FieldBridge;
import org.hibernate.search.annotations.IndexedEmbedded;
import org.hibernate.search.annotations.Resolution;

import com.uralian.cgiats.parser.TextParser;
import com.uralian.cgiats.parser.TextParserFactory;
import com.uralian.cgiats.util.MapToKeyValueBridge;
import com.uralian.cgiats.util.Utils;

@XmlType(name = "candidate")
@XmlAccessorType(XmlAccessType.FIELD)
@Entity
@Table(name = "india_candidate")
public class IndiaCandidate {
	@Id
	@GeneratedValue
	@Column(name = "candidate_id")
	protected Integer id;

	@Column(name = "status")
	protected CandidateStatus status;

	@XmlElement
	@Column(name = "first_name")
	protected String firstName;

	@XmlElement
	@Column(name = "last_name")
	protected String lastName;

	@XmlElement
	@Column(name = "phone")
	protected String phone;

	@Column(name = "phone_alt")
	protected String phoneAlt;

	@XmlElement
	@Column(name = "email")
	protected String email;

	@Column(name = "category")
	@Index(name = "india_candidate_category_idx")
	protected String category;

	@XmlElement
	@Column(name = "title")
	protected String title;

	@XmlElement
	@Enumerated(EnumType.STRING)
	@Column(name = "doctype")
	protected ContentType documentType;

	@Enumerated(EnumType.STRING)
	@Column(name = "procdoctype")
	protected ContentType processedDocumentType;

	@Enumerated(EnumType.STRING)
	@Column(name = "rtrdoctype")
	protected ContentType rtrDocumentType;

	@XmlElement
	@Embedded
	protected Address address;
	
	@OneToMany(mappedBy = "indiacandidate", fetch = FetchType.EAGER)
	private Set<IndiaCandidateStatuses> statushistory;

	@XmlElement
	@IndexedEmbedded(prefix = "")
	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true, optional = false)
	@PrimaryKeyJoinColumn
	private IndiaResume resume;
	@ElementCollection
	@CollectionTable(name = "india_property", joinColumns = @JoinColumn(name = "candidate_id") )
	@MapKeyColumn(name = "prop_name")
	@Column(name = "prop_value")
	@ForeignKey(name = "fk_property_candidate")
	@FieldBridge(impl = MapToKeyValueBridge.class)
	private Map<String, String> properties = new HashMap<String, String>();
	@ManyToOne
	@JoinColumn(name = "submittal_id")
	@ForeignKey(name = "fk_candidate_submittal")
	private IndiaSubmittal submittal;
	@Column(name = "hot")
	private Boolean hot;

	@Column(name = "block")
	private Boolean block;

	@Column(name = "visa_type")
	protected String visaType;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "visa_expired_date")
	protected Date visaExpiredDate;

	@Column(name = "created_by")
	private String createdUser;

	@DateBridge(resolution = Resolution.DAY)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "created_on")
	private Date createdOn;

	@Column(name = "updated_by")
	private String updatedBy;

	@DateBridge(resolution = Resolution.DAY)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "updated_on")
	private Date updatedOn;

	@Column(name = "portal_resume_Id")
	private String portalResumeId;

	@Column(name = "portal_resume_lastUpd")
	private String portalResumeLastUpd;

	@Column(name = "portal_resume_experience")
	private String portalResumeExperience;

	@Column(name = "portal_resume_last_comp")
	private String portalResumeLastComp;

	@Column(name = "portal_resume_last_position")
	private String portalResumeLastPosition;

	@Column(name = "portal_resume_qual")
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
	private Integer deleteFlag;

	@Column(name = "portal_email")
	private String portalEmail;

	@Column(name = "portal_viewedBy")
	private String portalViewedBy;

	@Column(name = "present_rate")
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
	
	@Column(name = "other_resume_source")
	private String otherResumeSource;
	
	@Column(name = "relevant_experience")
	private String relevantExperience;
	
	
	@Column(name = "reason")
	private byte[] reason;
	
	public boolean isFlag() {
		return flag;
	}
	
	public void setFlag(boolean flag) {
		this.flag = flag;
	}

	@Transient
	private boolean flag = true;

	
	@Column(name = "key_skills")
	private String keySkill;

	/**
	 */
	public IndiaCandidate() {
		resume = new IndiaResume();
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
	public IndiaCandidate(Integer id, String firstName, String lastName, String phone, String phoneAlt, String email,
			String title, ContentType documentType, ContentType processedDocumentType, Address address,
			String createdUser, Date createdOn, String updatedBy, Date updatedOn, String portalResumeId,
			String portalResumeLastUpd, String portalResumeExperience, String portalResumeLastComp,
			String portalResumeLastPosition, String portalResumeQual, long version, String visaType,
			Date visaExpiredDate, String reference1, String reference2, String reference3, String portalEmail,
			String portalViewedBy, String presentRate, String expectedRate, String authFlag, JobType jobType,
			String employmentStatus) {
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
	}
	
	
	public void setReason(byte[] reason) {
		this.reason = reason;
	}
	
	public byte[] getReason() {
		return reason;
	}

	public String getKeySkill() {
		return keySkill;
	}

	public void setKeySkill(String keySkill) {
		this.keySkill = keySkill;
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
	public IndiaResume getResume() {
		return resume;
	}

	/**
	 * @param resume
	 *            the resume to set.
	 */
	public void setResume(IndiaResume resume) {
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
				throw new RuntimeException(e);
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
	public IndiaSubmittal getSubmittal() {
		return submittal;
	}

	/**
	 * @param submittal
	 *            the submittal to set.
	 */
	public void setSubmittal(IndiaSubmittal submittal) {
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
	
	public Set<IndiaCandidateStatuses> getStatushistory() {
		return statushistory;
	}

	public void setStatushistory(Set<IndiaCandidateStatuses> statushistory) {
		this.statushistory = statushistory;
	}

	public String getOtherResumeSource() {
		return otherResumeSource;
	}

	public void setOtherResumeSource(String otherResumeSource) {
		this.otherResumeSource = otherResumeSource;
	}

	public String getRelevantExperience() {
		return relevantExperience;
	}

	public void setRelevantExperience(String relevantExperience) {
		this.relevantExperience = relevantExperience;
	}



}
