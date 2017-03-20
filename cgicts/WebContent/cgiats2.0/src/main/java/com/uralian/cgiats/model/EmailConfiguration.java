/**
 * 
 */
package com.uralian.cgiats.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * @author Sreenath
 * 
 */
@Entity
@Table(name = "email_configuration")
@SequenceGenerator(name = "emailSeq", sequenceName = "email_configuration_seq")
public class EmailConfiguration extends AuditableEntity<Serializable> implements
		Serializable {

	/**
	 * longEmailConfiguration.java
	 * 
	 */
	private static final long serialVersionUID = 530587296109076714L;

	@Id
	@GeneratedValue(generator = "emailSeq", strategy = GenerationType.AUTO)
	@Column(name = "email_config_Id")
	private int emailConfigId;
	@Column(name = "emails")
	private String emails;
	@Column(name = "report_name")
	private String reportName;

	/**
	 * @return the emailConfigId
	 */
	public int getEmailConfigId() {
		return emailConfigId;
	}

	/**
	 * @param emailConfigId
	 *            the emailConfigId to set
	 */
	public void setEmailConfigId(int emailConfigId) {
		this.emailConfigId = emailConfigId;
	}

	/**
	 * @return the emails
	 */
	public String getEmails() {
		return emails;
	}

	/**
	 * @param emails
	 *            the emails to set
	 */
	public void setEmails(String emails) {
		this.emails = emails;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.uralian.cgiats.model.AbstractEntity#getId()
	 */
	@Override
	public Serializable getId() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.uralian.cgiats.model.AbstractEntity#getBusinessKey()
	 */
	@Override
	protected Object getBusinessKey() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @return the reportName
	 */
	public String getReportName() {
		return reportName;
	}

	/**
	 * @param reportName
	 *            the reportName to set
	 */
	public void setReportName(String reportName) {
		this.reportName = reportName;
	}

}
