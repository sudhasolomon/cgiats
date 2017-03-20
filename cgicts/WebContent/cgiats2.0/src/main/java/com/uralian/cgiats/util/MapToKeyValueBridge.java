/*
 * MapToKeyValueBridge.java Feb 12, 2013
 *
 * Copyright 2012 Uralian, LLC. All rights reserved.
 */
package com.uralian.cgiats.util;

import java.util.Map;

import org.hibernate.search.bridge.StringBridge;

/**
 * @author Vlad Orzhekhovskiy
 */
public class MapToKeyValueBridge implements StringBridge
{
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.hibernate.search.bridge.StringBridge#objectToString(java.lang.Object)
	 */
	@Override
	public String objectToString(Object value)
	{
		Map<?, ?> map = (Map<?, ?>) value;
		if (Utils.isEmpty(map))
			return null;

		StringBuffer sb = new StringBuffer();
		for (Map.Entry<?, ?> entry : map.entrySet())
		{
			sb.append(entry.getKey()).append("=").append(entry.getValue());
			sb.append(",");
		}

		return sb.toString();
	}
}
