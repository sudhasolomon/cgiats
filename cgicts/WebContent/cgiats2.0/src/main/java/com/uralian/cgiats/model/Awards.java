/**
 * 
 */
package com.uralian.cgiats.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.*;

//import org.hibernate.annotations.Entity;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.search.annotations.Indexed;

/**
 * @author Parameshwar
 *
 */

@Entity
@Table(name ="awards")
public class Awards extends AuditableEntity<Integer> 
{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6125840677990427941L;
	@Id
	@GeneratedValue
	@Column(name ="award_id")
	private Integer awardId;
	
	@ManyToOne
	@JoinColumn(name = "user_id")	
	private User userId;
	
	@Column(name ="award_name")
	private String awardName;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name ="award_date")
	private Date awardDate;
	
	@Column(name ="award_desc")
	private String awardDesc;
	
	
	public Awards(){
		
	}
		
	/**
	 * @return the awardId
	 */
	public Integer getAwardId() {
		return awardId;
	}

	/**
	 * @param awardId the awardId to set
	 */
	public void setAwardId(Integer awardId) {
		this.awardId = awardId;
	}

	/**
	 * @return the userId
	 */
	public User getUserId() {
		return userId;
	}

	/**
	 * @param userId the userId to set
	 */
	public void setUserId(User userId) {
		this.userId = userId;
	}

	/**
	 * @return the awardName
	 */
	public String getAwardName() {
		return awardName;
	}
	
	/**
	 * @param awardName the awardName to set
	 */
	public void setAwardName(String awardName) {
		this.awardName = awardName;
	}

	/**
	 * @return the awardDate
	 * @throws ParseException 
	 */
	public Date getAwardDate(){
		return awardDate;
	}

	/**
	 * @param awardDate the awardDate to set
	 */
	public void setAwardDate(Date awardDate) {
		this.awardDate = awardDate;
	}

	/**
	 * @return the awardDesc
	 */
	public String getAwardDesc() {
		return awardDesc;
	}

	/**
	 * @param awardDesc the awardDesc to set
	 */
	public void setAwardDesc(String awardDesc) {
		this.awardDesc = awardDesc;
	}

	@Override
	public Integer getId() {
		// TODO Auto-generated method stub
		return awardId;
	}

	@Override
	protected Object getBusinessKey() {
		// TODO Auto-generated method stub
		return awardId;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Awards [awardId=" + awardId + ", userId=" + userId
				+ ", awardName=" + awardName + ", awardDate=" + awardDate
				+ ", awardDesc=" + awardDesc + "]";
	}
	
	
}
