/**
 * 
 */
package com.uralian.cgiats.dao.impl;

import java.io.Serializable;
import java.sql.Blob;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.springframework.stereotype.Repository;

import com.uralian.cgiats.dao.AgencyDetailsDao;
import com.uralian.cgiats.dao.EmailConfigurationDao;
import com.uralian.cgiats.model.AgencyDetails;
import com.uralian.cgiats.model.EmailConfiguration;
import com.uralian.cgiats.model.User;

/**
 * @author Sreenath
 * 
 */
@Repository
public class EmailConfigurationDaoImpl extends GenericDaoImpl<EmailConfiguration, Integer> implements EmailConfigurationDao {

	/**
	 * @param entityType
	 */
	protected EmailConfigurationDaoImpl() {
		super(EmailConfiguration.class);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.uralian.cgiats.dao.EmailConfigurationDao#saveOrUpdate(com.uralian
	 * .cgiats.model.EmailConfiguration)
	 */
	@Override
	public EmailConfiguration saveOrUpdate(EmailConfiguration emailConfiguration) {
		try {
			getHibernateTemplate().getSessionFactory().getCurrentSession().saveOrUpdate(emailConfiguration);
			return emailConfiguration;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

}
