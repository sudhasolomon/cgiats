/*
 * CleanupDB.java May 16, 2012
 *
 * Copyright 2012 Uralian, LLC. All rights reserved.
 */
package com.uralian.cgiats.util;

import static org.apache.log4j.Logger.getLogger;

import java.util.Scanner;

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
 * mvn exec:java -Dexec.mainClass="com.uralian.cgiats.util.CleanupDB" -Dexec.classpathScope=test
 * </pre>
 * 
 * @author Vlad Orzhekhovskiy
 */
public class CleanupDB implements Runnable
{
	public static final String USERNAME = "cgiadmin";
	public static final String PASSWORD = "cgiats2012!";

	@Autowired
	private DataSource dataSource;

	/**
   */
	public CleanupDB()
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
		Scanner sc = new Scanner(System.in);

		System.out.print("Enter username: ");
		String username = sc.next();
		System.out.print("Enter password: ");
		String password = sc.next();

		if (!USERNAME.equals(username) || !PASSWORD.equals(password))
		{
			System.err.println("\nInvalid username and/or password, exiting...");
			return;
		}

		System.out.print("Are you sure you want to wipe out all data? [y/n] ");
		String answer = sc.next();
		if (!"y".equalsIgnoreCase(answer))
			return;

		System.out.print("\nRemoving all data from the database... ");
		JdbcTemplate jt = new JdbcTemplate(dataSource);
		jt.update("delete from property");
		jt.update("delete from candidate");
		jt.update("delete from resume");
		System.out.println("done.");
	}

	/**
	 * Program entry point.
	 * 
	 * @param args
	 */
	public static void main(String[] args)
	{
		CleanupDB cdb = new CleanupDB();

		ConfigurableApplicationContext context = new ClassPathXmlApplicationContext(
		    "/spring/util-context.xml");
		context.registerShutdownHook();
		context.getAutowireCapableBeanFactory().autowireBean(cdb);

		cdb.run();
	}
}