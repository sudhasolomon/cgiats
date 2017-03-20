/**
 * 
 */
package com.uralian.cgiats.model;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.search.annotations.Indexed;

/**
 * @author Parameshwar
 *
 */
@Entity
@Table(name ="login_attempts")
public class LoginAttempts implements Serializable {
	
	private static final long serialVersionUID = -6943667123062154813L;
	
	@Id
	@SequenceGenerator(name = "generator", sequenceName = "login_attempts_login_id_seq")
	@GeneratedValue(strategy=GenerationType.SEQUENCE,generator = "generator")
	@Column(name ="login_id")
	private Integer id;		
	
	@Column(name = "user_id")
	private String createdBy;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "created_date")
	private Date createdOn;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "login_date")
	private Date loginDate;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "logout_date")
	private Date logoutDate;	
	
	@Column(name = "total")
	private Long total;
	
	@Column(name = "duration")
	private String duration;
	
	private String status;
	
	private Long durationTime;	
	
	public LoginAttempts(){
		
	}

	/**
	 * @return the id
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Integer id) {
		this.id = id;
	}

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
	 * @return the loginDate
	 */
	public Date getLoginDate() {
		return loginDate;
	}

	/**
	 * @param loginDate the loginDate to set
	 */
	public void setLoginDate(Date loginDate) {
		this.loginDate = loginDate;
	}

	/**
	 * @return the logoutDate
	 */
	public Date getLogoutDate() {
		return logoutDate;
	}

	/**
	 * @param logoutDate the logoutDate to set
	 */
	public void setLogoutDate(Date logoutDate) {
		this.logoutDate = logoutDate;
	}	
	
	
	/**
	 * @return the total
	 */
	public Long getTotal() {
		return total;
	}

	/**
	 * @param total the total to set
	 */
	public void setTotal(Long total) {
		this.total = total;
	}
	
	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}
		
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	
	@Override
	public String toString() {
		return "LoginAttempts [id=" + id + ", createdBy=" + createdBy
				+ ", createdOn=" + createdOn + ", loginDate=" + loginDate
				+ ", logoutDate=" + logoutDate + ", total=" + total + "]";
	}

	/**
	 * @return the duration
	 */
	public String getDuration() {
		return duration;
	}

	/**
	 * @param duration the duration to set
	 */
	public void setDuration(String duration) {
		this.duration = duration;
	}

	/**
	 * @return the durationTime
	 */
	public Long getDurationTime() {
		return durationTime;
	}

	/**
	 * @param durationTime the durationTime to set
	 */
	public void setDurationTime(Long durationTime) {
		this.durationTime = durationTime;
	}

}
