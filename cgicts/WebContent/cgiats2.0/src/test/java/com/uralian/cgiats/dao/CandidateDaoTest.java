package com.uralian.cgiats.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.io.IOUtils;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.uralian.cgiats.model.Candidate;
import com.uralian.cgiats.model.ContentType;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/spring/test-context.xml")
@Transactional
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = false)
public class CandidateDaoTest
{
	protected final Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	private DataSource dataSource;

	@Autowired
	private CandidateDao candidateDao;

	private static int johnId;

	@Test
	public void initDB()
	{
		log.info("Cleaning DB");

		JdbcTemplate jt = new JdbcTemplate(dataSource);
		jt.execute("select count(*) from dual");
		jt.update("delete from keyword");
		jt.update("delete from property");
		jt.update("delete from resume");
		jt.update("delete from candidate");
	}

	@Test
	public void saveMsWord() throws IOException
	{
		log.info(">>>> Saving candidate with Word resume");

		Candidate candidate = new Candidate();
		candidate.setEmail("john@doe.com");
		candidate.setFirstName("John");
		candidate.setLastName("Doe");
		candidate.setPhone("505-123-4444");
		candidate.getAddress().setState("CO");
		candidate.getAddress().setZipcode("80525");
		candidate.setTitle("Developer");
		loadMsWordFile(candidate, "/docs/ResumeTest.doc");
		candidate.parseDocument();
		candidateDao.save(candidate);
		assertNotNull(candidate.getId());
		johnId = candidate.getId();
	}

	@Test
	public void saveText() throws IOException
	{
		log.info(">>>> Saving candidate with Text resume");

		Candidate candidate = new Candidate();
		candidate.setEmail("jane@doe.com");
		candidate.setFirstName("Jane");
		candidate.setLastName("Doe");
		candidate.setPhone("505-404-2030");
		candidate.getAddress().setState("GA");
		candidate.getAddress().setZipcode("30309");
		candidate.setTitle("Enterprise Architect");
		loadTextFile(candidate, "/docs/ResumeTest.txt");
		candidate.parseDocument();
		candidateDao.save(candidate);

		assertNotNull(candidate.getId());
	}

	@Test
	public void findAll()
	{
		log.info(">>>> Retrieving all candidates with findAll(): no document, no keywords");

		List<Candidate> candidates = candidateDao.findAll();
		assertNotNull(candidates);
		assertEquals(2, candidates.size());

		log.debug(">>>> Lazily loading document and keywords for each candidate");
		for (Candidate candidate : candidates)
			assertNotNull(candidate.getDocument());
	}

	@Test
	public void loadById()
	{
		log.info(">>>> Retrieving one candidate with findById(): no document, no keywords");

		Candidate john = candidateDao.findById(johnId);
		assertNotNull(john);
		assertEquals("John", john.getFirstName());
		assertEquals(ContentType.MS_WORD, john.getDocumentType());

		log.debug(">>>> Lazily loading document and keywords for the candidate");

		assertNotNull(john.getDocument());
		assertTrue(john.getDocument().length > 1000);

		// Map<String, Keyword> johnKeys = john.getKeywords();
		// assertNotNull(johnKeys);
		// assertNotNull(johnKeys.get("java"));
		// assertNotNull(johnKeys.get("web"));
	}

	@Test
	public void findByCriteriaBareCandidate()
	{
		log.info(">>>> Retrieving one candidate with findByCriteria(): no document, no keywords");

		DetachedCriteria dc = null;
		List<Candidate> candidates = null;

		dc = DetachedCriteria.forClass(Candidate.class);
		dc.add(Restrictions.eq("firstName", "John"));
		candidates = candidateDao.findByCriteria(dc);
		assertNotNull(candidates);
		assertEquals(1, candidates.size());

		log.debug(">>>> Lazily loading document and keywords for the candidate");

		Candidate john = candidates.get(0);

		assertNotNull(john.getDocument());
		assertTrue(john.getDocument().length > 1000);

		// Map<String, Keyword> johnKeys = john.getKeywords();
		// assertNotNull(johnKeys);
		// assertNotNull(johnKeys.get("java"));
	}

	@Test
	public void findByCriteriaCandidateAndDocument()
	{
		log.info(">>>> Retrieving one candidate with findByCriteria(): EAGER document, no keywords");

		DetachedCriteria dc = null;
		List<Candidate> candidates = null;

		dc = DetachedCriteria.forClass(Candidate.class);
		dc.add(Restrictions.eq("firstName", "John"));
		dc.setFetchMode("resume", FetchMode.JOIN);
		candidates = candidateDao.findByCriteria(dc);
		assertNotNull(candidates);
		assertEquals(1, candidates.size());

		Candidate john = candidates.get(0);

		assertNotNull(john.getDocument());
		assertTrue(john.getDocument().length > 1000);

		log.debug(">>>> Lazily loading keywords for the candidate");

		// Map<String, Keyword> johnKeys = john.getKeywords();
		// assertNotNull(johnKeys);
		// assertNotNull(johnKeys.get("java"));
	}

