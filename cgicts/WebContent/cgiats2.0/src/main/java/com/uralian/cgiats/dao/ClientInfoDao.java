package com.uralian.cgiats.dao;

import java.util.Date;
import java.util.List;

import com.uralian.cgiats.model.ClientInfo;

/*
 * Radhika
 * Created Date:8/5/2013
 * Comment: Created to store candidate info for the tickets ATS-
 */

public interface ClientInfoDao extends GenericDao<ClientInfo,Integer>{

	public List<ClientInfo> getCandidateContractPeriod(Date dateStart, Date dateEnd);
	
}
