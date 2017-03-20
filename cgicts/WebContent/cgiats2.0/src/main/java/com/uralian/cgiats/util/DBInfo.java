/*
 * DBInfo.java May 16, 2012
 *
 * Copyright 2012 Uralian, LLC. All rights reserved.
 */
package com.uralian.cgiats.util;

import static org.apache.log4j.Logger.getLogger;

import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.log4j.Level;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;

import com.uralian.cgiats.dao.impl.CandidateDaoImpl;
import com.uralian.cgiats.service.impl.CandidateServiceImpl;

/**
 * Run it with:
 * 
 * <pre>
 * mvn exec:java -Dexec.mainClass="com.uralian.cgiats.util.DBInfo" -Dexec.classpathScope=test
 * </pre>
 * 
 * @author Vlad Orzhekhovskiy
 */
public class DBInfo implements Runnable
{
	@Autowired
	private DataSource dataSource;

	/**
   * 
   */
	public DBInfo()
	{
		disableLogging();
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run()
	{
		JdbcTemplate jt = new JdbcTemplate(dataSource);
		int userCount = jt.queryForInt("select count(*) from user_acct");
		List<Map<String, Object>> userList = jt
		    .queryForList("select user_role, count(*) as role_count from user_acct group by user_role");
		int resumeCount = jt.queryForInt("select count(*) from candidate");
		List<Map<String, Object>> doctypeList = jt
		    .queryForList("select doctype, count(*) as type_count from candidate group by doctype");

		System.out.println("Database Info");
		System.out.println("=============");

		System.out.println("Users accounts: " + userCount);
		for (Map<String, Object> map : userList)
			System.out.println("\t" + map.get("user_role") + ": "
			    + map.get("role_count"));
		System.out.println("Resumes: " + resumeCount);
		for (Map<String, Object> map : doctypeList)
			System.out.println("\t" + map.get("doctype") + ": "
			    + map.get("type_count"));
	}

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		DBInfo dbi = new DBInfo();

		ConfigurableApplicationContext context = new ClassPathXmlApplicationContext(
		    "/spring/util-context.xml");
		context.registerShutdownHook();
		context.getAutowireCapableBeanFactory().autowireBean(dbi);

		dbi.run();
	}
}