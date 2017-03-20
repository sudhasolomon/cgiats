///*
// * AuditTrailInterceptor.java Jan 30, 2012
// * 
// * Copyright 2012 Uralian, LLC. All rights reserved.
// */
//package com.uralian.cgiats.util;
//
//import java.io.Serializable;
//import java.security.Principal;
//import java.util.Arrays;
//import java.util.Date;
//
//import javax.faces.context.FacesContext;
//
//import org.hibernate.EmptyInterceptor;
//import org.hibernate.type.Type;
//
//import com.uralian.cgiats.web.UserInfoBean;
//
///**
// * Intercepts life-cycle events to provide create/update date and user id.
// * 
// * @author Vlad Orzhekhovskiy
// */
//public class AuditTrailInterceptor extends EmptyInterceptor
//{
//	private static final long serialVersionUID = -3321190612494546996L;
//
//	private static final String TEST_USER = "test";
//
//	/*
//	 * (non-Javadoc)
//	 * 
//	 * @see org.hibernate.EmptyInterceptor#onFlushDirty(java.lang.Object,
//	 * java.io.Serializable, java.lang.Object[], java.lang.Object[],
//	 * java.lang.String[], org.hibernate.type.Type[])
//	 */
//	@Override
//	public boolean onFlushDirty(Object entity, Serializable id,
//			Object[] currentState, Object[] previousState, String[] propertyNames,
//			Type[] types)
//	{
//		setValue(currentState, propertyNames, "updatedBy", getUser());
//		setValue(currentState, propertyNames, "updatedOn", new Date());
//		return true;
//	}
//
//	/*
//	 * (non-Javadoc)
//	 * 
//	 * @see org.hibernate.EmptyInterceptor#onSave(java.lang.Object,
//	 * java.io.Serializable, java.lang.Object[], java.lang.String[],
//	 * org.hibernate.type.Type[])
//	 */
//	@Override
//	public boolean onSave(Object entity, Serializable id, Object[] state,
//			String[] propertyNames, Type[] types)
//	{
//		setValue(state, propertyNames, "createdBy", getUser());
//		setValue(state, propertyNames, "createdOn", new Date());
//		return true;
//	}
//
//	/**
//	 * @param currentState
//	 * @param propertyNames
//	 * @param propertyToSet
//	 * @param value
//	 */
//	private void setValue(Object[] currentState, String[] propertyNames,
//			String propertyToSet, Object value)
//	{
//		int index = Arrays.asList(propertyNames).indexOf(propertyToSet);
//		if (index >= 0)
//			currentState[index] = value;
//	}
//
//	/**
//	 * @return
//	 */
//	protected String getUser()
//	{
//		try
//		{
//			Principal user = FacesContext.getCurrentInstance().getExternalContext()
//					.getUserPrincipal();
//			return user != null ? user.getName() : UserInfoBean.getUserIdOnline();
//		}
//		catch (Exception e)
//		{
//			return TEST_USER;
//		}
//	}
//}