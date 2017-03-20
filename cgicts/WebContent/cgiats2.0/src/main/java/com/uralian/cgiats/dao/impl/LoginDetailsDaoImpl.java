/**
 * 
 */
package com.uralian.cgiats.dao.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.uralian.cgiats.dao.LoginDetailsDao;
import com.uralian.cgiats.model.LoginAttempts;
import com.uralian.cgiats.model.LoginInfoDto;
import com.uralian.cgiats.util.Constants;
import com.uralian.cgiats.util.Utils;

/**
 * @author Parameshwar
 *
 */
@SuppressWarnings("unchecked")
@Repository
public class LoginDetailsDaoImpl extends GenericDaoImpl<LoginAttempts, Integer> implements LoginDetailsDao {
	/**
	 *
	 */
	public LoginDetailsDaoImpl() {
		super(LoginAttempts.class);
	}

	@Override
	public List<LoginInfoDto> getLoginAttemptsDetails(Date dateStart, Date dateEnd) {
		try {
			List<LoginInfoDto> infoDtos = new ArrayList<LoginInfoDto>();
			StringBuffer sql = new StringBuffer(); 
			if(dateStart !=null && dateEnd != null){
				sql.append("select object1.user_id,"
						+ "       object1.login, "
						+ "       object1.logout, "
						+ "       object2.login_count, "
						+ "       object2.duration from "
						+ " (select obj1.user_id AS user_id, obj1.login AS login, obj2.logout AS logout "
						+ " from ( "
						+ " select l.user_id AS user_id, l.login_date AS login, l.logout_date AS "
						+ "  logout, (EXTRACT(EPOCH FROM l.logout_date - l.login_date)) AS duration, "
						+ "   row_number() OVER(PARTITION BY l.user_id, date (l.login_date) ORDER BY "
						+ "    l.login_date) AS rn, row_number() OVER(PARTITION BY l.user_id, date ( "
						+ "    l.logout_date) ORDER BY l.logout_date DESC) AS rn2 from login_attempts l "
						+ "     where date (l.created_date) >= '"+dateStart+"' and date ( "
						+ "     l.created_date) <= '"+dateEnd+"' order by l.user_id, "
						+ "      l.login_date) AS obj1, "
						+ "      (select l.user_id AS user_id, l.login_date AS login "
						+ "      , l.logout_date AS logout, (EXTRACT(EPOCH FROM l.logout_date -  l.login_date)) AS duration,"
						+ " row_number() OVER(PARTITION BY l.user_id, "
						+ "        date (l.login_date) ORDER BY l.login_date DESC) AS rn, "
						+ "row_number() OVER(      PARTITION BY l.user_id, date (l.logout_date) ORDER BY l.logout_date DESC  ) AS rn2 "
						+ " from login_attempts l where date (l.created_date) >=  '"+dateStart+"' and date (l.created_date) <=   '"+dateEnd+"' "
						+ " order by l.user_id, l.login_date) AS    obj2 "
						+ "            where (obj1.rn = 1 and obj2.rn = 1) and obj1.user_id =  obj2.user_id "
						+ " and date (obj1.login) = date (obj2.login)) AS object1, "
						+ " (select obj.user_id AS user_id, date (obj.login) AS login, count(date (obj.login)) AS login_count,"
						+ " sum(obj.duration) AS duration from (select"
						+ "  l.user_id AS user_id, l.login_date AS login, l.logout_date AS logout, "
						+ "( EXTRACT(EPOCH FROM l.logout_date - l.login_date)) AS duration, row_number( )"
						+ " OVER(PARTITION BY l.user_id, date (l.login_date) ORDER BY l.login_date) AS rn, "
						+ " row_number() OVER(PARTITION BY l.user_id, date (l.logout_date) "
						+ "        ORDER BY l.logout_date DESC) AS rn2 from login_attempts l where date (l.created_date) >= '"+dateStart+"' "
						+ " and date (l.created_date) <= '"+dateEnd+"' "
						+ " order by l.user_id, l.login_date) AS obj"
						+ " group by obj.user_id, date (obj.login)) AS object2 where object1.user_id = object2.user_id and "
						+ "      object2.login = date (object1.login)");
			}
			 			
			List<?> result = findBySQLQuery(sql.toString(), null);
			if(result != null){
				Iterator<?> itr = result.iterator();
			String strTodayDate = Utils.convertDateToString(new Date());
			List<LoginInfoDto> logInInfoDtoList = new ArrayList<LoginInfoDto>();
			List<LoginInfoDto> logOutInfoDtoList = new ArrayList<LoginInfoDto>();
				while(itr.hasNext()){
					Object[] obj = (Object[]) itr.next();
					LoginInfoDto infoDto = new LoginInfoDto();
					infoDto.setCreatedBy(Utils.getStringValueOfObj(obj[0]));
					infoDto.setLoginDate(Utils.convertDateToString_HH_MM_A((Date)obj[1]));
					infoDto.setLogoutDate(Utils.convertDateToString_HH_MM_A((Date)obj[2]));
					infoDto.setDuration(Utils.getHoursFromSeconds(Utils.getIntegerValueOfDoubleObj(obj[4])));
					infoDto.setTotal(Utils.getIntegerValueOfBigDecimalObj(obj[3]).toString());
					
					infoDto.setStartDate(Utils.convertDateToString_HH_MM_SS((Date)obj[1]));
					String loginDate = Utils.convertDateToString((Date)obj[1]);
					
					infoDto.setStatus("logout");
					
					if (!loginDate.equals(strTodayDate)) {
//						infoDtos.add(infoDto);
						logOutInfoDtoList.add(infoDto);
					}else{
						Iterator<LoginInfoDto> loginInfoIteratator = infoDtos.iterator();
						while(loginInfoIteratator.hasNext()){
							LoginInfoDto loginInfoDto=loginInfoIteratator.next();
							if(loginInfoDto.getCreatedBy().equals(infoDto.getCreatedBy())){
								loginInfoIteratator.remove();
								break;
							}
						}
						if(infoDto.getLogoutDate()==null){
							infoDto.setStatus("login");
							infoDto.setLogoutDate(" - ");
						}
						if(infoDto.getStatus().equals("login")){
							logInInfoDtoList.add(infoDto);
						}else{
							logOutInfoDtoList.add(infoDto);
						}
//						infoDtos.add(infoDto);
					}
				}
				if(logOutInfoDtoList!=null && logOutInfoDtoList.size()>0){
				Collections.sort(logOutInfoDtoList, new Comparator<LoginInfoDto>() {
					@Override
					public int compare(LoginInfoDto o1, LoginInfoDto o2) {
						// TODO Auto-generated method stub
						return Utils.convertStringToDate_HH_MM_SS(o2.getStartDate()).compareTo(Utils.convertStringToDate_HH_MM_SS(o1.getStartDate()));
					}
				});
				}
				if(logInInfoDtoList!=null && logInInfoDtoList.size()>0){
					Collections.sort(logInInfoDtoList, new Comparator<LoginInfoDto>() {
						@Override
						public int compare(LoginInfoDto o1, LoginInfoDto o2) {
							// TODO Auto-generated method stub
							return Utils.convertStringToDate_HH_MM_SS(o2.getStartDate()).compareTo(Utils.convertStringToDate_HH_MM_SS(o1.getStartDate()));
						}
					});
					}
				infoDtos.addAll(logInInfoDtoList);
				infoDtos.addAll(logOutInfoDtoList);
			}
			
			
			
			
			/*
			List<String> paramNames = new ArrayList<String>();
			List<Object> paramValues = new ArrayList<Object>();

			StringBuilder hql = new StringBuilder();
			hql.append("select createdBy,to_date(to_char(createdOn, 'YYYY/MM/DD'), 'YYYY/MM/DD') as createdOn,"
					+ "min(loginDate) as loginDate, max(logoutDate) as logoutDate,"
					+ "max(loginDate),count(createdBy) as total,sum(durationTime) as durationTime from LoginAttempts");
			hql.append(" where 1=1");
			if (dateStart != null) {
				hql.append(" and createdOn >= :startDate");
				paramNames.add("startDate");
				paramValues.add(dateStart);
			}
			if (dateEnd != null) {
				hql.append(" and createdOn <= :endDate");
				paramNames.add("endDate");
				paramValues.add(DateUtils.addDays(dateEnd, 1));
			}
			hql.append(" group by createdBy,to_date(to_char(createdOn, 'YYYY/MM/DD'), 'YYYY/MM/DD')");
			hql.append(" order by to_date(to_char(createdOn, 'YYYY/MM/DD'), 'YYYY/MM/DD'),createdBy");
			List<?> result = runQuery(hql.toString(), paramNames.toArray(new String[0]), paramValues.toArray());

			List<LoginAttempts> logAttempts = new ArrayList<LoginAttempts>();
			for (Object record : result) {
				LoginAttempts loginattempts = new LoginAttempts();
				Object[] tuple = (Object[]) record;
				String username = (String) tuple[0];
				Date logout = (Date) tuple[3];
				Date login = (Date) tuple[4];
				Number total = (Number) tuple[5];
				Number duration = (Number) tuple[6];

				String durationTime = null;
				if (duration != null) {
					long totalDuration = (Long) duration;
					durationTime = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(totalDuration),
							TimeUnit.MILLISECONDS.toMinutes(totalDuration) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(totalDuration)),
							TimeUnit.MILLISECONDS.toSeconds(totalDuration) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(totalDuration)));
				}

				if (logout != null) {

					if (login.before(logout)) {
						loginattempts.setStatus("logout");
					} else {
						loginattempts.setStatus("login");
					}

				} else {
					durationTime = "";
					loginattempts.setStatus("login");
				}

				loginattempts.setCreatedBy(username);
				loginattempts.setCreatedOn((Date) tuple[1]);
				loginattempts.setLoginDate((Date) tuple[2]);
				loginattempts.setLogoutDate(logout);
				loginattempts.setTotal((Long) total);
				loginattempts.setDuration(durationTime);
				logAttempts.add(loginattempts);

			}
			*/
			return infoDtos;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	public List<Long> getLoginAttemptByDate(Date loginDate, String userId) {
		try {
			List<String> paramNames = new ArrayList<String>();
			List<Object> paramValues = new ArrayList<Object>();

			StringBuilder hql = new StringBuilder();
			hql.append("select count(durationTime) as durationTime from LoginAttempts");
			hql.append(" where 1=1 and createdBy = :userId");
			paramNames.add("userId");
			paramValues.add(userId);

			if (loginDate != null) {
				hql.append(" and COALESCE(cast(createdOn as date)) =  cast(:loginDate as date)");
				paramNames.add("loginDate");
				paramValues.add(loginDate);
			}
			List<?> result = runQuery(hql.toString(), paramNames.toArray(new String[0]), paramValues.toArray());

			return (List<Long>) result;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

}
