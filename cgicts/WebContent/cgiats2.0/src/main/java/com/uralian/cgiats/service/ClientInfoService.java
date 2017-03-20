package com.uralian.cgiats.service;

import java.util.Date;
import java.util.List;

import com.uralian.cgiats.dto.ClientInfoDto;
import com.uralian.cgiats.model.ClientInfo;

/*
 * Radhika
 * Created Date:8/1/2013
 * Comment: Created to store candidate info for the tickets ATS-
 */
public interface ClientInfoService {
	
	/**
	 * @return
	 */
	public List<ClientInfoDto> getListClientInfo();
	
	/**
	 * @param clientInfo
	 * @throws ServiceException
	 */
	public void saveClientInfo(ClientInfo clientInfo) throws ServiceException;
	
	/**
	 * @param clientInfo
	 * @throws ServiceException
	 */
	public void updateClientInfo(ClientInfo clientInfo) throws ServiceException;
	
	/**
	 * @param id
	 * @return
	 */
	public ClientInfo getClientInfo(Integer id);
	
	/**
	 * @param dateStart
	 * @param dateEnd
	 * @return
	 */
	public List<ClientInfo> getCandidateContractPeriod(Date dateStart, Date dateEnd);

	public void saveUpdateClient(ClientInfoDto projectDto);
}
