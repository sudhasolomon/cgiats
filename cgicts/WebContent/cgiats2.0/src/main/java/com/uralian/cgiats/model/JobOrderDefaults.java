package com.uralian.cgiats.model;

/**
 * @author Christian Rebollar
 */
public class JobOrderDefaults
{
	public final static String BILL_RATE = "BILL_RATE";
	public final static String INDUSTRY = "INDUSTRY";
	public final static String EXPERIENCE_TIME = "EXPERIENCE_TIME";
	public final static String EXPERIENCE_LEVEL = "EXPERIENCE_LEVEL";
	public final static String WORK_STATUS = "WORK_STATUS";

	/**
	 * @param jobOrder
	 */
	public void setDefaultValues(JobOrder jobOrder)
	{
		jobOrder.addField(WORK_STATUS, null, true);
		jobOrder.addField(BILL_RATE, null, true);
//		jobOrder.addField(EXPERIENCE_LEVEL, null, true);
//		jobOrder.addField(EXPERIENCE_TIME, null, true);
//		jobOrder.addField(INDUSTRY, null, true);
	}
	
	
	
	public void setIndiaJobsDefaultValues(IndiaJobOrder jobOrder)
	{
		jobOrder.addField(WORK_STATUS, null, true);
		jobOrder.addField(BILL_RATE, null, true);
	}
}
