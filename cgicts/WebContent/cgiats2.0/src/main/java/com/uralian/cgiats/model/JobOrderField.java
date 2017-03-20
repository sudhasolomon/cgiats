package com.uralian.cgiats.model;

import javax.persistence.*;

/**
 * @author Christian Rebollar
 */
@Entity
@Table(name = "job_order_field")
public class JobOrderField extends AuditableEntity<Integer>
{
	private static final long serialVersionUID = -571115609230056251L;

	@Id
	@GeneratedValue
	@Column(name = "field_id")
	private Integer id;

	@Column(name = "field_name")
	private String fieldName;

	@Column(name = "field_value")
	private String fieldValue;

	@Column(name = "field_visible")
	private boolean visible;

	@ManyToOne(optional = false)
	@JoinColumn(name = "order_id")
	private JobOrder jobOrder;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.uralian.cgiats.model.AbstractEntity#getId()
	 */
	@Override
	public Integer getId()
	{
		return id;
	}

	/**
	 * @return
	 */
	public String getFieldName()
	{
		return fieldName;
	}

	/**
	 * @param fieldName
	 */
	public void setFieldName(String fieldName)
	{
		this.fieldName = fieldName;
	}

	/**
	 * @return
	 */
	public String getFieldValue()
	{
		return fieldValue;
	}

	/**
	 * @param fieldValue
	 */
	public void setFieldValue(String fieldValue)
	{
		this.fieldValue = fieldValue;
	}

	/**
	 * @return
	 */
	public boolean isVisible()
	{
		return visible;
	}

	/**
	 * @param visible
	 */
	public void setVisible(boolean visible)
	{
		this.visible = visible;
	}

	/**
	 * @return
	 */
	public JobOrder getJobOrder()
	{
		return jobOrder;
	}

	/**
	 * @param jobOrder
	 */
	public void setJobOrder(JobOrder jobOpening)
	{
		this.jobOrder = jobOpening;
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
}