/*
 * ViewScope.java Jan 26, 2012
 * 
 * Copyright 2012 Uralian, LLC. All rights reserved.
 */
package com.uralian.cgiats.web;

import java.util.HashMap;
import java.util.Map;

import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.Scope;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * This is a custom scope for those beans that need to be view-bound.
 * 
 * @author Vlad Orzhekhovskiy
 */
public class ViewScope implements Scope, ApplicationContextAware
{
	public final String VIEW_SCOPE_KEY = "CRANK_VIEW_SCOPE";

	protected final Logger log = LoggerFactory.getLogger(getClass());

	private ApplicationContext applicationContext;

	public Object get(String name, ObjectFactory<?> objectFactory)
	{
		UIViewRoot uiRoot = FacesContext.getCurrentInstance().getViewRoot();
		if (uiRoot != null)
		{
			Map<String, Object> viewMap = extractViewScope(uiRoot);

			log.trace("Searching view map for [" + name + "]");
			Object object = viewMap.get(name);
			if (object == null)
			{
				log.trace("Restoring view from object factory");
				object = objectFactory.getObject();
				viewMap.put(name, object);
			}

			applicationContext.getAutowireCapableBeanFactory().autowireBean(object);
			log.trace("Bean " + object.getClass().getSimpleName() + " re-wired");
			return object;
		}
		else
			return null;
	}

	@SuppressWarnings("unchecked")
	private Map<String, Object> extractViewScope(UIViewRoot uiRoot)
	{
		Map<String, Object> attributes = uiRoot.getAttributes();

		Map<String, Object> viewScope = null;

		if (attributes.get(VIEW_SCOPE_KEY) == null)
		{
			viewScope = new HashMap<String, Object>();
			attributes.put(VIEW_SCOPE_KEY, viewScope);
		}
		else
		{
			viewScope = (Map<String, Object>) attributes.get(VIEW_SCOPE_KEY);
		}
		return viewScope;
	}

	public Object remove(String name)
	{
		UIViewRoot uiRoot = FacesContext.getCurrentInstance().getViewRoot();
		if (uiRoot != null)
		{
			log.trace("Removing [" + name + "] from view map");
			Map<String, Object> viewMap = extractViewScope(uiRoot);
			return viewMap.remove(name);
		}
		else
			return null;
	}

	public String getConversationId()
	{
		return null;
	}

	public void registerDestructionCallback(String name, Runnable callback)
	{
		// Not supported
	}

	public Object resolveContextualObject(String key)
	{
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.context.ApplicationContextAware#setApplicationContext
	 * (org.springframework.context.ApplicationContext)
	 */
	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
	    throws BeansException
	{
		this.applicationContext = applicationContext;
	}
}