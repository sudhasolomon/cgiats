package com.uralian.cgiats.service;

import com.uralian.cgiats.model.AgencyDetails;
import com.uralian.cgiats.model.CandidateInfo;

/*
 * Radhika
 * Created Date:8/5/2013
 * Comment: Created to store candidate info for the tickets ATS-
 */
public interface AgencyDetailsService {

	/**
	 * @param agencyDetails
	 * @throws ServiceException
	 */
	public void saveAgencyDetails(AgencyDetails agencyDetails) throws ServiceException;
	
	/**
	 * @param agencyDetails
	 * @throws ServiceException
	 */
	public void updateAgencyDetails(AgencyDetails agencyDetails) throws ServiceException;
	
	/**
	 * @param id
	 * @return
	 */
	public AgencyDetails getAgencyDetails(Integer id);
	
	
}
