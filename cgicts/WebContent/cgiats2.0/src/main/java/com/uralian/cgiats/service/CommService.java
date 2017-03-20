/*
 * CommService.java Jun 27, 2012
 *
 * Copyright 2012 Uralian, LLC. All rights reserved.
 */
package com.uralian.cgiats.service;

import java.io.Serializable;

/**
 * @author Vlad Orzhekhovskiy
 */
public interface CommService
{
	/**
	 * @param to
	 * @param subject
	 * @param content
	 * @param attachments
	 * @throws ServiceException
	 */
	public void sendEmail(String from, String to, String subject, String content,
	    AttachmentInfo... attachments) throws ServiceException;

	/**
	 * @param to
	 * @param cc
	 * @param subject
	 * @param content
	 * @param attachments
	 * @throws ServiceException
	 */
	public void sendEmail(String from, String[] to, String[] cc, String subject,
	    String content, AttachmentInfo... attachments) throws ServiceException;

	/**
	 * @author Vlad Orzhekhovskiy
	 */
	public static class AttachmentInfo implements Serializable
	{
		private static final long serialVersionUID = 1L;

		private final String name;
		private final byte[] data;
		private final String type;

		/**
		 * @param name
		 * @param data
		 * @param type
		 */
		public AttachmentInfo(String name, byte[] data, String type)
		{
			this.name = name;
			this.data = data;
			this.type = type;
		}

		/**
		 * @return the name.
		 */
		public String getName()
		{
			return name;
		}

		/**
		 * @return the data.
		 */
		public byte[] getData()
		{
			return data;
		}

		/**
		 * @return the type.
		 */
		public String getType()
		{
			return type;
		}
	}
}
