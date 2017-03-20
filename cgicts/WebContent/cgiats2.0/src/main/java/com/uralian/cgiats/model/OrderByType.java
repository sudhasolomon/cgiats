package com.uralian.cgiats.model;

/**
 * @author Christian Rebollar
 */
public enum OrderByType
{
	ASC("ASC"), DESC("DESC");

	private final String orderByType;

	/**
	 * @param value
	 */
	private OrderByType(String value)
	{
		this.orderByType = value;
	}

	/**
	 * @return
	 */
	public String getValue()
	{
		return orderByType;
	}
}
