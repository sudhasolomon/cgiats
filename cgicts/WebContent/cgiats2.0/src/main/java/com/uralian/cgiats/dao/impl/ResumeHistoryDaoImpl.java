/**
 * 
 */
package com.uralian.cgiats.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.uralian.cgiats.dao.ResumeHistoryDao;
import com.uralian.cgiats.model.ResumeHistory;

/**
 * @author Parameshwar
 *
 */
@SuppressWarnings("unchecked")
@Repository
public class ResumeHistoryDaoImpl extends GenericDaoImpl<ResumeHistory, Integer> implements ResumeHistoryDao {

	protected ResumeHistoryDaoImpl() {
		super(ResumeHistory.class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.uralian.cgiats.dao.ResumeHistoryDao#findAllUsers()
	 */
	public List<String> findAllUsers() {
		try {
			String hql = "select distinct u.userId from User u where u.userId is not null order by u.userId";
			List<String> list = (List<String>) runQuery(hql.toString());
			return list;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;

	}
}
