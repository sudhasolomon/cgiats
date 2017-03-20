/*
 * Reindex.java Jul 30, 2012
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
import com.uralian.cgiats.model.Candidate;
import com.uralian.cgiats.service.CandidateService;
import com.uralian.cgiats.service.ServiceException;
import com.uralian.cgiats.service.impl.CandidateServiceImpl;

/**
 * Run it with:
 * 
 * <pre>
 * mvn exec:java -Dexec.mainClass="com.uralian.cgiats.util.Reindex" -Dexec:args="batchSize startId endId" -Dexec.classpathScope=test
 * </pre>
 * 
 * @author Vlad Orzhekhovskiy
 */
public class Reindex implements Runnable
{
	@Autowired
	private CandidateService candidateService;

	@Autowired
	private DataSource dataSource;

	private int batchSize = 500;

	private Integer idToStart = null;
	private Integer idToEnd = null;

	/**
	 * @param batchSize the batchSize to set.
	 */
	public void setBatchSize(int batchSize)
	{
		this.batchSize = batchSize;
		if (batchSize < 1)
			throw new IllegalArgumentException("Batch size must be greater than 0");
	}

	/**
	 * @param idToStart the idToStart to set.
	 */
	public void setIdToStart(Integer idToStart)
	{
		this.idToStart = idToStart;
		if (idToStart < 1)
			throw new IllegalArgumentException("Start id must be greater than 0");
	}

	/**
	 * @param idToEnd the idToEnd to set.
	 */
	public void setIdToEnd(Integer idToEnd)
	{
		this.idToEnd = idToEnd;
		if (idToEnd < 1)
			throw new IllegalArgumentException("End id must be greater than 0");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run()
	{
		disableLogging();

		try
		{
			reindexResumes();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
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
	 * @throws ServiceException
	 */
	public void reindexResumes() throws ServiceException
	{
		// find min/max resume id's
		int minmax[] = findMinMaxIds();
		if (minmax == null || minmax.length == 0)
		{
			System.out.println("The resume table is empty, nothing to reindex");
			return;
		}

		int minId = minmax[0];
		int maxId = minmax[1];
		System.out.println("Resume id range: " + minId + " - " + maxId);

		int startId = idToStart != null ? idToStart : minId;
		int lastId = idToEnd != null ? idToEnd : maxId;
		System.out.println("Reindexing id range: " + startId + " - " + lastId);
		while (startId < lastId)
		{
			int endId = Math.min(startId + batchSize, lastId);
			System.out.print("Reindexing resumes from " + startId + " to " + endId
			    + ": ");

			List<Integer> ids = retrieveIds(startId, endId);
			for (Integer id : ids)
			{
				Candidate candidate = candidateService.getCandidate(id, true, true);
				candidate.parseDocument();
				candidateService.updateCandidate(candidate);
			}

			System.out.println("done.");
			startId = endId;
		}
	}

	/**
	 * @return
	 */
	protected int[] findMinMaxIds()
	{
		JdbcTemplate jt = new JdbcTemplate(dataSource);

		String sql = "select min(candidate_id), max(candidate_id) from candidate";
		Map<String, Object> minmaxMap = jt.queryForMap(sql);
		Integer minId = (Integer) minmaxMap.get("min");
		Integer maxId = (Integer) minmaxMap.get("max");

		return minId != null && maxId != null ? new int[] { minId, maxId } : null;
	}

	/**
	 * @param minId
	 * @param maxId
	 * @return
	 */
	protected List<Integer> retrieveIds(int minId, int maxId)
	{
		JdbcTemplate jt = new JdbcTemplate(dataSource);

		String sql = "select candidate_id from candidate where candidate_id between ? and ?";
		List<Integer> ids = jt.queryForList(sql, Integer.class, minId, maxId - 1);
		return ids;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		Reindex reindex = new Reindex();

		ConfigurableApplicationContext context = new ClassPathXmlApplicationContext(
		    "/spring/util-context.xml");
		context.registerShutdownHook();
		context.getAutowireCapableBeanFactory().autowireBean(reindex);

		reindex.run();
	}
}