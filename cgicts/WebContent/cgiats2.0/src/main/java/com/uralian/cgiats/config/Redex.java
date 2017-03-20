package com.uralian.cgiats.config;

import java.util.Date;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;

import com.uralian.cgiats.model.Candidate;
import com.uralian.cgiats.model.Resume;

public class Redex {
	private static final Logger LOGGER = Logger.getLogger(Redex.class.getName());
	private static FileHandler fh = null;

	public static void main(String args[]) {
		try {
			fh = new FileHandler("redex.log");
			fh.setFormatter(new SimpleFormatter());
			LOGGER.addHandler(fh);
			Configuration cfg = new Configuration();
			cfg.configure("hibernate.cfg.xml");
			SessionFactory factory = cfg.buildSessionFactory();
			Session session = factory.openSession();
			System.out.println("Started");
			LOGGER.info("Started");

			FullTextSession fullTextSession = Search.getFullTextSession(session);
			Transaction tx = fullTextSession.beginTransaction();
			Criteria criteria = fullTextSession.createCriteria(Candidate.class).setProjection(Projections.property("id"));
			criteria.addOrder(Order.asc("id"));
			// criteria.setFirstResult(0);
			// criteria.setMaxResults(50000);
			////
			List<Object> candidateIds = criteria.list();
			fullTextSession.clear();
			int count = 0;
			long startTime = System.currentTimeMillis();
			System.out.println("CandidateIDs size : " + candidateIds.size() + " \t" + (new Date()));
			for (Object candidateId : candidateIds) {

				try {
					criteria = fullTextSession.createCriteria(Resume.class);
					criteria.add(Restrictions.eq("id", candidateId));
					// criteria.setFirstResult(i);
					// criteria.setMaxResults(maxResults);
					Resume resume = (Resume) criteria.uniqueResult();
					if (resume != null) {
						Candidate candidate = resume.getCandidate();
						count++;
						try {
							fullTextSession.index(resume);
							fullTextSession.index(candidate);
							tx.commit();
							tx = fullTextSession.beginTransaction();
						} catch (HibernateException e) {
							System.out.println("Transaction Hibernate Exception" + e);
							e.printStackTrace();
							tx.rollback();
							LOGGER.severe("Transaction Hibernate Exception" + e);
							tx = fullTextSession.beginTransaction();
						} catch (Exception e) {
							System.out.println("Transaction Exception " + e.getMessage());
							tx.rollback();
							e.printStackTrace();
							LOGGER.severe("Transaction Exception" + e);
							// session.clear();
							// fullTextSession.clear();
							tx = fullTextSession.beginTransaction();
						} catch (Throwable e) {
							System.out.println("Transaction Throwable " + e.getMessage());
							tx.rollback();
							e.printStackTrace();
							LOGGER.severe("Transaction Throwable" + e);
							// session.clear();
							// fullTextSession.clear();
							tx = fullTextSession.beginTransaction();
						}
					}
					fullTextSession.flushToIndexes();
					fullTextSession.clear();
					if (count % 1000 == 0) {
						long endTime = System.currentTimeMillis();
						long time = (endTime - startTime);
						LOGGER.info(count + " records indexing completed in : " + ((int) ((time / (1000 * 60)) % 60)) + " minutes and "
								+ ((int) (time / 1000) % 60) + " seconds" + "  \t" + (new Date()));
						startTime = System.currentTimeMillis();
					}
				} catch (HibernateException e) {
					System.out.println("Fetching Resume HibernateException" + e);
					LOGGER.severe("Fetching Resume HibernateException" + e);
					e.printStackTrace();
					tx.rollback();
					tx = fullTextSession.beginTransaction();
				} catch (Exception e) {
					System.out.println("Fetching Resume Exception " + e.getMessage());
					LOGGER.severe("Fetching Resume Exception" + e);
					tx.rollback();
					e.printStackTrace();
					// session.clear();
					// fullTextSession.clear();
					tx = fullTextSession.beginTransaction();
				} catch (Throwable e) {
					System.out.println("Fetching Resume Throwable " + e.getMessage());
					LOGGER.severe("Fetching Resume Throwable" + e);
					tx.rollback();
					e.printStackTrace();
					// session.clear();
					// fullTextSession.clear();
					tx = fullTextSession.beginTransaction();
				}
			}
		} catch (HibernateException e) {
			System.out.println("Hibernate Exception" + e);
			LOGGER.severe("HibernateException" + e);
			e.printStackTrace();
		} catch (Exception e) {
			System.out.println("Main Exception " + e);
			LOGGER.severe("Main Exception" + e);
			e.printStackTrace();
		} catch (Throwable e) {
			System.out.println("Main Throwable " + e);
			LOGGER.severe("Main Throwable" + e);
			e.printStackTrace();
		}
		LOGGER.info("Indexing completted ****");
	}
}
