/*
 * AbstractDownloader.java Apr 16, 2012
 *
 * Copyright 2012 Uralian, LLC. All rights reserved.
 */
package com.uralian.cgiats.proxy;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.Callable;

import org.apache.http.Header;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.uralian.cgiats.model.ContentType;
import com.uralian.cgiats.service.CandidateService;
import com.uralian.cgiats.service.ServiceException;

/**
 * @author Vlad Orzhekhovskiy
 */
public abstract class AbstractDownloader implements Callable<DocumentHolder>
{
	protected final Logger log = LoggerFactory.getLogger(getClass());

	private CandidateService service;

	/**
	 * @return the service.
	 */
	public CandidateService getService()
	{
		return service;
	}

	/**
	 * @param service the service to set.
	 */
	public void setService(CandidateService service)
	{
		this.service = service;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.concurrent.Callable#call()
	 */
	@Override
	public abstract DocumentHolder call() throws Exception;

	/**
	 * @param response
	 */
	protected ContentType getFileTypeFromResponse(
	    org.apache.http.HttpResponse response) throws ServiceException
	{
		String fileName = null;
		try
		{
			Header[] contentHeader = response
			    .getHeaders(CGIATSConstants.CONTENT_DISPOSITION_KEY);
			if (contentHeader != null && contentHeader.length > 0)
			{
				// getting file name from response to match the filename on server
				String pattern = "filename=";
				for (Header header : contentHeader)
				{
					int index = header.getValue().indexOf(pattern);
					if (index >= 0)
					{
						fileName = header.getValue().substring(index + pattern.length());
						break;
					}
				}
			}
			if (fileName == null)
			{
				if (log.isErrorEnabled())
				{
					log.error("File name could not be retrieve from response ");
				}
				throw new ServiceException(
				    "File name could not be retrieve from response ");
			}
			int index = fileName.lastIndexOf(".");
			fileName = fileName.substring(index + 1).toLowerCase();

			if (log.isInfoEnabled())
			{
				log.info("Successfully retrieved the file extension " + fileName);
			}
			if (fileName.equals("doc"))
			{
				return ContentType.MS_WORD;
			}
			else if (fileName.equals("docx"))
			{
				return ContentType.DOCX;
			}
			else if (fileName.equals("txt"))
			{
				return ContentType.PLAIN;
			}
			else if (fileName.equals("htm") || fileName.equals("html"))
			{
				return ContentType.HTML;
			}
			else if (fileName.equals("pdf"))
			{
				return ContentType.PDF;
			}
			else
			{
				if (log.isErrorEnabled())
				{
					log.error("Unsupported file type " + fileName);
				}
				throw new ServiceException("Unsupported file type " + fileName);
			}
		}
		catch (ServiceException se)
		{
			throw se;
		}
		catch (Exception e)
		{
			if (log.isErrorEnabled())
			{
				log.error(
				    "Unexpected error getting the file extension from the response", e);
			}
			throw new ServiceException(
			    "Unexpected error getting the file extension from the response", e);
		}
	}

	/**
	 * @param response
	 * @return
	 * @throws IOException
	 * @throws IllegalStateException
	 */
	protected byte[] extractDocument(org.apache.http.HttpResponse response)
	    throws IllegalStateException, IOException
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		InputStream is = response.getEntity().getContent();

		byte[] buf = new byte[4096];
		int read;
		while ((read = is.read(buf)) != -1)
			baos.write(buf, 0, read);

		baos.close();

		return baos.toByteArray();
	}
}