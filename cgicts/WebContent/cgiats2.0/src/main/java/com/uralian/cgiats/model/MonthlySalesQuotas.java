package com.uralian.cgiats.model;

import java.util.*;

import javax.persistence.*;

import org.hibernate.annotations.ForeignKey;

import com.uralian.cgiats.util.Utils;

/**
 * @author Christian Rebollar
 */
@Entity
@Table(name = "monthly_sales_quotas")
public class MonthlySalesQuotas extends AuditableEntity<Integer> 
{
	private static final long serialVersionUID = 6086775399753573849L;

	@Id
	@GeneratedValue
	@Column(name = "id")
	private Integer id;

	@Column(name = "dm_name")
	private String dmName;

	@Column(name = "sales_quota")
	private String salesQuota;

	@Column(name = "month")
	private String month;

	@Column(name = "user_role")  
	private String userRole;

	@Column(name = "year")  
	private String year;
	
	public Integer getId() {
		return id;
	}

	public String getDmName() {
		return dmName;
	}

	public String getSalesQuota() {
		return salesQuota;
	}

	public String getMonth() {
		return month;
	}

	public String getYear() {
		return year;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setDmName(String dmName) {
		this.dmName = dmName;
	}

	public void setSalesQuota(String salesQuota) {
		this.salesQuota = salesQuota;
	}

	public void setMonth(String month) {
		this.month = month;
	}

	public void setYear(String year) {
		this.year = year;
	}

	@Override
	protected Object getBusinessKey() {
		// TODO Auto-generated method stub
		return id;
	}

	public String getUserRole() {
		return userRole;
	}

	public void setUserRole(String userRole) {
		this.userRole = userRole;
	}


}