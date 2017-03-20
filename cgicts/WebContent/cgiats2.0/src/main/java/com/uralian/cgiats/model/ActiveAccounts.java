package com.uralian.cgiats.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

  
/**
 * @author rajashekhar
 */
@Entity
@Table(name = "active_accounts")
public class ActiveAccounts {
	
/*	@Id
	@SequenceGenerator(name = "generator", sequenceName = "active_account_seq")
	@GeneratedValue(strategy=GenerationType.SEQUENCE,generator = "generator")*/
	@Id
	@GeneratedValue
	@Column(name = "active_account_id")
	protected Integer activeAccountId;
	
	@Column(name = "company_name")
	protected String companyName;
	
	@Column(name = "account_manager")
	protected String accountManager;
	
	@Column(name = "dm")
	protected String DM;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "start_date")
	protected Date startDate;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "end_date")
	protected Date endDate;
	
	@Column(name = "email")
	protected String email;
	
	@Column(name = "phone")
    protected String phone;	
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "created_on")
	protected Date createdOn;
	
	@Column(name = "created_by")
	protected String  createdBy;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "updated_on")
	protected Date updatedOn;
	
	@Column(name = "updated_by")
	protected String updatedBy;
	
	
	@Column(name="delete_flag")
	protected Integer deleteFlag;
	
	
	@Column(name="comments")
    protected String  comments;
	
	

	public Integer getActiveAccountId() {
		return activeAccountId;
	}

	public void setActiveAccountId(Integer activeAccountId) {
		this.activeAccountId = activeAccountId;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getAccountManager() {
		return accountManager;
	}

	public void setAccountManager(String accountManager) {
		this.accountManager = accountManager;
	}

	public String getDM() {
		return DM;
	}

	public void setDM(String dM) {
		DM = dM;
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

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public Date getUpdatedOn() {
		return updatedOn;
	}

	public void setUpdatedOn(Date updatedOn) {
		this.updatedOn = updatedOn;
	}

	public String getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}

	public Integer getDeleteFlag() {
		return deleteFlag;
	}

	public void setDeleteFlag(Integer deleteFlag) {
		this.deleteFlag = deleteFlag;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	@Override
	public String toString() {
		return "ActiveAccounts [activeAccountId=" + activeAccountId
				+ ", companyName=" + companyName + ", accountManager="
				+ accountManager + ", DM=" + DM + ", startDate=" + startDate
				+ ", endDate=" + endDate + ", email=" + email + ", phone="
				+ phone + ", createdOn=" + createdOn + ", createdBy="
				+ createdBy + ", updatedOn=" + updatedOn + ", updatedBy="
				+ updatedBy + ", deleteFlag=" + deleteFlag + ", comments="
				+ comments + "]";
	}
}
