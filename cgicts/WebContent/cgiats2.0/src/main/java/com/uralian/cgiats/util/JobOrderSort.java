package com.uralian.cgiats.util;

import java.util.Comparator;

import org.primefaces.model.SortOrder;

import com.uralian.cgiats.model.JobOrder;

/**
 * @author Sreenath
 *
 */
public class JobOrderSort implements Comparator<JobOrder> {
	private String sortField;

	private SortOrder sortOrder;

	public JobOrderSort(String sortField, SortOrder sortOrder) {
		this.sortField = sortField;
		this.sortOrder = sortOrder;
	}

	@Override
	public int compare(JobOrder jobOrder1, JobOrder JobOrder2) {
		try {
			Object value1 = JobOrder.class.getField(this.sortField).get(jobOrder1);
			Object value2 = JobOrder.class.getField(this.sortField).get(JobOrder2);
			
			

			int value = ((Comparable) value1).compareTo(value2);

			return SortOrder.ASCENDING.equals(sortOrder) ? value : -1 * value;
		} catch (Exception e) {
			throw new RuntimeException();
		}
	}

}