	@Test
	public void findByCriteriaCandidateAndKeywords()
	{
		log.info(">>>> Retrieving one candidate with findByCriteria(): EAGER keywords, no document");

		DetachedCriteria dc = null;
		List<Candidate> candidates = null;

		dc = DetachedCriteria.forClass(Candidate.class);
		dc.add(Restrictions.eq("firstName", "John"));
		dc.setFetchMode("keywords", FetchMode.JOIN);
		dc.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		candidates = candidateDao.findByCriteria(dc);
		assertNotNull(candidates);
		assertEquals(1, candidates.size());

		Candidate john = candidates.get(0);

		// Map<String, Keyword> johnKeys = john.getKeywords();
		// assertNotNull(johnKeys);
		// assertNotNull(johnKeys.get("java"));

		log.debug(">>>> Lazily loading document for the candidate");

		assertNotNull(john.getDocument());
		assertTrue(john.getDocument().length > 1000);
	}

	@Test
	public void findByCriteriaCandidateAndKeywordsAndDocument()
	{
		log.info(">>>> Retrieving one candidate with findByCriteria(): EAGER keywords, EAGER document");

		DetachedCriteria dc = null;
		List<Candidate> candidates = null;

		dc = DetachedCriteria.forClass(Candidate.class);
		dc.add(Restrictions.eq("firstName", "John"));
		dc.setFetchMode("keywords", FetchMode.JOIN);
		dc.setFetchMode("resume", FetchMode.JOIN);
		dc.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		candidates = candidateDao.findByCriteria(dc);
		assertNotNull(candidates);
		assertEquals(1, candidates.size());

		Candidate john = candidates.get(0);

		// Map<String, Keyword> johnKeys = john.getKeywords();
		// assertNotNull(johnKeys);
		// assertNotNull(johnKeys.get("java"));

		assertNotNull(john.getDocument());
		assertTrue(john.getDocument().length > 1000);
	}

	@Test
	public void findByQueryCandidateOnly()
	{
		log.info(">>>> Retrieving one candidate with HQL: no keywords, no document");

		List<Candidate> candidates = null;

		String hql = "select distinct c from Candidate c where firstName = :firstName";
		candidates = candidateDao.findByQuery(hql, "firstName", "John");
		assertNotNull(candidates);
		assertEquals(1, candidates.size());

		Candidate john = candidates.get(0);

		log.debug(">>>> Lazily loading keywords for the candidate");

		// Map<String, Keyword> johnKeys = john.getKeywords();
		// assertNotNull(johnKeys);
		// assertNotNull(johnKeys.get("java"));

		log.debug(">>>> Lazily loading document for the candidate");

		assertNotNull(john.getDocument());
		assertTrue(john.getDocument().length > 1000);
	}

	@Test
	public void findByQueryCandidateAndKeywords()
	{
		log.info(">>>> Retrieving one candidate with HQL: EAGER keywords, no document");

		List<Candidate> candidates = null;

		String hql = "select distinct c from Candidate c left join fetch c.keywords where c.firstName = :firstName";
		candidates = candidateDao.findByQuery(hql, "firstName", "John");
		assertNotNull(candidates);
		assertEquals(1, candidates.size());

		Candidate john = candidates.get(0);

		// Map<String, Keyword> johnKeys = john.getKeywords();
		// assertNotNull(johnKeys);
		// assertNotNull(johnKeys.get("java"));

		log.debug(">>>> Lazily loading document for the candidate");

		assertNotNull(john.getDocument());
		assertTrue(john.getDocument().length > 1000);
	}

	@Test
	public void findByQueryCandidateAndDocument()
	{
		log.info(">>>> Retrieving one candidate with HQL: EAGER document, no keywords");

		List<Candidate> candidates = null;

		String hql = "select distinct c from Candidate c left join fetch c.resume where c.firstName = :firstName";
		candidates = candidateDao.findByQuery(hql, "firstName", "John");
		assertNotNull(candidates);
		assertEquals(1, candidates.size());

		Candidate john = candidates.get(0);

		assertNotNull(john.getDocument());
		assertTrue(john.getDocument().length > 1000);

		log.debug(">>>> Lazily loading keywords for the candidate");

		// Map<String, Keyword> johnKeys = john.getKeywords();
		// assertNotNull(johnKeys);
		// assertNotNull(johnKeys.get("java"));
	}

	@Test
	public void findByQueryCandidateAndDocumentAndKeywords()
	{
		log.info(">>>> Retrieving one candidate with HQL: EAGER document, EAGER keywords");

		List<Candidate> candidates = null;

		String hql = "select distinct c from Candidate c"
		    + " left join fetch c.resume left join fetch c.keywords where c.firstName = :firstName";
		candidates = candidateDao.findByQuery(hql, "firstName", "John");
		assertNotNull(candidates);
		assertEquals(1, candidates.size());

		Candidate john = candidates.get(0);

		assertNotNull(john.getDocument());
		assertTrue(john.getDocument().length > 1000);

		// Map<String, Keyword> johnKeys = john.getKeywords();
		// assertNotNull(johnKeys);
		// assertNotNull(johnKeys.get("java"));
	}

	/**
	 * @param candidate
	 * @param resource
	 * @throws IOException
	 */
	protected void loadMsWordFile(Candidate candidate, String resource)
	    throws IOException
	{
		InputStream is = getClass().getResourceAsStream(resource);
		byte[] document = IOUtils.toByteArray(is);
		candidate.setDocument(document, ContentType.MS_WORD);
	}

	/**
	 * @param candidate
	 * @param resource
	 * @throws IOException
	 */
	protected void loadTextFile(Candidate candidate, String resource)
	    throws IOException
	{
		InputStream is = getClass().getResourceAsStream(resource);
		byte[] document = IOUtils.toByteArray(is);
		candidate.setDocument(document, ContentType.PLAIN);
	}
}