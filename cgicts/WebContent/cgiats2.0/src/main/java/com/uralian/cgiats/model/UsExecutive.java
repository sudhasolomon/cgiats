package com.uralian.cgiats.model;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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
import javax.persistence.MapKeyColumn;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.hibernate.search.annotations.DateBridge;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.FieldBridge;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.IndexedEmbedded;
import org.hibernate.search.annotations.Resolution;
import org.hibernate.search.annotations.Store;

import com.uralian.cgiats.parser.TextParser;
import com.uralian.cgiats.parser.TextParserFactory;
import com.uralian.cgiats.util.MapToKeyValueBridge;
import com.uralian.cgiats.util.Utils;

@Entity
@Table(name = "us_executive")
public class UsExecutive implements Serializable{

	
	private static final long serialVersionUID = 421225595287620167L;
	
	@Id
	@GeneratedValue
	@Column(name = "executive_id")
	protected Integer id;

	@Column(name = "status")
	protected CandidateStatus status;

	@Column(name = "first_name")
	protected String firstName;

	@Column(name = "last_name")
	protected String lastName;

	@Column(name = "phone")
	protected String phone;

	@Column(name = "phone_alt")
	protected String phoneAlt;

	@Column(name = "email")
	protected String email;

	@Column(name = "category")
	protected String category;

