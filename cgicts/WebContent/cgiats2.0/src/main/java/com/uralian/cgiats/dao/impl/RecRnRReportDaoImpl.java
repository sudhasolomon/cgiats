/**
 * 
 */
package com.uralian.cgiats.dao.impl;

import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.uralian.cgiats.dao.RecRnRReportDao;
import com.uralian.cgiats.model.SubmitalStats;

/**
 * @author Sreenath
 * 
 */
@Repository
public class RecRnRReportDaoImpl extends GenericDaoImpl<SubmitalStats, Integer>
		implements RecRnRReportDao {

	/**
	 * @param entityType
	 */
	protected RecRnRReportDaoImpl() {
		super(SubmitalStats.class);
	}

}
