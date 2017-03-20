package com.uralian.cgiats.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.uralian.cgiats.dao.ActiveAccountsDao;
import com.uralian.cgiats.model.ActiveAccounts;
import com.uralian.cgiats.model.User;
import com.uralian.cgiats.model.UserRole;

/**
 * @author rajashekhar
 */
@SuppressWarnings("unchecked")
@Repository
public class ActiveAccountsDaoImpl extends GenericDaoImpl<ActiveAccounts, Integer> implements ActiveAccountsDao {

	protected ActiveAccountsDaoImpl() {
		super(ActiveAccounts.class);
	}

	@Override
	public List<User> loadUsers(UserRole userRole) {
		try {
			StringBuffer hql = new StringBuffer();
			hql.append(" from User u where status='ACTIVE' and userRole=:userRole order by u.userId asc");
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("userRole", userRole);
			return (List<User>) getHibernateTemplate().findByNamedParam(hql.toString(), "userRole", userRole);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	public List<ActiveAccounts> loadActAccounts() {
		try {

			StringBuffer hql = new StringBuffer();
			hql.append(" from ActiveAccounts aa where deleteFlag=0 order by coalesce(aa.updatedOn,aa.createdOn) desc");
			return findByQuery(hql.toString(), null);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

}
