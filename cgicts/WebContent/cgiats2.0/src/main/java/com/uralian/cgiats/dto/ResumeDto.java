/**
 * 
 */
package com.uralian.cgiats.dto;

import java.io.Serializable;

import com.uralian.cgiats.model.ContentType;

/**
 * @author Sreenath
 *
 */
public class ResumeDto implements Serializable {

	private String resumeContent;
	private byte[] originalDoc;
	private ContentType originalDocType;

	private byte[] rtrDocumentDoc;
	private ContentType rtrDocumentType;

	private byte[] processedDocument;
	private ContentType processedDocumentType;

	public byte[] getRtrDocumentDoc() {
		return rtrDocumentDoc;
	}

	public void setRtrDocumentDoc(byte[] rtrDocumentDoc) {
		this.rtrDocumentDoc = rtrDocumentDoc;
	}

	public ContentType getRtrDocumentType() {
		return rtrDocumentType;
	}

	public void setRtrDocumentType(ContentType rtrDocumentType) {
		this.rtrDocumentType = rtrDocumentType;
	}

	public byte[] getProcessedDocument() {
		return processedDocument;
	}

	public void setProcessedDocument(byte[] processedDocument) {
		this.processedDocument = processedDocument;
	}

	public ContentType getProcessedDocumentType() {
		return processedDocumentType;
	}

	public void setProcessedDocumentType(ContentType processedDocumentType) {
		this.processedDocumentType = processedDocumentType;
	}

	public String getResumeContent() {
		return resumeContent;
	}

	public void setResumeContent(String resumeContent) {
		this.resumeContent = resumeContent;
	}

	/**
	 * @return the originalDoc
	 */
	public byte[] getOriginalDoc() {
		return originalDoc;
	}

	/**
	 * @param originalDoc
	 *            the originalDoc to set
	 */
	public void setOriginalDoc(byte[] originalDoc) {
		this.originalDoc = originalDoc;
	}

	/**
	 * @return the originalDocType
	 */
	public ContentType getOriginalDocType() {
		return originalDocType;
	}

	/**
	 * @param originalDocType
	 *            the originalDocType to set
	 */
	public void setOriginalDocType(ContentType originalDocType) {
		this.originalDocType = originalDocType;
	}

}
