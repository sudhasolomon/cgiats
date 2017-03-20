package com.uralian.cgiats.dao.impl;

import org.springframework.stereotype.Repository;

import com.uralian.cgiats.dao.AgencyDetailsDao;
import com.uralian.cgiats.model.AgencyDetails;

/*
 * Radhika
 * Created Date:8/5/2013
 * Comment: Created to store candidate info for the tickets ATS-
 */
@Repository
@SuppressWarnings("unchecked")
public class AgencyDetailsDaoImpl extends GenericDaoImpl<AgencyDetails,Integer> implements AgencyDetailsDao{
	
	/**
	 */
	public AgencyDetailsDaoImpl(){
		super(AgencyDetails.class);
	}
}
