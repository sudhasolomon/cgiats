/*
 * UIBean.java Mar 15, 2012
 *
 * Copyright 2012 Uralian, LLC. All rights reserved.
 */
package com.uralian.cgiats.web;

import static javax.faces.application.FacesMessage.SEVERITY_FATAL;
import static javax.faces.application.FacesMessage.SEVERITY_INFO;
import static javax.faces.application.FacesMessage.SEVERITY_WARN;

import java.util.Iterator;

import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Vlad Orzhekhovskiy
 */
public class UIBean
{
	protected final Logger log = LoggerFactory.getLogger(getClass());

	/**
	 * @param summary
	 * @param detail
	 */
	public static void addInfoMessage(String summary, String detail)
	{
		getFacesContext().addMessage(null,
		    new FacesMessage(SEVERITY_INFO, summary, detail));
	}

	/**
	 * @param id
	 * @param severity
	 * @param e
	 */
	public static void addErrorMessage(String id, Severity severity, Exception e)
	{
		String summary = SEVERITY_FATAL.equals(severity) ? "Fatal Error"
		    : SEVERITY_WARN.equals(severity) ? "Warning" : "Error";
		addMessage(id, severity, summary, e.getMessage());
	}

	/**
	 * @param id
	 * @param severity
	 * @param summary
	 * @param detail
	 */
	public static void addMessage(String id, Severity severity, String summary,
	    String detail)
	{
		getFacesContext().addMessage(getClientId(id),
		    new FacesMessage(severity, summary, detail));
	}

	/**
	 * @return
	 */
	public static FacesContext getFacesContext()
	{
		return FacesContext.getCurrentInstance();
	}

	/**
	 * @param id
	 * @return
	 */
	public static String getClientId(String id)
	{
		if (id == null)
			return null;

		UIComponent c = findComponent(id);
		return c != null ? c.getClientId() : null;
	}

	/**
	 * @param id
	 * @return
	 */
	public static UIComponent findComponent(String id)
	{
		UIComponent root = FacesContext.getCurrentInstance().getViewRoot();
		return findComponent(root, id);
	}

	/**
	 * @param c
	 * @param id
	 * @return
	 */
	public static UIComponent findComponent(UIComponent c, String id)
	{
		if (id.equals(c.getId()))
			return c;

		Iterator<UIComponent> kids = c.getFacetsAndChildren();
		while (kids.hasNext())
		{
			UIComponent found = findComponent(kids.next(), id);
			if (found != null)
				return found;
		}
		return null;
	}
	
	/**
	 * @return
	 */
	public static String getBaseUrl()
	{
		HttpServletRequest request = (HttpServletRequest) getFacesContext()
		    .getExternalContext().getRequest();
		String baseUrl = request.getScheme() + "://" + request.getServerName()
		    + ":" + request.getServerPort() + request.getContextPath();
		return baseUrl;
	}	
}