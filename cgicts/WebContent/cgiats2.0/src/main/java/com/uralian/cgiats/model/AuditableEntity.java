/*
 * AuditableEntity.java Jan 30, 2012
 * 
 * Copyright 2012 Uralian, LLC. All rights reserved.
 */
package com.uralian.cgiats.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.*;

import org.hibernate.annotations.Formula;

/**
 * @author Vlad Orzhekhovskiy
 */
@MappedSuperclass
public abstract class AuditableEntity<ID extends Serializable> extends
    AbstractEntity<ID>
{
	private static final long serialVersionUID = -7722544975069356302L;

	@Column(name = "created_by")
	private String createdBy;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "created_on")
	private Date createdOn;

	@Column(name = "updated_by")
	private String updatedBy;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "updated_on")
	private Date updatedOn;

	@Version
	@Column(name = "version")
	private long version;

	/**
	 * @return the createdBy.
	 */
	public String getCreatedBy()
	{
		return createdBy;
	}

	/**
	 * @param createdBy the createdBy to set.
	 */
	public void setCreatedBy(String createdBy)
	{
		this.createdBy = createdBy;
	}

	/**
	 * @return the createDate.
	 */
	public Date getCreatedOn()
	{
		return createdOn;
	}

	/**
	 * @param createDate the createDate to set.
	 */
	public void setCreatedOn(Date createDate)
	{
		this.createdOn = createDate;
	}

	/**
	 * @return the updatedBy.
	 */
	public String getUpdatedBy()
	{
		return updatedBy;
	}

	/**
	 * @param updatedBy the updatedBy to set.
	 */
	public void setUpdatedBy(String updatedBy)
	{
		this.updatedBy = updatedBy;
	}

	/**
	 * @return the updateDate.
	 */
	public Date getUpdatedOn()
	{
		return updatedOn;
	}

	/**
	 * @param updateDate the updateDate to set.
	 */
	public void setUpdatedOn(Date updateDate)
	{
		this.updatedOn = updateDate;
	}

	@Formula("coalesce(updated_on, created_on)")
	public Date getLastModified()
	{
		return updatedOn != null ? updatedOn : createdOn;
	}

	/**
	 * @return the version.
	 */
	public long getVersion()
	{
		return version;
	}

	/**
	 * @param version the version to set.
	 */
	public void setVersion(long version)
	{
		this.version = version;
	}
}