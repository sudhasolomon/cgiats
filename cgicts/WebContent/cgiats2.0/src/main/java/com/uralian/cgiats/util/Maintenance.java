/*
 * Maintenance.java Jul 31, 2012
 *
 * Copyright 2012 Uralian, LLC. All rights reserved.
 */
package com.uralian.cgiats.util;

import static org.apache.log4j.Logger.getLogger;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.apache.log4j.Level;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;

import com.uralian.cgiats.dao.impl.CandidateDaoImpl;
import com.uralian.cgiats.service.impl.CandidateServiceImpl;

/**
 * Run it with:
 * 
 * <pre>
 * mvn exec:java -Dexec.mainClass="com.uralian.cgiats.util.Maintenance" -Dexec.classpathScope=test
 * </pre>
 * 
 * @author Vlad Orzhekhovskiy
 */
public class Maintenance implements Runnable
{
	@Autowired
	private DataSource dataSource;

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run()
	{
		disableLogging();
		openJobOrders();
	}

	/**
	 */
	public void disableLogging()
	{
		getLogger("org.hibernate").setLevel(Level.ERROR);
		getLogger("org.springframework").setLevel(Level.ERROR);
		getLogger("org.springframework.orm").setLevel(Level.ERROR);
		getLogger(CandidateServiceImpl.class).setLevel(Level.WARN);
		getLogger(CandidateDaoImpl.class).setLevel(Level.WARN);
	}

	/**
	 */
	protected void openJobOrders()
	{
		JdbcTemplate jt = new JdbcTemplate(dataSource);

		StringBuilder sql = new StringBuilder();
		sql.append("select order_id from job_order");
		sql.append(" where status = 'ASSIGNED'");
		sql.append(" and created_on + hrs_open * interval '1 hour' <= now()");
		final List<Integer> ids = jt.queryForList(sql.toString(), Integer.class);
		
		String sqlUpdate ="update job_order set status='OPEN', assigned_to=null where order_id=?";
		jt.batchUpdate(sqlUpdate, new BatchPreparedStatementSetter()
		{
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException
			{
				ps.setInt(1, ids.get(i));
			}
			
			@Override
			public int getBatchSize()
			{
				return ids.size();
			}
		});

		System.out.println(ids.size() + " job order(s) opened");
	}

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		Maintenance mtn = new Maintenance();

		ConfigurableApplicationContext context = new ClassPathXmlApplicationContext(
		    "/spring/util-context.xml");
		context.registerShutdownHook();
		context.getAutowireCapableBeanFactory().autowireBean(mtn);

		mtn.run();
	}
}