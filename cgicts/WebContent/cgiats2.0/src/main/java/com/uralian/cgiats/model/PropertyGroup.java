package com.uralian.cgiats.model;

import java.io.Serializable;
import java.util.*;

public class PropertyGroup implements Serializable
{
  private static final long serialVersionUID = 2037412838614585911L;
  
	private String propertyKey;
	private String propertyValue;
	
	public static List<PropertyGroup> getListFromMap(Map<String, String> properties)
	{
		List<PropertyGroup> list = new ArrayList<PropertyGroup>();
		if (properties != null && !properties.isEmpty())
		{
			for (String key : properties.keySet())
			{
				String value = properties.get(key);
				PropertyGroup group = new PropertyGroup();
				group.setPropertyKey(key);
				group.setPropertyValue(value);
				list.add(group);
			}
		}
		return list;
	}
	
	public static Map<String, String> getMapFromList(List<PropertyGroup> groupList)
	{
		Map<String, String> properties = new HashMap<String, String>();
		if (groupList != null && !groupList.isEmpty())
		{
			for (PropertyGroup group : groupList)
			{
				properties.put(group.getPropertyKey(), group.getPropertyValue());
			}
		}
		return properties;
	}
	
	/**
   * @return the propertyKey.
   */
  public String getPropertyKey()
  {
  	return propertyKey;
  }
	/**
   * @param propertyKey the propertyKey to set.
   */
  public void setPropertyKey(String propertyKey)
  {
  	this.propertyKey = propertyKey;
  }
	/**
   * @return the propertyValue.
   */
  public String getPropertyValue()
  {
  	return propertyValue;
  }
	/**
   * @param propertyValue the propertyValue to set.
   */
  public void setPropertyValue(String propertyValue)
  {
  	this.propertyValue = propertyValue;
  }	
}
