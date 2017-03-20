package com.uralian.cgiats.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.uralian.cgiats.util.Constants;
import com.uralian.cgiats.util.Utils;


@Entity
@Table(name = "india_job_order")
public class IndiaJobOrder extends AuditableEntity<Integer> implements Cloneable{

	
	private static final long serialVersionUID = 8471069528567973087L;

	@Id
	@GeneratedValue
	@Column(name = "order_id")
	private Integer id;

	@Column(name = "customer")
	private String customer;

	@Column(name = "hide_customer")
	private Boolean customerHidden;

	@Column(name = "city")
	private String city;

	@Column(name = "state")  
	private String state;

	@Column(name = "num_pos")
	private Integer numOfPos;
	
	@Column(name = "num_of_Resumes")
	private Integer numOfRsumes;


	@Enumerated(EnumType.STRING)
	@Column(name = "jobtype")
	private JobType jobType;

	@Column(name = "salary")
	private Integer salary;

	@Column(name = "perm_fee")
	private Integer permFee;

	@Column(name = "payrate")
	private Integer payrate;

	@Column(name = "acc_w2")
	private Boolean acceptW2;

	@Column(name = "hr_w2")
	private Integer hourlyRateW2;

	@Column(name = "an_w2")
	private Integer annualRateW2;

	@Column(name = "acc_1099")
	private Boolean accept1099;

	@Column(name = "hr_1099")
	private Integer hourlyRate1099;

	@Column(name = "acc_c2c")
	private Boolean acceptC2c;

	@Column(name = "hr_c2c")
	private Integer hourlyRateC2c;

	@Column(name = "start_date")
	@Temporal(TemporalType.DATE)
	private Date startDate;

	@Column(name = "end_date")
	@Temporal(TemporalType.DATE)
	private Date endDate;

	@Column(name = "assigned_to")
	private String assignedTo;
	
	@Column(name = "dmname")
	private String dmName;
	
	
	@ManyToOne
	@JoinColumn(name = "assigned_to_user")
	//@ForeignKey(name = "fk_order_user")
	private User assignedToUser;

	@Enumerated(EnumType.STRING)
	@Column(name = "priority", nullable = false)
	private JobOrderPriority priority;

