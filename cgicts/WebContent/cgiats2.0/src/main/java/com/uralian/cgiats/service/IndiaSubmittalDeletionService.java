package com.uralian.cgiats.service;

import java.util.List;

import com.uralian.cgiats.model.IndiaSubmittal;
import com.uralian.cgiats.model.IndiaSubmittalDeletion;

public interface IndiaSubmittalDeletionService {
	
	public void saveSubmittalDeletion(IndiaSubmittalDeletion submittalDeletion)throws ServiceException;

	
	public List<IndiaSubmittalDeletion> getDeletedStatusDetails(Integer submittalId);

	public IndiaSubmittal getDeletedSubmittal(int subid);

}
