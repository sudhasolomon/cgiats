package com.uralian.cgiats.service;

import java.util.List;

import com.uralian.cgiats.model.Submittal;
import com.uralian.cgiats.model.SubmittalDeletion;

public interface SubmittalDeletionService {

	public void saveSubmittalDeletion(SubmittalDeletion submittalDeletion)
			throws ServiceException;

	/**
	 * @author Raghavender
	 * @param submittalId
	 * @return
	 */
	// Code added Raghavendra 19/05/14.
	public List<SubmittalDeletion> getDeletedStatusDetails(Integer submittalId);

	/**
	 * @author Raghavender
	 * @param subid
	 * @return
	 */
	// Code added Raghavendra 20/05/14.
	public Submittal getDeletedSubmittal(int subid);

}
