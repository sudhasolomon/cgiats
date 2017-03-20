package com.uralian.cgiats.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.search.annotations.Indexed;


/**
 * @author Chaitanya
 *
 */
@Entity
@Table(name = "cb_user_views")
public class CBUserViews extends AuditableEntity<Integer> implements Serializable 
{
	private static final long serialVersionUID = -2162621836299612953L;

	@Id
	@GeneratedValue
	@Column(name = "id")
	protected Integer id;

	@Column(name = "portal_user_Id")
	protected String portalUserId;

	@Column(name = "portal_Name")
	protected String portalName;
	
	@Column(name = "total_views")
	protected String totalViews;
	
	@Column(name = "completed_views")
	protected String completedViews;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "view_date")
	protected Date viewDate;
	
	@Column(name="comments",length=255)
	protected String comments ;
	
	/**
	 */
	public CBUserViews()
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
	public CBUserViews(Integer id,String portalUserId,String portalName,String totalViews, String completedViews,Date viewDate)
	{
		this.id=id;
		this.portalUserId = portalUserId;
		this.portalName = portalName;
		this.totalViews= totalViews;
		this.completedViews=completedViews;
		this.viewDate=viewDate;
	}

	@Override
	protected Object getBusinessKey() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer getId() {
		// TODO Auto-generated method stub
		return id;
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

	public String getPortalName() {
		return portalName;
	}

	public void setPortalUserId(String portalUserId) {
		this.portalUserId = portalUserId;
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

	public void setTotalViews(String totalViews) {
		this.totalViews = totalViews;
	}

	public void setCompletedViews(String completedViews) {
		this.completedViews = completedViews;
	}

	public Date getViewDate() {
		return viewDate;
	}

	public void setViewDate(Date viewDate) {
		this.viewDate = viewDate;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

}