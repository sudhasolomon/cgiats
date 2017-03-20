/*
 * UserService.java May 16, 2012
 *
 * Copyright 2012 Uralian, LLC. All rights reserved.
 */
package com.uralian.cgiats.service;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.uralian.cgiats.dto.ResumesUpdateCountDto;
import com.uralian.cgiats.dto.UserDto;
import com.uralian.cgiats.model.MobileRegistration;
import com.uralian.cgiats.model.Pages;
import com.uralian.cgiats.model.PagesHeader;
import com.uralian.cgiats.model.User;
import com.uralian.cgiats.model.UserRoles;

/**
 * @author Vlad Orzhekhovskiy
 */
public interface IndiaUserService {
	/**
	 * @return
	 */
	public List<UserDto> listUsers();

	public List<UserDto> listEMs();
	
	public List<String> listAllADMsAndRec(List<String> dmName,Boolean status);
	
	public List<String> listAllDMSAndADMsAndRecByLocAndUserIds(List<String> dmName,List<String> locs,String status);
	
	
	public List<UserDto> listDMs();
	
	public List<UserDto> getAllDMAndADM_OfficeLocations(UserDto userDto,Boolean isActive, Boolean isIndia);

	public List<UserDto> listADMs(String emId);

	public UserDto getUserByCredentials(String username, String password,Boolean isActive);

	
	public Object getServedDatesOfUser(List<String> startDates,List<String> endDates,Boolean isFullYear,List<Integer> yearList,String userName);	
	/**
	 * @return
	 */
	public List<User> listBdms();

	/**
	 * @param userId
	 * @return
	 */
	public User loadUser(String userId);

	/**
	 * @param user
	 * @return
	 * @throws ServiceException
	 */
	public User saveUser(User user) throws ServiceException;

	/**
	 * @param user
	 * @throws ServiceException
	 */
	public void updateUser(User user) throws ServiceException;

	public void updateUser(UserDto userDto) throws ServiceException;

	/**
	 * @param userId
	 * @throws ServiceException
	 */
	public void deleteUser(String userId,Date relievDate) throws ServiceException;

	/**
	 * @param userId
	 * @throws ServiceException
	 */
	public List<User> getUser(String userId) throws ServiceException;

	/**
	 * @param userId
	 * @throws ServiceException
	 */
	public List<User> getRecruiters(String userId) throws ServiceException;

	public List<UserDto> getUsersByCreatedDate(String loginUser,Date monthStart, Date monthEnd);

	public void saveRegistration(MobileRegistration register);

	public List<MobileRegistration> listRegistrations();

	public void activateUser(String id, Date joinDate);

	public List<PagesHeader> loadMenuCfgData();

	public void saveCfg(UserRoles ur);

	public List<UserRoles> loadUserRole(User user);

	public UserRoles loadPage(String s, User user);

	public Pages loadPages(String s);

	// public void passwordRecovery(User user);

	public User loadUserByEmail(String email);

	public List<UserDto> listIndiaUsers();
	
	public List<UserDto> getUsersByIds(Collection<String> userIds);
	
	public List<UserDto> getUsersInfoByIds(Collection<String> userIds);

	public List<User> getAllUserIds();

	public List<ResumesUpdateCountDto> getResumesUpdateCount(Date fromDate, Date toDate);

	public List<UserDto> getAllIndiaDMsNoStatus();

	public List<UserDto> getAllDMsNoStatus();
	 
	public List<UserDto> getAllRecruiters(UserDto userDto, Boolean isIndia);

 
	public List<?> getClientsUnderDM(Date srtDate, Date endDate, String dm);

	public List<?> getAllClinets();
	
	public void permanentDeleteUser(String userId,Date relievDate) throws ServiceException;
 
}
