/*
 * ContentType.java Feb 29, 2012
 *
 * Copyright 2012 Uralian, LLC. All rights reserved.
 */
package com.uralian.cgiats.model;

/**
 * This type enumerates acceptable content types for resume documents.
 * 
 * @author Vlad Orzhekhovskiy
 */
public enum ContentType
{
	PLAIN("text/plain", ".txt"), MS_WORD("application/msword", ".doc"), HTML(
	    "text/html", ".html", ".htm"), DOCX(
	    "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
	    ".docx"), RTF("application/msword", ".rtf"),PDF("application/pdf", ".pdf");

	private final String mimeType;
	private final String[] extensions;

	/**
	 * @param extensions
	 */
	private ContentType(String mimeType, String... extensions)
	{
		this.mimeType = mimeType;
		this.extensions = extensions;
	}

	/**
	 * @return the mimeType.
	 */
	public String getMimeType()
	{
		return mimeType;
	}

	/**
	 * @return the extensions.
	 */
	public String[] getExtensions()
	{
		return extensions;
	}

	/**
	 * @return
	 */
	public String getPreferredExtension()
	{
		return extensions[0];
	}

	/**
	 * @param fileName
	 * @return
	 */
	public static ContentType resolveByFileName(String fileName)
	{
		String lcName = fileName.toLowerCase();
		for (ContentType ct : values())
		{
			for (String ext : ct.extensions)
			{
				if (lcName.endsWith(ext))
					return ct;
			}
		}

		return null;
	}

	/**
	 * @param mimeType
	 * @return
	 */
	public static ContentType resolveByMimeType(String mimeType)
	{
		for (ContentType ct : values())
		{
			if (ct.getMimeType().equals(mimeType))
				return ct;
		}

		return null;
	}
}