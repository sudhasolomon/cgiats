/*
 * ServiceException.java Jan 29, 2012
 * 
 * Copyright 2012 Uralian, LLC. All rights reserved.
 */
package com.uralian.cgiats.service;

/**
 * General service exception, used by service layer methods.
 * 
 * @author Vlad Orzhekhovskiy
 */
public class ServiceException extends Exception
{
	private static final long serialVersionUID = 2073840911168730839L;

	/**
   * 
   */
	public ServiceException()
	{
		super();
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public ServiceException(String arg0, Throwable arg1)
	{
		super(arg0, arg1);
	}

	/**
	 * @param arg0
	 */
	public ServiceException(String arg0)
	{
		super(arg0);
	}

	/**
	 * @param arg0
	 */
	public ServiceException(Throwable arg0)
	{
		super(arg0);
	}
}