	@Column(name = "title")
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
	protected Address address;

	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true, optional = false)
	@PrimaryKeyJoinColumn
	private ExecutiveResume executiveresume;

	@ElementCollection
	@CollectionTable(name = "executive_property", joinColumns = @JoinColumn(name = "executive_id"))
	@MapKeyColumn(name = "prop_name")
	@Column(name = "prop_value")
	@ForeignKey(name = "fk_property_executive")
	@FieldBridge(impl = MapToKeyValueBridge.class)
	private Map<String, String> properties = new HashMap<String, String>();

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

	@DateBridge(resolution=Resolution.DAY)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "created_on")
	private Date createdOn;

	@Column(name = "updated_by")
	private String updatedBy;
	
	@DateBridge(resolution=Resolution.DAY)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "updated_on")
	private Date updatedOn;
	
	
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
	
	@Column(name = "portal_resume_experience")
	private String portalResumeExperience;
	
	@Column(name = "portal_resume_last_comp")
	private String portalResumeLastComp;
	
	@Column(name = "portal_resume_last_position")
	private String portalResumeLastPosition;
	
	@Column(name = "portal_resume_qual")
	private String portalResumeQual;

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

	public void setPortalResumeLastPosition(String portalResumeLastPosition) {
		this.portalResumeLastPosition = portalResumeLastPosition;
	}

	public String getPortalResumeQual() {
		return portalResumeQual;
	}

	public void setPortalResumeQual(String portalResumeQual) {
		this.portalResumeQual = portalResumeQual;
	}
	
	public Date getLastModified()
	{
		return getUpdatedOn() != null ? getUpdatedOn() : getCreatedOn();
	}
	
	public String getPropertyValue(String property)
	{
		return this.properties.get(property);
	}

	public UsExecutive(Integer id, CandidateStatus status, String firstName,
			String lastName, String phone, String phoneAlt, String email,
			String category, String title, ContentType documentType,
			ContentType processedDocumentType, ContentType rtrDocumentType,
			Address address, ExecutiveResume executiveresume,
			Map<String, String> properties, Boolean hot, Boolean block,
			String visaType, Date visaExpiredDate, String createdUser,
			Date createdOn, String updatedBy, Date updatedOn, long version,
			String reference1, String reference2, String reference3,
			Integer deleteFlag, String portalResumeExperience,
			String portalResumeLastComp, String portalResumeLastPosition,
			String portalResumeQual, String presentRate, String expectedRate,
			String authFlag, JobType jobType, String employmentStatus) {
		super();
		this.id = id;
		this.status = status;
		this.firstName = firstName;
		this.lastName = lastName;
		this.phone = phone;
		this.phoneAlt = phoneAlt;
		this.email = email;
		this.category = category;
		this.title = title;
		this.documentType = documentType;
		this.processedDocumentType = processedDocumentType;
		this.rtrDocumentType = rtrDocumentType;
		this.address = address;
		this.executiveresume = executiveresume;
		this.properties = properties;
		this.hot = hot;
		this.block = block;
		this.visaType = visaType;
		this.visaExpiredDate = visaExpiredDate;
		this.createdUser = createdUser;
		this.createdOn = createdOn;
		this.updatedBy = updatedBy;
		this.updatedOn = updatedOn;
		this.version = version;
		this.reference1 = reference1;
		this.reference2 = reference2;
		this.reference3 = reference3;
		this.deleteFlag = deleteFlag;
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

	public JobType getJobType() {
		return jobType;
	}

	public void setJobType(JobType jobType) {
		this.jobType = jobType;
	}

	public CandidateStatus getStatus()
	{
		return status != null ? status : CandidateStatus.Available;
	}

	/**
	 * @param status the status to set.
	 */
	public void setStatus(CandidateStatus status)
	{
		this.status = status;
	}

	public UsExecutive() {
		executiveresume = new ExecutiveResume();
		executiveresume.setUsExecutive(this);
		//setVisaType(VisaType.H1B);
		address = new Address();
	}

	@Override
	public String toString() {
		return "UsExecutiveResumes [id=" + id + ", firstName=" + firstName
				+ ", lastName=" + lastName + ", title=" + title + "]";
	}
	
	public synchronized void parseDocument()
	{
		System.out.println("documentType>>>>>"+documentType);
		if (documentType != null && getDocument() != null)
		{
			TextParserFactory tpf = TextParserFactory.getInstance();
			TextParser parser = tpf.getParser(documentType);

			try
			{
				ByteArrayInputStream bais = new ByteArrayInputStream(getDocument());
				String parsedText = parser.parseText(bais);
				getExecutiveResume().setParsedText(parsedText);
			}
			catch (IOException e)
			{
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
	}

	public Map<String, String> getProperties()
	{
		return properties;
	}
	public void addProperties(Map<String, String> properties)
	{
		this.properties.putAll(properties);
	}
	

	/**
	 */
	public synchronized void parseProcessedDocument()
	{
		if (processedDocumentType != null && getProcessedDocument() != null)
		{
			TextParserFactory tpf = TextParserFactory.getInstance();
			TextParser parser = tpf.getParser(processedDocumentType);

			try
			{
				ByteArrayInputStream bais = new ByteArrayInputStream(getProcessedDocument());
				String parsedText = parser.parseText(bais);
				getExecutiveResume().setParsedText(parsedText);
			}
			catch (IOException e)
			{
				throw new RuntimeException(e);
			}
		}
	}

	/**
	 */
	public synchronized void parseRtrDocument()
	{
		if (rtrDocumentType != null && getRtrDocument() != null)
		{
			TextParserFactory tpf = TextParserFactory.getInstance();
			TextParser parser = tpf.getParser(rtrDocumentType);

			try
			{
				ByteArrayInputStream bais = new ByteArrayInputStream(getRtrDocument());
				String parsedText = parser.parseText(bais);
				getExecutiveResume().setParsedText(parsedText);
			}
			catch (IOException e)
			{
				throw new RuntimeException(e);
			}
		}
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

	/**
	 * Returns the city and state part of the address, something like
	 * "Atlanta, GA".
	 * 
	 * @return city and state of the address, if available.
	 */
	public String getCityState()
	{
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
	
	public byte[] getDocument()
	{
		return executiveresume.getDocument();
	}

	/**
	 * @param document the document to set.
	 */
	public void setDocument(byte[] document, ContentType documentType)
	{
		this.executiveresume.setDocument(document);
		this.documentType = documentType;
	}
	
	public byte[] getProcessedDocument()
	{
		return executiveresume.getProcessedDocument();
	}

	/**
	 * @param document the document to set.
	 */
	public void setProcessedDocument(byte[] document,ContentType processedDocumentType)
	{
		this.executiveresume.setProcessedDocument(document);
		this.processedDocumentType = processedDocumentType;
	}
	
	public byte[] getRtrDocument()
	{
		return executiveresume.getRtrDocument();
	}

	/**
	 * @param RtrDocument the RtrDocument to set.
	 */
	public void setRtrDocument(byte[] document,ContentType rtrDocumentType)
	{
		this.executiveresume.setRtrDocument(document);
		this.rtrDocumentType = rtrDocumentType;
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

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public ContentType getDocumentType() {
		return documentType;
	}

	public void setDocumentType(ContentType documentType) {
		this.documentType = documentType;
	}

	public ContentType getProcessedDocumentType() {
		return processedDocumentType;
	}

	public void setProcessedDocumentType(ContentType processedDocumentType) {
		this.processedDocumentType = processedDocumentType;
	}

	public ContentType getRtrDocumentType() {
		return rtrDocumentType;
	}

	public void setRtrDocumentType(ContentType rtrDocumentType) {
		this.rtrDocumentType = rtrDocumentType;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public ExecutiveResume getExecutiveResume() {
		return executiveresume;
	}

	public void setExecutiveResume(ExecutiveResume executiveresume) {
		this.executiveresume = executiveresume;
	}

	public Boolean getHot() {
		return hot;
	}

	public void setHot(Boolean hot) {
		this.hot = hot;
	}

	public Boolean getBlock() {
		return block;
	}

	public void setBlock(Boolean block) {
		this.block = block;
	}

	public String getVisaType() {
		return visaType;
	}

	public void setVisaType(String visaType) {
		this.visaType = visaType;
	}

	public Date getVisaExpiredDate() {
		return visaExpiredDate;
	}

	public void setVisaExpiredDate(Date visaExpiredDate) {
		this.visaExpiredDate = visaExpiredDate;
	}

	public String getCreatedUser() {
		return createdUser;
	}

	public void setCreatedUser(String createdUser) {
		this.createdUser = createdUser;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	public String getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}

	public Date getUpdatedOn() {
		return updatedOn;
	}

	public void setUpdatedOn(Date updatedOn) {
		this.updatedOn = updatedOn;
	}

	public long getVersion() {
		return version;
	}

	public void setVersion(long version) {
		this.version = version;
	}

	public String getReference1() {
		return reference1;
	}

	public void setReference1(String reference1) {
		this.reference1 = reference1;
	}

	public String getReference2() {
		return reference2;
	}

	public void setReference2(String reference2) {
		this.reference2 = reference2;
	}

	public String getReference3() {
		return reference3;
	}

	public void setReference3(String reference3) {
		this.reference3 = reference3;
	}

	public Integer getDeleteFlag() {
		return deleteFlag;
	}

	public void setDeleteFlag(Integer deleteFlag) {
		this.deleteFlag = deleteFlag;
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

	public String getAuthFlag() {
		return authFlag;
	}

	public void setAuthFlag(String authFlag) {
		this.authFlag = authFlag;
	}

	public String getEmploymentStatus() {
		return employmentStatus;
	}

	public void setEmploymentStatus(String employmentStatus) {
		this.employmentStatus = employmentStatus;
	}

}
