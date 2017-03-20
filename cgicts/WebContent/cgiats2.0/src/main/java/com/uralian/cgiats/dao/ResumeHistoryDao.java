/**
 * 
 */
package com.uralian.cgiats.dao;

import java.util.List;

import com.uralian.cgiats.model.ResumeHistory;

/**
 * @author Parameshwar
 *
 */
public interface ResumeHistoryDao extends GenericDao<ResumeHistory, Integer> {
	
	
	/**
	 * @return
	 */
	public List<String> findAllUsers();
	
}
