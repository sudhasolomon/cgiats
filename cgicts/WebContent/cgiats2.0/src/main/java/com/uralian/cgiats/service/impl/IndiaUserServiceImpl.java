/*
 * UserServiceImpl.java May 16, 2012
 *
 * Copyright 2012 Uralian, LLC. All rights reserved.
 */
package com.uralian.cgiats.service.impl;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uralian.cgiats.dao.IndiaUserDao;
import com.uralian.cgiats.dto.ResumesUpdateCountDto;
import com.uralian.cgiats.dto.UserDto;
import com.uralian.cgiats.model.MobileRegistration;
import com.uralian.cgiats.model.Pages;
import com.uralian.cgiats.model.PagesHeader;
import com.uralian.cgiats.model.User;
import com.uralian.cgiats.model.UserRole;
import com.uralian.cgiats.model.UserRoles;
import com.uralian.cgiats.service.IndiaUserService;
import com.uralian.cgiats.service.ServiceException;
import com.uralian.cgiats.util.TransformDtoToEntity;
import com.uralian.cgiats.util.TransformEntityToDto;
import com.uralian.cgiats.util.Utils;

/**
 * @author Vlad Orzhekhovskiy
 */
@Service
@Transactional(rollbackFor = ServiceException.class)
public class IndiaUserServiceImpl implements IndiaUserService {
	protected final Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	private IndiaUserDao userDao;

	@Override
	@Transactional(readOnly = true)
	public User loadUser(String userId) {
		User user = userDao.findById(userId);
		return user;
	}

	@Override
	public List<UserDto> listIndiaUsers() {
		Map<String, Object> params = new HashMap<String, Object>();
		StringBuffer hql = new StringBuffer();
		hql.append("from User u");
		hql.append(" where status in('ACTIVE') and userRole = :role order by u.userId");
		params.put("role", UserRole.IN_Recruiter);
		List<User> list = userDao.findByQuery(hql.toString(), params);
		List<UserDto> userdtolist = new ArrayList<UserDto>();
		if (list != null && list.size() > 0) {
			for (User user : list) {
				if (user.getUserId() != null && user.getUserId().trim().length() > 0)
					userdtolist.add(TransformEntityToDto.getUserDto(user));
			}
		}
		return userdtolist;
	}

