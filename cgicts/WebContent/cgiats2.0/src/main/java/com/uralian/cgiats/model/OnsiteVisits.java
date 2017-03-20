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
 * @author Christian Rebollar
 */
@Entity
@Table(name = "onsite_visits")
public class OnsiteVisits extends AuditableEntity<Integer> 
{
	private static final long serialVersionUID = 6086775399753573849L;

	@Id
	@GeneratedValue
	@Column(name = "id")
	private Integer id;

	@Column(name = "dm_name")
	private String dmName;

	@Column(name = "client")
	private String client;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "visit_date")
	private Date visitDate;

	@Column(name = "purpose")  
	private String purpose;

	public Integer getId() {
		return id;
	}

	public String getDmName() {
		return dmName;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setDmName(String dmName) {
		this.dmName = dmName;
	}

	
	@Override
	protected Object getBusinessKey() {
		// TODO Auto-generated method stub
		return id;
	}

	public String getClient() {
		return client;
	}

	public Date getVisitDate() {
		return visitDate;
	}

	public String getPurpose() {
		return purpose;
	}

	public void setClient(String client) {
		this.client = client;
	}

	public void setVisitDate(Date visitDate) {
		this.visitDate = visitDate;
	}

	public void setPurpose(String purpose) {
		this.purpose = purpose;
	}

}