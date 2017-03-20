package com.uralian.cgiats.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "india_job_order_field")
public class IndiaJobOrderField extends AuditableEntity<Integer>{
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8702886175422888623L;

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
	private IndiaJobOrder indiaJobOrder;

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
	public IndiaJobOrder getIndiaJobOrder() {
		return indiaJobOrder;
	}

	/**
	 * @param jobOrder
	 */
	public void setIndiaJobOrder(IndiaJobOrder indiaJobOrder) {
		this.indiaJobOrder = indiaJobOrder;
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
