/*
 * UserDaoImpl.java May 16, 2012
 *
 * Copyright 2012 Uralian, LLC. All rights reserved.
 */
package com.uralian.cgiats.dao.impl;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.uralian.cgiats.dao.UserDao;
import com.uralian.cgiats.model.MobileRegistration;
import com.uralian.cgiats.model.Pages;
import com.uralian.cgiats.model.PagesHeader;
import com.uralian.cgiats.model.User;
import com.uralian.cgiats.model.UserRoles;
import com.uralian.cgiats.util.Constants;

/**
 * @author Vlad Orzhekhovskiy
 */
@Repository
@SuppressWarnings("unchecked")
public class UserDaoImpl extends GenericDaoImpl<User, String> implements UserDao {
	/**
	 */
	public UserDaoImpl() {
		super(User.class);
	}

	@Override
	@Transactional(readOnly = true)
	public void saveRegistration(MobileRegistration register) {
		try {

			getHibernateTemplate().saveOrUpdate(register);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<MobileRegistration> listRegistrations() {
		try {
			return getHibernateTemplate().loadAll(MobileRegistration.class);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	public List<PagesHeader> loadMenuCfgData() {
		try {

			return getHibernateTemplate().loadAll(PagesHeader.class);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	public void saveCfg(UserRoles ur) {
		try {
			getHibernateTemplate().saveOrUpdate(ur);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
	}

	@Override
	public List<UserRoles> loadUserRole(User user) {
		try {
			StringBuffer hql = new StringBuffer();
			hql.append(" from UserRoles where (userRole='" + user.getUserRole().toString() + "'" + " or userRole.user.userId='" + user.getUserId() + "'"
					+ ") and status='YES'");

			Query qry = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(hql.toString());
			qry.setCacheable(true);
			List<UserRoles> list = qry.list();

			return list;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	public UserRoles loadPage(String s, User user) {
		try {
			StringBuffer hql = new StringBuffer();
			int id = Integer.parseInt(s);
			hql.append(" from UserRoles where pageName.pagesId='" + id + "'");
			if (user != null) {
				hql.append(" and userRole='" + user.getUserRole().toString() + "'");
			}
			Query qry = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(hql.toString());
			List<UserRoles> list = qry.list();
			if (list != null && list.size() > 0)
				return list.get(0);
			else
				return null;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	public Pages loadPages(String s) {
		try {
			StringBuffer hql = new StringBuffer();
			hql.append(" from Pages where pageName='" + s + "'");

			Query qry = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(hql.toString());
			List<Pages> list = qry.list();
			return list.get(0);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.uralian.cgiats.dao.UserDao#getUserByCredentials()
	 */
	@Override
	public User getUserByCredentials(String username, String password,Boolean isActive) {
		User user = null;
		try {
			Criteria criteria = getHibernateTemplate().getSessionFactory().getCurrentSession().createCriteria(User.class);
			criteria.add(Restrictions.and(Restrictions.eq("userId", username), Restrictions.eq("password", password)));
			if(isActive!=null && isActive){
				criteria.add(Restrictions.eq("status", Constants.ACTIVE));
			}
			user = (User) criteria.uniqueResult();
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return user;
	}

	@Override
	public User findByEmail(String email) {

		User user = null;
		try {
			Criteria criteria = getHibernateTemplate().getSessionFactory().getCurrentSession().createCriteria(User.class);
			criteria.add(Restrictions.eq("email", email));
			user = (User) criteria.uniqueResult();
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return user;
	}
}
