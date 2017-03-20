package com.uralian.cgiats.service;

import java.util.List;

import com.uralian.cgiats.model.CandidateSearchDto;
import com.uralian.cgiats.model.UsExecutive;

public interface UsExecutiveService {

	public void saveExecutive(UsExecutive usexecutive) throws ServiceException;
	
	public void updateExecutive(UsExecutive usexecutive) throws ServiceException;

	public List<UsExecutive> getExecutiveDetails(String email);

	public List<UsExecutive> getExistCandidateDetails(int id);

	public UsExecutive getExecutive(Integer executiveId, boolean fetchDocument,
			boolean fetchProperties);

	public List<UsExecutive> findExecutive(CandidateSearchDto criteria);

	public int findExecutivesCount(CandidateSearchDto criteria);
	
	public void deleteExecutive(int ExecutiveId) throws ServiceException;

}