	@Override
	public List<UserDto> listUsers() {
		List<UserDto> userdtolist = null;
		try {
			StringBuffer hql = new StringBuffer();
			hql.append("from User u");
			hql.append(" where status in('ACTIVE') order by u.userId");
			List<User> list = userDao.findByQuery(hql.toString(), null);
			userdtolist = new ArrayList<UserDto>();
			if (list != null && list.size() > 0) {
				for (User user : list) {
					if (user.getUserId() != null && user.getUserId().trim().length() > 0)
						userdtolist.add(TransformEntityToDto.getUserDto(user));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return userdtolist;
	}

	@Override
	@Transactional(readOnly = true)
	public List<User> listBdms() {
		try {
			StringBuffer hql = new StringBuffer();
			Map<String, Object> params = new HashMap<String, Object>();
			hql.append("from User u");
			hql.append(" where userRole !=:role and status = :status order by u.userId");
			params.put("role", UserRole.IN_Recruiter);
			params.put("status", "ACTIVE");
			return userDao.findByQuery(hql.toString(), params);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	public List<UserDto> listEMs() {
		List<UserDto> userdtolist = null;
		try {
			StringBuffer hql = new StringBuffer();
			Map<String, Object> params = new HashMap<String, Object>();
			hql.append("from User u");
			hql.append(" where userRole =:role and status = :status order by u.userId");
			params.put("role", UserRole.EM);
			params.put("status", "ACTIVE");
			List<User> list = userDao.findByQuery(hql.toString(), params);
			userdtolist = new ArrayList<UserDto>();
			if (list != null && list.size() > 0) {
				for (User user : list) {
					userdtolist.add(TransformEntityToDto.getUserDto(user));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return userdtolist;
	}

	public List<User> getRecruiters(String userId) {
		try {
			StringBuffer hql = new StringBuffer();
			Map<String, Object> params = new HashMap<String, Object>();
			hql.append("from User u");
			hql.append(" where userRole =:role and status = :status and assignedBdm=:bdm order by u.userId");
			params.put("role", UserRole.IN_Recruiter);
			params.put("status", "ACTIVE");
			params.put("bdm", userId);
			return userDao.findByQuery(hql.toString(), params);
		} catch (Exception e) {
			e.getMessage();
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	public User saveUser(User user) throws ServiceException {
		try {
			user.setStatus("ACTIVE");
			return userDao.save(user);
		} catch (RuntimeException e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
			throw new ServiceException(e);
		}
	}

	@Override
	public void updateUser(User user) throws ServiceException {
		try {
			userDao.update(user);
		} catch (RuntimeException e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
			throw new ServiceException(e);
		}
	}

	@Override
	public void deleteUser(String userId, Date relievDate) throws ServiceException {
		try {
			User user = userDao.findById(userId);
			user.setPassword(null);
			user.setStatus("INACTIVE");
			user.setRelievingDate(relievDate);
			userDao.update(user);
			// userDao.delete(user);
		} catch (RuntimeException e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
			throw new ServiceException(e);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<User> getUser(String userId) {
		List<User> result = null;
		try {
			StringBuffer hql = new StringBuffer();
			hql.append("select userId from User u");
			hql.append(" where LOWER(u.id) = LOWER(:userId) order by u.userId");

			result = userDao.findByQuery(hql.toString(), "userId", userId.toLowerCase());
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return result;
	}

	@Override
	public List<UserDto> getUsersByCreatedDate(String loginUser, Date monthStart, Date monthEnd) {
		List<User> users = null;
		List<UserDto> userdtolist = null;
		try {
			StringBuffer hql = new StringBuffer();
			Map<String, Object> params = new HashMap<String, Object>();

			if (loginUser.equalsIgnoreCase("Hari")) {
				hql.append(" from User u  where status in ('ACTIVE','INACTIVE') ");
			} else {
				hql.append(" from User u  where status='ACTIVE' ");
			}

			if (monthStart != null && monthEnd != null) {
				// hql.append(" date(u.createdOn)=date(:date) order by userId");
				// params.put("date", new Date());

				hql.append(" and createdOn>=:createdOn1 and createdOn<=:createdOn2");

				params.put("createdOn1", monthStart);
				params.put("createdOn2", monthEnd);
			}
			hql.append(" order by userId");
			users = userDao.findByQuery(hql.toString(), params);
			userdtolist = new ArrayList<UserDto>();
			if (users != null && users.size() > 0) {
				for (User user : users) {
					if (user.getUserId() != null && user.getUserId().trim().length() > 0)
						userdtolist.add(TransformEntityToDto.getUserDto(user));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return userdtolist;
	}

	@Override
	public void saveRegistration(MobileRegistration register) {
		try {
			userDao.saveRegistration(register);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
	}

	@Override
	public List<MobileRegistration> listRegistrations() {
		try {
			return userDao.listRegistrations();
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	public void activateUser(String userId, Date joinDate) {
		try {
			User user = userDao.findById(userId);
			user.setPassword(null);
			user.setStatus("ACTIVE");
			user.setJoinDate(joinDate);
			user.setRelievingDate(null);
			userDao.update(user);
		} catch (RuntimeException e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
			e.printStackTrace();
		}
	}

	@Override
	public List<PagesHeader> loadMenuCfgData() {
		try {
			return userDao.loadMenuCfgData();
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	public void saveCfg(UserRoles ur) {
		try {
			userDao.saveCfg(ur);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
	}

	@Override
	public List<UserRoles> loadUserRole(User user) {
		try {
			return userDao.loadUserRole(user);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	public UserRoles loadPage(String s, User user) {
		try {
			return userDao.loadPage(s, user);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	public Pages loadPages(String s) {
		try {
			return userDao.loadPages(s);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	public UserDto getUserByCredentials(String username, String password, Boolean isActive) {
		UserDto userDto = null;
		try {
			User user = userDao.getUserByCredentials(username, password, isActive);
			if (user != null) {
				userDto = TransformEntityToDto.getUserDto(user);
			}
		} catch (Exception e) {
			e.printStackTrace();
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return userDto;
	}

	@Override
	public User loadUserByEmail(String email) {
		try {
			return userDao.findByEmail(email);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	public List<UserDto> listADMs(String emId) {
		try {
			StringBuffer hql = new StringBuffer();
			Map<String, Object> params = new HashMap<String, Object>();
			hql.append("from User u");
			hql.append(" where userRole =:role and assignedBdm =:emId and status = :status order by u.userId");
			params.put("role", UserRole.ADM);
			params.put("emId", emId);
			params.put("status", "ACTIVE");
			List<User> list = userDao.findByQuery(hql.toString(), params);
			List<UserDto> userdtolist = new ArrayList<UserDto>();
			if (list != null && list.size() > 0) {
				for (User user : list) {
					userdtolist.add(TransformEntityToDto.getUserDto(user));
				}
			}
			return userdtolist;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}

		return null;
	}

	@Override
	public void updateUser(UserDto userDto) throws ServiceException {
		try {
			if (userDto != null && userDto.getUserId() != null) {
				User user = userDao.findById(userDto.getUserId());
				if (user != null) {
					TransformDtoToEntity.getUser(userDto, user, userDto.getBase64Image());
					userDao.update(user);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.uralian.cgiats.service.UserService#listDMs()
	 */
	@Override
	public List<UserDto> listDMs() {
		List<UserDto> userdtolist = null;
		try {
			StringBuffer hql = new StringBuffer();
			List<String> paramNames = new ArrayList<String>();
			List<Object> paramValues = new ArrayList<Object>();
			hql.append("select u.userId,u.firstName,u.lastName from User u");
			hql.append(" where userRole =:role and status = :status order by u.userId");
			paramNames.add("role");
			paramValues.add(UserRole.IN_DM);
			paramNames.add("status");
			paramValues.add("ACTIVE");
			List<?> list = userDao.runQuery(hql.toString(), paramNames.toArray(new String[0]), paramValues.toArray());
			userdtolist = new ArrayList<UserDto>();
			if (list != null && list.size() > 0) {
				Iterator<?> iterator = list.iterator();
				while (iterator.hasNext()) {
					Object[] obj = (Object[]) iterator.next();
					UserDto dto = new UserDto();
					dto.setUserId((String) obj[0]);
					dto.setFullName(Utils.concatenateTwoStringsWithSpace((String) obj[1], (String) obj[2]));
					userdtolist.add(dto);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return userdtolist;
	}

	@Override
	public List<UserDto> getAllDMAndADM_OfficeLocations(UserDto userDto,Boolean isActive, Boolean isIndia) {
		List<UserDto> userdtolist = null;
		try {
			StringBuffer hql = new StringBuffer();
			Map<String, Object> params = new HashMap<String, Object>();
			// select DISTINCT u.office_location from user_acct u where
			// u.status='ACTIVE' and u.user_role in ('DM','ADM')
			hql.append("select u.officeLocation,u.userId,u.firstName,u.lastName,u.userRole from User u where ");
			if (isActive != null && isActive) {
				hql.append("u.status='ACTIVE' and");
			} else if (isActive != null && !isActive) {
				hql.append("u.status='INACTIVE' and");
			}
			if(isIndia != null){
				if(isIndia){
					hql.append(" u.userRole in ('IN_DM','IN_Recruiter')");
				}else{
					hql.append(" u.userRole in ('DM','ADM','Recruiter')");
				}
			}else{
				hql.append(" u.userRole in ('DM','ADM','Recruiter')");
			}
			
			
			if (userDto != null && (userDto.getUserRole().equals(UserRole.IN_DM))) {
				hql.append(
						" and (u.userId=?1 OR ((u.assignedBdm = ?1) or" + " (u.assignedBdm in   (select us.userId from User us where us.assignedBdm = ?1))))");
				params.put("1", userDto.getUserId());
			}

			List<?> list = userDao.findByQuery(hql.toString(), -1, -1, params);
			userdtolist = new ArrayList<UserDto>();
			if (list != null && list.size() > 0) {
				for (Object object : list) {
					Object[] obj = (Object[]) object;
					UserDto dto = new UserDto();
					dto.setOfficeLocation((String) obj[0]);
					dto.setUserId((String) obj[1]);
					dto.setFullName(Utils.concatenateTwoStringsWithSpace((String) obj[2], (String) obj[3]));
					dto.setUserRole((UserRole) obj[4]);
					userdtolist.add(dto);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return userdtolist;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.uralian.cgiats.service.UserService#getUsersByIds(java.util.List)
	 */
	@Override
	public List<UserDto> getUsersByIds(Collection<String> userIds) {
		List<UserDto> userdtolist = null;
		try {
			StringBuffer hql = new StringBuffer();
			List<String> paramNames = new ArrayList<String>();
			List<Object> paramValues = new ArrayList<Object>();
			hql.append(
					"select u.userId,u.assignedBdm,u.officeLocation,u.firstName,u.userRole,u.firstName,u.lastName,u.phone,u.city,u.email,u.lastName from User u");
			hql.append(" where (u.userId in ?1 "
					+ "or u.userId in (select us.assignedBdm from User us where us.userId in ?1 and us.assignedBdm IS NOT NULL and us.status = 'ACTIVE')) and u.status='ACTIVE'");
			paramNames.add("1");
			paramValues.add(userIds);
			List<?> list = userDao.runQuery(hql.toString(), paramNames.toArray(new String[0]), paramValues.toArray());
			userdtolist = new ArrayList<UserDto>();
			if (list != null && list.size() > 0) {
				Iterator<?> iterator = list.iterator();
				while (iterator.hasNext()) {
					Object[] obj = (Object[]) iterator.next();
					UserDto dto = new UserDto();
					dto.setUserId((String) obj[0]);
					dto.setAssignedBdm((String) obj[1]);
					dto.setOfficeLocation((String) obj[2]);
					dto.setFullName(Utils.concatenateTwoStringsWithSpace((String) obj[3], (String) obj[10]));
					dto.setUserRole((UserRole) obj[4]);
					dto.setFirstName((String) obj[5]);
					dto.setLastName((String) obj[6]);
					dto.setPhone((String) obj[7]);
					dto.setCity((String) obj[8]);
					dto.setEmail((String) obj[9]);
					userdtolist.add(dto);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return userdtolist;
	}

	@Override
	public List<UserDto> getUsersInfoByIds(Collection<String> userIds) {
		List<UserDto> userdtolist = null;
		try {
			StringBuffer hql = new StringBuffer();
			List<String> paramNames = new ArrayList<String>();
			List<Object> paramValues = new ArrayList<Object>();
			hql.append(
					"select u.userId,u.assignedBdm,u.officeLocation,u.userRole,u.firstName,u.lastName,u.phone,u.city,u.email,u.employeeId,u.joinDate,u.level,u.status "
							+ "from User u");
			hql.append(" where u.userId in ?1");
			paramNames.add("1");
			paramValues.add(userIds);
			List<?> list = userDao.runQuery(hql.toString(), paramNames.toArray(new String[0]), paramValues.toArray());
			if (list != null && list.size() > 0) {
				userdtolist = new ArrayList<UserDto>();
				Iterator<?> iterator = list.iterator();
				while (iterator.hasNext()) {
					Object[] obj = (Object[]) iterator.next();
					UserDto dto = new UserDto();
					dto.setUserId((String) obj[0]);
					dto.setAssignedBdm((String) obj[1]);
					dto.setOfficeLocation((String) obj[2]);
					dto.setFullName(Utils.concatenateTwoStringsWithSpace((String) obj[4], (String) obj[5]));
					dto.setUserRole((UserRole) obj[3]);
					dto.setFirstName((String) obj[4]);
					dto.setLastName((String) obj[5]);
					dto.setPhone((String) obj[6]);
					dto.setCity((String) obj[7]);
					dto.setEmail((String) obj[8]);
					dto.setEmployeeId((String) obj[9]);
					dto.setJoiningDate(Utils.convertDateToString((Date) obj[10]));
					dto.setLevel(obj[11] != null ? ((Integer) obj[11]).toString() : "");
					dto.setStatus(Utils.getStringValueOfObj(obj[12]));
					userdtolist.add(dto);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return userdtolist;
	}

	@Override
	public List<User> getAllUserIds() {
		Map<String, Object> params = new HashMap<String, Object>();
		StringBuffer hql = new StringBuffer();
		hql.append("select u.userId from User u order by u.userId");
		// params.put("" , null);
		List<User> list = userDao.findByQuery(hql.toString(), params);
		return list;
	}

	@Override
	public List<ResumesUpdateCountDto> getResumesUpdateCount(Date fromDate, Date toDate) {
		Map<String, Object> params = new HashMap<String, Object>();
		List<ResumesUpdateCountDto> resumeupdatecountlist = null;
		StringBuffer hql = new StringBuffer();
		/*
		 * hql.append(
		 * "select count(*), rah.createdBy from ResumeHistory rah where rah.status='UPDATED'"
		 * +
		 * " and rah.createdOn>= :fromDate and rah.createdOn< :toDate GROUP BY rah.createdBy"
		 * );
		 */

/*		hql.append(
				"select count(*),(coalesce(u.firstName, '') ||' ' || coalesce(u.lastName, '')) as name from ResumeHistory ra, User u where ra.createdBy=u.userId and ra.id in "
						+ "(select MIN(r.id) from ResumeHistory r where r.status='UPDATED'"
						+ "and r.createdOn>= :fromDate and r.createdOn<= :toDate GROUP BY r.createdBy,r.candidate) group by ra.createdBy,u.userId");*/
		
		
		hql.append("select obj1.name, round(round((obj1.resume_updated_count * 100.0) / obj2.serviced_days, 2)/100,2) AS per_day_count, "
				+ " obj1.resume_updated_count AS resume_updated_count from (select count(*) AS resume_updated_count,coalesce(u.first_name, '') ||' ' || coalesce(u.last_name, '') as"
				+ " name,ra.created_by AS user_id from resume_audit_history ra, user_acct u where ra.created_by=u.user_id and ra.history_id in "
				+ " (select MIN(r.history_id) from resume_audit_history r where r.status='UPDATED' and r.created_on>= :fromDate and r.created_on< :toDate "
				+ " GROUP BY r.created_by,r.candidate) group by ra.created_by,u.user_id) AS obj1, (SELECT result.created_by AS created_by,count(result.created_On) AS serviced_days from"
				+ " (select ra2.created_by AS created_by,count(date(ra2.created_on)) AS created_On from resume_audit_history ra2, user_acct u2 where ra2.created_by=u2.user_id and ra2.history_id in"
				+ " (select MIN(r1.history_id) from resume_audit_history r1 where r1.status='UPDATED' and r1.created_on>= :fromDate and r1.created_on< :toDate "
				+ " GROUP BY r1.created_by,r1.candidate) group by ra2.created_by,date(ra2.created_on)) AS result group by result.created_by)AS obj2 where obj1.user_id = obj2.created_by");

		params.put("fromDate", fromDate);
		params.put("toDate", toDate);
		List list = userDao.findBySQLQuery(hql.toString(), params);
		resumeupdatecountlist = new ArrayList<ResumesUpdateCountDto>();
		if (list != null && list.size() > 0) {
			Iterator<?> iterator = list.iterator();
			while (iterator.hasNext()) {
				Object[] obj = (Object[]) iterator.next();
				ResumesUpdateCountDto dto = new ResumesUpdateCountDto();
				dto.setResumes_count(((BigInteger) (obj[2])).doubleValue());
				dto.setName((String) obj[0]);
				dto.setAvg_count(((BigDecimal) (obj[1])).doubleValue());
				resumeupdatecountlist.add(dto);
			}
		}
		return resumeupdatecountlist;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.uralian.cgiats.service.UserService#listRecsByDM(java.lang.String)
	 */
	@Override
	public List<String> listAllADMsAndRec(List<String> dmNames, Boolean status) {
		List<String> recList = null;
		try {
			StringBuffer hql = new StringBuffer();
			Map<String, Object> params = new HashMap<String, Object>();
			hql.append("select u.userId from User u where ((u.assignedBdm in (:dm)) or" + " (u.assignedBdm in  ");
			if (status != null && status) {
				hql.append(" (select us.userId from User us where us.assignedBdm in (:dm) and us.status='ACTIVE'))) and u.status='ACTIVE'");
			} else if (status != null && !status) {
				hql.append(" (select us.userId from User us where us.assignedBdm in (:dm) and us.status='INACTIVE'))) and u.status='INACTIVE'");
			} else {
				hql.append(" (select us.userId from User us where us.assignedBdm in (:dm))))");
			}
			params.put("dm", dmNames);
			recList = (List<String>) userDao.findByQuery(hql.toString(), -1, -1, params);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return recList;
	}

	@Override
	public List<String> listAllDMSAndADMsAndRecByLocAndUserIds(List<String> dmNames, List<String> locs, String status) {
		List<String> recList = null;
		try {
			StringBuffer hql = new StringBuffer();
			Map<String, Object> params = new HashMap<String, Object>();
			hql.append("select u.userId from User u where");
			if (status != null) {
				hql.append(" u.status='" + status + "'	and	");
			}
			hql.append("	  u.userRole in ('DM','ADM','Recruiter') ");
			if (locs != null && locs.size() > 0) {
				hql.append("and u.officeLocation in ?1");
				params.put("1", locs);
			}
			if (dmNames != null && dmNames.size() > 0) {
				hql.append(" and ((u.userId in (:dm)) or (u.assignedBdm in (:dm))		 or"
						+ " (u.assignedBdm in (select us.userId from User us where		 us.assignedBdm in (:dm)  ");
				hql.append("))) ");
				if (status != null) {
					hql.append("and u.status='" + status + "'		");
				}
				params.put("dm", dmNames);
			}
			recList = (List<String>) userDao.findByQuery(hql.toString(), -1, -1, params);

		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return recList;
	}


	@Override
	public List<UserDto> getAllIndiaDMsNoStatus() {
		List<UserDto> userdtolist = null;
		try {
			StringBuffer hql = new StringBuffer();
			hql.append("select u.userId,u.firstName,u.lastName from User u where u.status in ('ACTIVE', 'INACTIVE') and u.userRole ='IN_DM'");
			List<?> list = userDao.runQuery(hql.toString(), null, null);
			userdtolist = new ArrayList<UserDto>();
			if (list != null && list.size() > 0) {
				for (Object object : list) {
					Object[] obj = (Object[]) object;
					UserDto dto = new UserDto();
					dto.setUserId((String) obj[0]);
					dto.setFullName(Utils.concatenateTwoStringsWithSpace((String) obj[1], (String) obj[2]));
					userdtolist.add(dto);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return userdtolist;
	}

	@Override
	public List<UserDto> getAllDMsNoStatus() {
		List<UserDto> userdtolist = null;
		try {
			StringBuffer hql = new StringBuffer();
			hql.append("select u.userId,u.firstName,u.lastName from User u where u.status in ('ACTIVE', 'INACTIVE') and u.userRole ='DM'");
			List<?> list = userDao.runQuery(hql.toString(), null, null);
			userdtolist = new ArrayList<UserDto>();
			if (list != null && list.size() > 0) {
				for (Object object : list) {
					Object[] obj = (Object[]) object;
					UserDto dto = new UserDto();
					dto.setUserId((String) obj[0]);
					dto.setFullName(Utils.concatenateTwoStringsWithSpace((String) obj[1], (String) obj[2]));
					userdtolist.add(dto);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return userdtolist;
	}


	@Override
	public List<UserDto> getAllRecruiters(UserDto userDto, Boolean isIndia) {
		List<String> recruiterList = null;
		List<UserDto> userdtolist = null;
		Map<String, Object> params = new HashMap<String, Object>();
		try {
			StringBuffer sqlQuery = new StringBuffer();
			sqlQuery.append(" select (CASE WHEN u.first_name ISNULL THEN '' ELSE u.first_name END)||' '||(CASE WHEN "
					+ "u.last_name ISNULL THEN '' ELSE u.last_name END) as firstName, u.user_id,d.id,d.name from user_acct u,designation d where ");
			if(isIndia != null){
				if(isIndia){
					sqlQuery.append( " u.user_role in ('IN_Recruiter', 'IN_TL') and u.status='ACTIVE' and u.level=d.id ");
					}else{
						sqlQuery.append( " u.user_role in ('Recruiter', 'TL') and u.status='ACTIVE' and u.level=d.id ");
					}
			}else{
				sqlQuery.append( " u.user_role in ('Recruiter', 'TL') and u.status='ACTIVE' and u.level=d.id ");
			}
			
			
			if (userDto != null && (userDto.getUserRole().equals(UserRole.IN_DM) 
					|| userDto.getUserRole().equals(UserRole.IN_Recruiter) || userDto.getUserRole().equals(UserRole.IN_TL))) {
//				sqlQuery.append(" and ((u.assigned_bdm = ?1) or" + " (u.assigned_bdm in   (select us.user_id from user_acct us where us.assigned_bdm = ?1)))");
				if (userDto.getUserRole().equals(UserRole.IN_Recruiter) || userDto.getUserRole().equals(UserRole.IN_TL)) {
					sqlQuery.append(" and u.user_id = ?1");
					params.put("1", userDto.getUserId());
				} else {
					sqlQuery.append(" and ((u.assigned_bdm = ?1) or" + " (u.assigned_bdm in   (select us.user_id from user_acct us where us.assigned_bdm = ?1)))");
					params.put("1", userDto.getUserId());
				}
			}
			sqlQuery.append(" ORDER by firstName ASC");
			recruiterList = (List<String>) userDao.findBySQLQuery(sqlQuery.toString(), params);
			userdtolist = new ArrayList<UserDto>();
			if (recruiterList != null && recruiterList.size() > 0) {
				Iterator<?> iterator = recruiterList.iterator();
				while (iterator.hasNext()) {
					Object[] obj = (Object[]) iterator.next();
					UserDto dto = new UserDto();
					dto.setUserId((String) obj[1]);
					dto.setFullName((String) obj[0]);
					dto.setRecDesignation((String) obj[3]);
					userdtolist.add(dto);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
		}
		return userdtolist;
	}
	@Override
	public Object getServedDatesOfUser(List<String> startDates, List<String> endDates, Boolean isFullYear, List<Integer> yearList, String userName) {
		List<UserDto> list = null;
		try {
			StringBuffer sqlSelect = new StringBuffer();
			if (isFullYear != null && !isFullYear) {
				sqlSelect.append(" select u.user_id,u.join_date, u.level,d.min_start_count,d.max_start_count," + "(CASE WHEN u.relieving_date ISNULL THEN ( "
						+ "CASE WHEN u.status='INACTIVE' THEN u.updated_on ELSE( " + "CASE WHEN u.updated_on ISNULL THEN CURRENT_DATE "
						+ "ELSE CURRENT_DATE END) END) ELSE u.relieving_date END) AS SERVEDDATE " + ",u.status,"
						+ "((CASE WHEN u.first_name ISNULL THEN '' ELSE u.first_name END) ||' '|| "
						+ "(CASE WHEN u.last_name ISNULL THEN '' ELSE u.last_name END)) AS full_name,d.name,u.user_role,d.avg_start_count " + " from user_acct u,designation d "
						+ "where ((u.assigned_bdm='" + userName + "' OR u.user_id='" + userName + "' "
						+ "or u.assigned_bdm in (select ua.user_id from user_acct ua where ua.assigned_bdm='" + userName + "'))" + ") AND u.level = d.id ");

				if (startDates != null && startDates.size() > 0) {
					sqlSelect.append(" AND ( ");
					for (int i = 0; i < startDates.size(); i++) {
						sqlSelect.append("(u.join_date <= '" + startDates.get(i) + "' " + "AND (CASE WHEN u.relieving_date ISNULL THEN ( "
								+ "CASE WHEN u.status='INACTIVE' THEN u.updated_on ELSE( " + "CASE WHEN u.updated_on ISNULL THEN CURRENT_DATE "
								+ "ELSE CURRENT_DATE END) END) ELSE u.relieving_date END) >= '" + endDates.get(i) + "') ");
						if (i >= 0 && (i + 1) < startDates.size()) {
							sqlSelect.append("OR ");
						}
					}

					sqlSelect.append(" ) ");
				}
			} else {

				sqlSelect.append(" select u.user_id,u.join_date,u.level,d.min_start_count,d.max_start_count," + "(CASE WHEN u.relieving_date ISNULL THEN ( "
						+ "CASE WHEN u.status='INACTIVE' THEN u.updated_on ELSE( " + "CASE WHEN u.updated_on ISNULL THEN CURRENT_DATE "
						+ "ELSE CURRENT_DATE END) END) ELSE u.relieving_date END) AS SERVEDDATE " + ",u.status,"
						+ "((CASE WHEN u.first_name ISNULL THEN '' ELSE u.first_name END) ||' '|| "
						+ "(CASE WHEN u.last_name ISNULL THEN '' ELSE u.last_name END)) AS full_name,d.name,u.user_role,d.avg_start_count " + " from user_acct u,designation d "
						+ "where ((u.assigned_bdm='" + userName + "' OR u.user_id='" + userName + "' "
						+ "or u.assigned_bdm in (select ua.user_id from user_acct ua where ua.assigned_bdm='" + userName + "'))" + ") AND u.level = d.id ");

				if (yearList != null && yearList.size() > 0) {
					sqlSelect.append(" AND ( ");
					for (int i = 0; i < yearList.size(); i++) {
						sqlSelect.append("( " + "EXTRACT (YEAR FROM u.join_date) <=" + yearList.get(i)
								+ " AND EXTRACT(YEAR FROM (CASE WHEN u.relieving_date ISNULL THEN (" + "CASE WHEN u.status='INACTIVE' THEN u.updated_on ELSE( "
								+ " CASE WHEN u.updated_on ISNULL THEN CURRENT_DATE " + " ELSE CURRENT_DATE END) END) ELSE u.relieving_date END))>="
								+ yearList.get(i) + " ) ");
						if (i >= 0 && (i + 1) < yearList.size()) {
							sqlSelect.append("OR ");
						}
					}
					sqlSelect.append(" ) ");
				}
			}

			/*
			 * sqlSelect.append(""
			 * 
			 * 
			 * 
			 * 
			 * + "(u.join_date <= '01-31-2016' " +
			 * "AND (CASE WHEN u.relieving_date ISNULL THEN ( " +
			 * "CASE WHEN u.status='INACTIVE' THEN u.updated_on ELSE( " +
			 * "CASE WHEN u.updated_on ISNULL THEN CURRENT_DATE " +
			 * "ELSE u.updated_on END) END) ELSE u.relieving_date END) >= '01-01-2016') "
			 * + "OR " + " (u.join_date <= '06-30-2016' " +
			 * "AND (CASE WHEN u.relieving_date ISNULL THEN ( " +
			 * "CASE WHEN u.status='INACTIVE' THEN u.updated_on ELSE( " +
			 * "CASE WHEN u.updated_on ISNULL THEN CURRENT_DATE " +
			 * "ELSE u.updated_on END) END) ELSE u.relieving_date END) >= '06-01-2016')  "
			 * );
			 */

			log.info("Query :::: <<<<<<< " + sqlSelect.toString() + " >>>>>>>>>>>>>");
			List<?> resultList = userDao.findBySQLQuery(sqlSelect.toString(), null);
			if (resultList != null && resultList.size() > 0) {
				log.info("Size of the Records ::: " + resultList.size());
				list = new ArrayList<UserDto>();
				for (Object object : resultList) {
					Object obj[] = (Object[]) object;
					UserDto dto = new UserDto();
					dto.setUserId(Utils.getStringValueOfObj(obj[0]));
					dto.setJoinDate((Date) obj[1]);
					dto.setStrJoinDate(Utils.convertDateToString(dto.getJoinDate()));
					dto.setMinStartCount(Utils.getIntegerValueOfBigDecimalObj(obj[3]));
					dto.setMaxStartCount(Utils.getIntegerValueOfBigDecimalObj(obj[4]));
					dto.setServedDate((Date) obj[5]);
					dto.setFullName(Utils.getStringValueOfObj(obj[7]));
					dto.setDesignation(Utils.getStringValueOfObj(obj[8]));
					dto.setStatus(Utils.getStringValueOfObj(obj[6]));
					dto.setUserRole(UserRole.valueOf(Utils.getStringValueOfObj(obj[9])));
					dto.setAvgStartCount(Utils.getIntegerValueOfBigDecimalObj(obj[10]));
					list.add(dto);
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			e.printStackTrace();
		}
		return list;
	}

	@Override
	public List<?> getClientsUnderDM(Date srtDate, Date endDate, String dm) {
		StringBuffer sql = new StringBuffer();
		/*sql.append("select DISTINCT j.customer from job_order j where j.created_by='"+dm+"' and j.created_on>='"+srtDate+"'"
				+ "and j.created_on<='"+endDate+"'");*/
		sql.append("select DISTINCT j.customer from job_order j where j.created_by in (select u.user_id from user_acct u where u.user_id='"+dm+"' or "
				+ "u.assigned_bdm='"+dm+"') and j.created_on>='"+srtDate+"' and j.created_on<='"+endDate+"'");
		List<?> resultList = userDao.findBySQLQuery(sql.toString(), null);

		return resultList;
	}

	 

	@Override
	public List<?> getAllClinets() {
		StringBuffer sql = new StringBuffer();
		/*sql.append("select DISTINCT j.customer from job_order j where j.created_by='"+dm+"' and j.created_on>='"+srtDate+"'"
				+ "and j.created_on<='"+endDate+"'");*/
		//sql.append("select DISTINCT j.customer from job_order j where j.delete_flg = 0 ORDER BY j.customer ASC");
		sql.append("select DISTINCT c.client_name from client_names c ORDER BY c.client_name ASC");
		List<?> resultList = userDao.findBySQLQuery(sql.toString(), null);

		return resultList;
	}
	@Override
	public void permanentDeleteUser(String userId, Date relievDate) throws ServiceException {
		try {
			User user = userDao.findById(userId);
			userDao.delete(user);
			// userDao.delete(user);
		} catch (RuntimeException e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
			throw new ServiceException(e);
		}
	}
	 
}