	public void setFields(Map<String, IndiaJobOrderField> fields) {
		this.fields = fields;
	}

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false)
	private JobOrderStatus status;

	@OneToMany(mappedBy = "jobOrder", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<IndiaSubmittal> submittals = new HashSet<IndiaSubmittal>();

	@OneToMany(mappedBy = "indiaJobOrder", cascade = CascadeType.ALL, orphanRemoval = true)
	@Fetch(FetchMode.JOIN)
	@BatchSize(size = 1000)
	@MapKey(name = "fieldName")
	private Map<String, IndiaJobOrderField> fields = new HashMap<String, IndiaJobOrderField>();

	@Column(name = "category")
	private String category;
	
	@Column(name = "keySkills")
	private String keySkills;

	@Column(name = "title")
	private String title;

	//@Lob
	@Column(name = "description",length=21045)
	private String description;

	//@Lob
	@Column(name = "attachment")
	private byte[] attachment;

	@Column(name = "att_filename")
	private String attachmentFileName;

	@Column(name = "hrs_open")
	private Integer hoursToOpen;
	
	@Column(name = "delete_flg")
	private Integer deleteFlag;
	
	private Long days;
	
	private String client;

	private String onlineFlag;
	
	@Column(name = "em_name")
	private String emName;
	
	//@Lob
	@Column(name = "reason")
	private byte[] reason;
	
	@Column(name = "hot")
	private Boolean hot;
	
	@Column(name = "maxSalary")
	private Integer maxSalary;
	
	@Column(name = "minExperience")
	private String minExp;
	
	@Column(name = "maxExperience")
	private String maxExp;
	
	@Column(name = "education")
	private String education;
	
	/**
	 */
	public IndiaJobOrder()
	{
		
	}

	/**
	 * @param defaultValues
	 */
	public IndiaJobOrder(JobOrderDefaults defaultValues)
	{
		defaultValues.setIndiaJobsDefaultValues(this);
		status = JobOrderStatus.OPEN;
		priority = JobOrderPriority.MEDIUM;
		jobType = JobType.PERMANENT;
		numOfPos = 1;
		hoursToOpen = 24;
		onlineFlag="Yes";
	}
	
	public IndiaJobOrder(Integer id, String city, String state, Integer numOfPos, String title, String description,
			JobOrderStatus status, Date createdOn, Date updatedOn) {
		super();
		this.id = id;
		this.city = city;
		this.state = state;
		this.numOfPos = numOfPos;
		this.title = title;
		this.description = description;
		this.status = status;
		super.setCreatedOn(createdOn);
		super.setUpdatedOn(updatedOn);
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see com.uralian.cgiats.model.AbstractEntity#getId()
	 */
	
	public String getDmName() {
		return dmName;
	}

	public void setDmName(String dmName) {
		this.dmName = dmName;
	}

	public void setNumOfRsumes(Integer numOfRsumes) {
		this.numOfRsumes = numOfRsumes;
	}
	
	public Integer getNumOfRsumes() {
		return numOfRsumes;
	}
	
	@Override
	public Integer getId()
	{
		return id;
	}

	/**
	 * @return the fields.
	 */
	public Map<String, IndiaJobOrderField> getFields()
	{
		return fields;
	}
	

	/**
	 * @return
	 */
	public List<IndiaJobOrderField> getFieldList()
	{
		return new ArrayList<IndiaJobOrderField>(fields.values());
	}

	/**
	 * @return
	 */
	public List<IndiaJobOrderField> getNonBlankFieldList()
	{
		ArrayList<IndiaJobOrderField> list = new ArrayList<IndiaJobOrderField>();
		for (IndiaJobOrderField field : fields.values())
		{
			if (!Utils.isBlank(field.getFieldValue()))
				list.add(field);
		}
		return list;
	}

	/**
	 * @return
	 */
	public List<IndiaJobOrderField> getVisibleFieldList()
	{
		ArrayList<IndiaJobOrderField> list = new ArrayList<IndiaJobOrderField>();
		for (IndiaJobOrderField field : fields.values())
		{
			if (!Utils.isBlank(field.getFieldValue()) && field.isVisible())
				list.add(field);
		}
		return list;
	}

	/**
	 * @param token
	 * @param value
	 * @param visible
	 */
	public synchronized void addField(String token, String value, Boolean visible)
	{
		IndiaJobOrderField field = fields.get(token);

		if (field == null)
		{
			field = new IndiaJobOrderField();
			field.setFieldName(token);
			field.setFieldValue(value);
			field.setVisible(visible);
		}
		else
		{
			field.setFieldValue(value);
			field.setVisible(visible);
		}
		field.setIndiaJobOrder(this);
		fields.put(token, field);
	}

	/**
	 * @return
	 */
	
	
	
	
	public String getAssignedTo()
	{
		return assignedTo;
	}


	/*public void setCustomerHidden(Boolean customerHidden) {
		this.customerHidden = customerHidden;
	}*/

	public byte[] getReason() {
		return reason;
	}

	public void setReason(byte[] reason) {
		this.reason = reason;
	}

	public Boolean getHot() {
		return hot;
	}

	public void setHot(Boolean hot) {
		this.hot = hot;
	}

	/**
	 * @param assignedTo
	 */
	public void setAssignedTo(String assignedTo)
	{
		this.assignedTo = assignedTo;
	}

	/**
	 * @return
	 */
	public JobOrderPriority getPriority()
	{
		return priority;
	}

	/**
	 * @param priority
	 */
	public void setPriority(JobOrderPriority priority)
	{
		this.priority = priority;
	}

	/**
	 * @return
	 */
	public JobOrderStatus getStatus()
	{
		return status;
	}

	
	
	public User getAssignedToUser() {
		return assignedToUser;
	}

	public void setAssignedToUser(User assignedToUser) {
		this.assignedToUser = assignedToUser;
	}

	/**
	 * @param status
	 */
	public void setStatus(JobOrderStatus status)
	{
		this.status = status;
	}

	/**
	 * @return the hoursToOpen.
	 */
	public Integer getHoursToOpen()
	{
		return hoursToOpen != null ? hoursToOpen : 0;
	}

	/**
	 * @param hoursToOpen the hoursToOpen to set.
	 */
	public void setHoursToOpen(Integer hoursToOpen)
	{
		this.hoursToOpen = hoursToOpen;
	}

	/**
	 * @return the jobType.
	 */
	public JobType getJobType()
	{
		return jobType;
	}

	/**
	 * @param jobType the jobType to set.
	 */
	public void setJobType(JobType jobType)
	{
		this.jobType = jobType;
	}

	/**
	 * @return the salary.
	 */
	public Integer getSalary()
	{
		return salary;
	}

	/**
	 * @param salary the salary to set.
	 */
	public void setSalary(Integer salary)
	{
		this.salary = salary;
	}

	/**
	 * @return the permFee.
	 */
	public Integer getPermFee()
	{
		return permFee;
	}

	/**
	 * @param permFee the permFee to set.
	 */
	public void setPermFee(Integer permFee)
	{
		this.permFee = permFee;
	}

	/**
	 * @return the customer.
	 */
	public String getCustomer()
	{
		return customer;
	}

	/**
	 * @param customer the customer to set.
	 */
	public void setCustomer(String customer)
	{
		this.customer = customer;
	}

	/**
	 * @return the customerHidden.
	 */
	public Boolean getCustomerHidden()
	{
		return customerHidden!=null?customerHidden:false;
	}

	/**
	 * @param customerHidden the customerHidden to set.
	 */
	public void setCustomerHidden(Boolean customerHidden)
	{
		this.customerHidden = customerHidden;
	}

	/**
	 * @return the city.
	 */
	public String getCity()
	{
		return city;
	}

	/**
	 * @param city the city to set.
	 */
	public void setCity(String city)
	{
		this.city = city;
	}

	/**
	 * @return the state.
	 */
	public String getState()
	{
		return state;
	}

	/**
	 * @param state the state to set.
	 */
	public void setState(String state)
	{
		this.state = state;
	}

	/**
	 * @return
	 */
	public String getLocation()
	{
		return !Utils.isBlank(city) && !Utils.isBlank(state) ? city + ", " + state
		    : !Utils.isBlank(city) ? city : !Utils.isBlank(state) ? state : "";
	}

	/**
	 * @return the numOfPos.
	 */
	public Integer getNumOfPos()
	{
		return numOfPos;
	}

	/**
	 * @param numOfPos the numOfPos to set.
	 */
	public void setNumOfPos(Integer numOfPos)
	{
		this.numOfPos = numOfPos;
	}

	/**
	 * @return the category.
	 */
	public String getCategory()
	{
		return category;
	}

	/**
	 * @param category the category to set.
	 */
	public void setCategory(String category)
	{
		this.category = category;
	}

	/**
	 * @return the title.
	 */
	public String getTitle()
	{
		return title;
	}

	/**
	 * @param title the title to set.
	 */
	public void setTitle(String title)
	{
		this.title = title;
	}

	/**
	 * @return the attachment.
	 */
	public byte[] getAttachment()
	{
		return attachment;
	}

	/**
	 * @param attachment the attachment to set.
	 */
	public void setAttachment(byte[] attachment)
	{
		this.attachment = attachment;
	}

	/**
	 * @return
	 */
	public String getDescription()
	{
		return description;
	}

	/**
	 * @param description
	 */
	public void setDescription(String description)
	{
		this.description = description;
	}

	/**
	 * @return the attachmentFileName.
	 */
	public String getAttachmentFileName()
	{
		return attachmentFileName;
	}

	/**
	 * @param attachmentFileName the attachmentFileName to set.
	 */
	public void setAttachmentFileName(String attachmentFileName)
	{
		this.attachmentFileName = attachmentFileName;
	}

	/**
	 * @return the payrate.
	 */
	public Integer getPayrate()
	{
		return payrate;
	}

	/**
	 * @param payrate the payrate to set.
	 */
	public void setPayrate(Integer payrate)
	{
		this.payrate = payrate;
	}

	/**
	 * @return the acceptW2.
	 */
	public Boolean getAcceptW2(){
		return acceptW2!=null?acceptW2:false;
	}

	/**
	 * @param acceptW2 the acceptW2 to set.
	 */
	public void setAcceptW2(Boolean acceptW2)
	{
		this.acceptW2 = acceptW2;
	}

	/**
	 * @return the hourlyRateW2.
	 */
	public Integer getHourlyRateW2()
	{
		return hourlyRateW2;
	}

	/**
	 * @param hourlyRateW2 the hourlyRateW2 to set.
	 */
	public void setHourlyRateW2(Integer hourlyRateW2)
	{
		this.hourlyRateW2 = hourlyRateW2;
	}

	/**
	 * @return the annualRateW2.
	 */
	public Integer getAnnualRateW2()
	{
		return annualRateW2;
	}

	/**
	 * @param annualRateW2 the annualRateW2 to set.
	 */
	public void setAnnualRateW2(Integer annualRateW2)
	{
		this.annualRateW2 = annualRateW2;
	}

	/**
	 * @return the accept1099.
	 */
	public Boolean getAccept1099()
	{
		return accept1099!=null?accept1099:false;
	}

	/**
	 * @param accept1099 the accept1099 to set.
	 */
	public void setAccept1099(Boolean accept1099)
	{
		this.accept1099 = accept1099;
	}

	/**
	 * @return the hourlyRate1099.
	 */
	public Integer getHourlyRate1099()
	{
		return hourlyRate1099;
	}

	/**
	 * @param hourlyRate1099 the hourlyRate1099 to set.
	 */
	public void setHourlyRate1099(Integer hourlyRate1099)
	{
		this.hourlyRate1099 = hourlyRate1099;
	}

	/**
	 * @return the acceptC2c.
	 */
	public Boolean getAcceptC2c()
	{
		return acceptC2c!=null?acceptC2c:false;
	}

	/**
	 * @param acceptC2c the acceptC2c to set.
	 */
	public void setAcceptC2c(Boolean acceptC2c)
	{
		this.acceptC2c = acceptC2c;
	}

	/**
	 * @return the hourlyRateC2c.
	 */
	public Integer getHourlyRateC2c()
	{
		return hourlyRateC2c;
	}

	/**
	 * @param hourlyRateC2c the hourlyRateC2c to set.
	 */
	public void setHourlyRateC2c(Integer hourlyRateC2c)
	{
		this.hourlyRateC2c = hourlyRateC2c;
	}

	/**
	 * @return the startDate.
	 */
	public Date getStartDate()
	{
		return startDate;
	}

	/**
	 * @param startDate the startDate to set.
	 */
	public void setStartDate(Date startDate)
	{
		this.startDate = startDate;
	}

	/**
	 * @return the endDate.
	 */
	public Date getEndDate()
	{
		return endDate;
	}

	/**
	 * @param endDate the endDate to set.
	 */
	public void setEndDate(Date endDate)
	{
		this.endDate = endDate;
	}

	/**
	 * @return the submittals.
	 */
	public Set<IndiaSubmittal> getSubmittals()
	{
		return submittals;
	}
	
	/**
	 * @param submittals the submittals to set.
	 */
	public void setSubmittals(Set<IndiaSubmittal> orders)
	{
		this.submittals = orders;
	}
	/**
	 * @return
	 */
	public List<IndiaSubmittal> getSubmittalList()
	{
		List<IndiaSubmittal> subDetails = new ArrayList<IndiaSubmittal>();
		List<IndiaSubmittal> list = new ArrayList<IndiaSubmittal>(submittals);
		Collections.sort(list, new Comparator<IndiaSubmittal>()
		{
			public int compare(IndiaSubmittal o1, IndiaSubmittal o2)
			{
				return o2.getCreatedOn().compareTo(o1.getCreatedOn());
			}
		});
		Iterator<IndiaSubmittal> itr = list.iterator();
		while(itr.hasNext()) {
			IndiaSubmittal detail = (IndiaSubmittal)itr.next();
			if(detail.getDeleteFlag()==0){
				subDetails.add(detail);
			}
		}
		return subDetails;
	}

	

	/**
	 * @param order
	 */
	public void addSubmittal(IndiaSubmittal order)
	{
		this.submittals.add(order);
		order.setJobOrder(this);
	}

	/**
	 * @param order
	 */
	public void removeSubmittal(Submittal order)
	{
		this.submittals.remove(order);
		order.setJobOrder(null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.uralian.cgiats.model.AbstractEntity#getBusinessKey()
	 */
	@Override
	protected Object getBusinessKey()
	{
		return id;
	}
	
	
	/**
	 * @return the deleteFlag
	 */
	public Integer getDeleteFlag() {
		return deleteFlag;
	}

	/**
	 * @param deleteFlag the deleteFlag to set
	 */
	public void setDeleteFlag(Integer deleteFlag) {
		this.deleteFlag = deleteFlag;
	}


	public String getOnlineFlag() {
		return onlineFlag;
	}

	public void setOnlineFlag(String onlineFlag) {
		this.onlineFlag = onlineFlag;
	}

	public String getKeySkills() {
		return keySkills;
	}

	public void setKeySkills(String keySkills) {
		this.keySkills = keySkills;
	}

	public String getEmName() {
		return emName;
	}

	public void setEmName(String emName) {
		this.emName = emName;
	}
	
	
	
	public Integer getMaxSalary() {
		return maxSalary;
	}

	public void setMaxSalary(Integer maxSalary) {
		this.maxSalary = maxSalary;
	}

	public String getMinExp() {
		return minExp;
	}

	public void setMinExp(String minExp) {
		this.minExp = minExp;
	}

	public String getMaxExp() {
		return maxExp;
	}

	public void setMaxExp(String maxExp) {
		this.maxExp = maxExp;
	}

	public String getEducation() {
		return education;
	}

	public void setEducation(String education) {
		this.education = education;
	}

	/**
	 * @return the days
	 */
	public Long getDays() {
		
		/*if(getCreatedOn()!=null){
		  
			long diffTime=new Date().getTime()-getCreatedOn().getTime();
			this.days=TimeUnit.DAYS.convert(diffTime, TimeUnit.MILLISECONDS);
		}
		return days;*/
		
		Date d1 = this.getCreatedOn();
		Date d2 = new Date();

		SimpleDateFormat sd = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");

		Calendar cal1 = new GregorianCalendar();
		Calendar cal2 = new GregorianCalendar();

		SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
		Date date1 = null;
		Date date2 = null;
		try {
			date1 = sdf.parse(d1.toString());
			cal1.setTime(date1);

			date2 = sdf.parse(sd.format(d2));
			cal2.setTime(date2);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		long daysDiff = date2.getTime() - date1.getTime();
		long days = daysDiff / (24 * 60 * 60 * 1000);
		this.setDays(days + 1);

		return days;
	}

	/**
	 * @param days the days to set
	 */
	public void setDays(Long days) {
		this.days = days;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#clone()
	 */
	@Override
	public IndiaJobOrder clone()
	{
		IndiaJobOrder that = new IndiaJobOrder();
		that.setAccept1099(this.accept1099);
		that.setAcceptC2c(this.acceptC2c);
		that.setAcceptW2(this.acceptW2);
		that.setAnnualRateW2(this.annualRateW2);
		that.setAttachment(this.attachment);
		that.setAttachmentFileName(this.attachmentFileName);
		that.setCity(this.city);
		that.setCustomer(this.customer);
		that.setCreatedBy(Constants.USER_ID);
		that.setUpdatedOn(new Date());
		that.setCreatedOn(new Date());
		that.setDescription(this.description);
		that.setHourlyRate1099(this.hourlyRate1099);
		that.setHourlyRateC2c(this.hourlyRateC2c);
		that.setHourlyRateW2(this.hourlyRateW2);
		that.setJobType(this.jobType);
		that.setNumOfPos(this.numOfPos);
		that.setPayrate(this.payrate);
		that.setPriority(this.priority);
		that.setSalary(this.salary);
		that.setState(this.state);
		that.setStatus(this.status);
		that.setTitle(this.title);
		that.setStartDate(this.startDate);
		that.setDeleteFlag(this.deleteFlag);
		that.setKeySkills(this.keySkills);

		for (IndiaJobOrderField field : this.fields.values())
		{
			that.addField(field.getFieldName(), field.getFieldValue(),
			field.isVisible());
		}
		return that;
	}

	public String getClient() {
		if(getCustomerHidden())
			client="";
		else
			client=this.customer;
		return client;
	}

	public void setClient(String client) {
		this.client = client;
	}
}
