/*
 * StatsTimeRange.java Feb 4, 2013
 *
 * Copyright 2012 Uralian, LLC. All rights reserved.
 */
package com.uralian.cgiats.model;

import java.util.Calendar;
import java.util.Date;

/**
 * @author Vlad Orzhekhovskiy
 */
public enum StatsTimeRange
{
	Today("Today")
	{
		@Override
		public Date getFromDate()
		{
			Calendar cal = Calendar.getInstance();
			setToDayStart(cal);
			return cal.getTime();
		}

		@Override
		public Date getToDate()
		{
			Calendar cal = Calendar.getInstance();
			setToDayEnd(cal);
			return cal.getTime();
		}
	},

	Last30Days("30 Days")
	{
		@Override
		public Date getFromDate()
		{
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.DAY_OF_YEAR, -30);
			setToDayStart(cal);
			return cal.getTime();
		}

		@Override
		public Date getToDate()
		{
			return Today.getToDate();
		}
	},

	Last90Days("90 Days")
	{
		@Override
		public Date getFromDate()
		{
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.DAY_OF_YEAR, -90);
			setToDayStart(cal);
			return cal.getTime();
		}

		@Override
		public Date getToDate()
		{
			return Today.getToDate();
		}
	},

	AllTime("All Time")
	{
		@Override
		public Date getFromDate()
		{
			return null;
		}

		@Override
		public Date getToDate()
		{
			return null;
		}
	},

	Custom("Custom")
	{
		@Override
		public Date getFromDate()
		{
			throw new IllegalStateException("No fromDate for Custom range");
		}

		@Override
		public Date getToDate()
		{
			throw new IllegalStateException("No toDate for Custom range");
		}
	};

	private final String description;

	/**
	 * @param description
	 */
	private StatsTimeRange(String description)
	{
		this.description = description;
	}

	/**
	 * @return the description.
	 */
	public String getDescription()
	{
		return description;
	}

	/**
	 * @return
	 */
	public abstract Date getFromDate();

	/**
	 * @return
	 */
	public abstract Date getToDate();

	/**
	 * @param cal
	 */
	public static void setToDayStart(Calendar cal)
	{
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
	}

	/**
	 * @param cal
	 */
	public static void setToDayEnd(Calendar cal)
	{
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 59);
		cal.set(Calendar.MILLISECOND, 999);
	}
}