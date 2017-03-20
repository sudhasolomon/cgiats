package com.uralian.cgiats.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.search.annotations.Field;


@Entity
@Table(name = "executive_resume")
public class ExecutiveResume extends AuditableEntity<Integer>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 540682002247868155L;
	
	@Id
	@GeneratedValue(generator = "customForeignGenerator")
	@GenericGenerator(name = "customForeignGenerator", strategy = "foreign", parameters = @Parameter(name = "property", value = "usExecutive"))
	@Column(name = "executive_id")
	private Integer id;

	@OneToOne(mappedBy = "executiveresume")
	@PrimaryKeyJoinColumn
	private UsExecutive usExecutive;

	@Lob
	@Column(name = "orig_document")
	private byte[] document;

	@Lob
	@Column(name = "parsed_text")
	@Field(name = "executiveresume")
	private String parsedText;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "orig_last_update")
	private Date originalLastUpdate;

	@Lob
	@Column(name = "proc_document")
	private byte[] processedDocument;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "proc_last_update")
	private Date processedLastUpdate;
	
	@Lob
	@Column(name = "rtr_document")
	private byte[] rtrDocument;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "rtr_last_update")
	private Date rtrLastUpdate;


	/*
	 * (non-Javadoc)
	 * 
	 * @see com.uralian.cgiats.model.AbstractEntity#getId()
	 */
	
	public Integer getId()
	{
		return id;
	}

	/**
	 * @return the candidate.
	 */
	

	public UsExecutive getUsExecutive() {
		return usExecutive;
	}

	public void setUsExecutive(UsExecutive usExecutive) {
		this.usExecutive = usExecutive;
	}

	/**
	 * @return the document.
	 */
	public byte[] getDocument()
	{
		return document;
	}

	/**
	 * @param document the document to set.
	 */
	public void setDocument(byte[] document)
	{
		this.document = document;
		this.originalLastUpdate = new Date();
	}

	/**
	 * @return the parsedText.
	 */
	public String getParsedText()
	{
		return parsedText;
	}

	/**
	 * @param parsedText the parsedText to set.
	 */
	public void setParsedText(String text)
	{
		this.parsedText = text;
	}

	/**
	 * @return the processedDocument.
	 */
	public byte[] getProcessedDocument()
	{
		return processedDocument;
	}

	/**
	 * @param processedDocument the processedDocument to set.
	 */
	public void setProcessedDocument(byte[] processedDocument)
	{
		this.processedDocument = processedDocument;
		this.processedLastUpdate = new Date();
	}

	/**
	 * @return the originalLastUpdate.
	 */
	public Date getOriginalLastUpdate()
	{
		return originalLastUpdate;
	}

	/**
	 * @param originalLastUpdate the originalLastUpdate to set.
	 */
	public void setOriginalLastUpdate(Date originalLastUpdate)
	{
		this.originalLastUpdate = originalLastUpdate;
	}

	/**
	 * @return the processedLastUpdate.
	 */
	public Date getProcessedLastUpdate()
	{
		return processedLastUpdate;
	}

	/**
	 * @param processedLastUpdate the processedLastUpdate to set.
	 */
	public void setProcessedLastUpdate(Date processedLastUpdate)
	{
		this.processedLastUpdate = processedLastUpdate;
	}

	/**
	 * @return the rtrDocument.
	 */
	
	public byte[] getRtrDocument() {
		return rtrDocument;
	}
	
	/**
	 * @param rtrDocument the rtrDocument to set.
	 */
	
	public void setRtrDocument(byte[] rtrDocument) {
		this.rtrDocument = rtrDocument;
		this.rtrLastUpdate = new Date();
	}

	
	/**
	 * @return the rtrLastUpdate.
	 */
	public Date getRtrLastUpdate() {
		return rtrLastUpdate;
	}
	/**
	 * @param rtrLastUpdate the rtrLastUpdate to set.
	 */
	public void setRtrLastUpdate(Date rtrLastUpdate) {
		this.rtrLastUpdate = rtrLastUpdate;
	}

	/**
	 * @return
	 */
	public boolean isProcessedOutOfDate()
	{
		return originalLastUpdate != null
		    && (processedLastUpdate == null || processedLastUpdate
		        .before(originalLastUpdate));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.uralian.cgiats.model.AbstractEntity#getBusinessKey()
	 */

	protected Object getBusinessKey()
	{
		return id;
	}

}
