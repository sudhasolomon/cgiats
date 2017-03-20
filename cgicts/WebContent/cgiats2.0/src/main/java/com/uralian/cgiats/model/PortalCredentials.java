package com.uralian.cgiats.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.search.annotations.Indexed;


/**
 * @author Chaitanya
 *
 */
@Entity
@Table(name = "portal_credentials")
public class PortalCredentials extends AuditableEntity<Integer> implements Serializable 
{
	private static final long serialVersionUID = -2162621836299612953L;

	@Id
	@GeneratedValue
	@Column(name = "id")
	protected Integer id;

	@Column(name = "portal_user_Id")
	protected String portalUserId;

	@Column(name = "portal_password")
	private String portalPassword;

	@Column(name = "portal_Name")
	protected String portalName;
	
	@Column(name = "total_views")
	protected String totalViews;
	
	@Column(name = "completed_views")
	protected String completedViews;

	@Column(name = "remaining_views")
	protected String remainingViews;
	
	/**
	 */
	public PortalCredentials()
	{
	}

	/**
	 * @param candidateId
	 * @param orderId
	 * @param resumeStatus
	 * @param createdUser
	 * @param createdOn
	 * @param updatedBy
	 * @param updatedOn
	 */
	public PortalCredentials(Integer id,String portalUserId,String portalPassword,String portalName,String totalViews, String completedViews, String remainingViews )
	{
		this.id=id;
		this.portalUserId = portalUserId;
		this.portalPassword = portalPassword;
		this.portalName = portalName;
		this.totalViews= totalViews;
		this.completedViews=completedViews;
		this.remainingViews=remainingViews;
	}

	@Override
	protected Object getBusinessKey() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer getId() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	
	/**
	 * @return
	 */
	public Date getLastModified()
	{
		return getUpdatedOn() != null ? getUpdatedOn() : getCreatedOn();
	}


	public String getPortalUserId() {
		return portalUserId;
	}


	public String getPortalPassword() {
		return portalPassword;
	}


	public String getPortalName() {
		return portalName;
	}


	public void setPortalUserId(String portalUserId) {
		this.portalUserId = portalUserId;
	}


	public void setPortalPassword(String portalPassword) {
		this.portalPassword = portalPassword;
	}


	public void setPortalName(String portalName) {
		this.portalName = portalName;
	}

	public String getTotalViews() {
		return totalViews;
	}

	public String getCompletedViews() {
		return completedViews;
	}

	public String getRemainingViews() {
		return remainingViews;
	}

	public void setTotalViews(String totalViews) {
		this.totalViews = totalViews;
	}

	public void setCompletedViews(String completedViews) {
		this.completedViews = completedViews;
	}

	public void setRemainingViews(String remainingViews) {
		this.remainingViews = remainingViews;
	}

}