package com.uralian.cgiats.service.impl;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.Map.Entry;

import javax.sql.DataSource;

import org.apache.commons.io.IOUtils;
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

import com.uralian.cgiats.model.*;
import com.uralian.cgiats.service.CandidateService;
import com.uralian.cgiats.service.ServiceException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/spring/test-context.xml")
@Transactional
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = false)
public class CandidateServiceImplTest
{
	protected final Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	private CandidateService candidateService;

	@Autowired
	private DataSource dataSource;

	private static int johnId;

	@Test
	public void initDB()
	{
		JdbcTemplate jt = new JdbcTemplate(dataSource);
		jt.execute("select count(*) from dual");
		jt.update("delete from keyword");
		jt.update("delete from property");
		jt.update("delete from candidate");
		jt.update("delete from resume");
	}

	@Test
	public void saveMsWord() throws IOException
	{
		log.info(">>>> Saving candidate 1");

		Candidate candidate = new Candidate();
		candidate.setEmail("john@doe.com");
		candidate.setFirstName("John");
		candidate.setLastName("Doe");
		candidate.setPhone("505-123-4444");
		candidate.getAddress().setState("CO");
		candidate.getAddress().setCity("Atlanta");
		candidate.getAddress().setZipcode("80525");
		candidate.setTitle("Developer");
		loadMsWordFile(candidate, "/docs/ResumeTest.doc");
		candidate.parseDocument();

		HashMap<String, String> properties = new HashMap<String, String>();
		properties.put("available", "6 months");
		candidate.addProperties(properties);

		try
		{
			candidateService.saveCandidate(candidate);
			assertNotNull(candidate.getId());
			johnId = candidate.getId();
		}
		catch (ServiceException e)
		{
			fail("Save failed: " + e.getMessage());
		}
	}

	@Test
	public void saveText() throws IOException
	{
		log.info(">>>> Saving candidate 2");

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

		HashMap<String, String> properties = new HashMap<String, String>();
		properties.put("Expertise", "Java Sr");
		candidate.addProperties(properties);

		try
		{
			candidateService.saveCandidate(candidate);
			assertNotNull(candidate.getId());
		}
		catch (ServiceException e)
		{
			fail("Save failed: " + e.getMessage());
		}
	}

	@Test
	public void getBareCandidate()
	{
		log.info(">>>> Retrieving candidate w/o keywords and document");

		Candidate john = candidateService.getCandidate(johnId, false, false);
		assertNotNull(john);
	}

	@Test
	public void getCandidateWithKeywords()
	{
		log.info(">>>> Retrieving candidate with keywords");

		Candidate john = candidateService.getCandidate(johnId, false, true);
		assertNotNull(john);
	}

	@Test
	public void getCandidateWithDocument()
	{
		log.info(">>>> Retrieving candidate with document");

		Candidate john = candidateService.getCandidate(johnId, true, false);
		assertNotNull(john);

		assertNotNull(john.getDocument());
	}

	@Test
	public void getCandidateWithProperties()
	{
		log.info(">>>> Retrieving candidate with document");

		Candidate john = candidateService.getCandidate(johnId, false, true);
		assertNotNull(john);

		Entry<String, String> key = john.getProperties().entrySet().iterator()
		    .next();
		assertTrue(key.getKey().equals("available"));
		assertTrue(key.getValue().equals("6 months"));
	}

	@Test
	public void findByName()
	{
		CandidateSearchDto criteria = null;
		List<Candidate> candidates = null;

		log.info(">>>> Searching candidates by name");

		criteria = new CandidateSearchDto();
		criteria.setName("jo");
		candidates = (List<Candidate>) candidateService.findCandidates(criteria);
		assertNotNull(candidates);
		assertEquals(1, candidates.size());

		criteria.setName("DOE");
		candidates = (List<Candidate>) candidateService.findCandidates(criteria);
		assertNotNull(candidates);
		assertEquals(2, candidates.size());

		criteria.setName("x");
		candidates = (List<Candidate>) candidateService.findCandidates(criteria);
		assertNotNull(candidates);
		assertEquals(0, candidates.size());
	}

	@Test
	public void findByDate()
	{
		CandidateSearchDto criteria = null;
		List<Candidate> candidates = null;

		log.info(">>>> Searching candidates by date");

		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -1);
		Date yesterday = cal.getTime();
		cal.setTime(new Date());
		cal.add(Calendar.DATE, 1);
		Date tomorrow = cal.getTime();

		criteria = new CandidateSearchDto();
		criteria.setEndEntryDate(yesterday);
		candidates = (List<Candidate>) candidateService.findCandidates(criteria);
		assertNotNull(candidates);
		assertEquals(0, candidates.size());

		criteria.setStartEntryDate(tomorrow);
		candidates = (List<Candidate>) candidateService.findCandidates(criteria);
		assertNotNull(candidates);
		assertEquals(0, candidates.size());

		criteria.setStartEntryDate(yesterday);
		criteria.setEndEntryDate(tomorrow);
		candidates = (List<Candidate>) candidateService.findCandidates(criteria);
		assertNotNull(candidates);
		assertEquals(2, candidates.size());
	}

	@Test
	public void findByKeywords()
	{
		CandidateSearchDto criteria = new CandidateSearchDto();
		List<Candidate> candidates = null;

		log.info(">>>> Searching candidates by keywords");

		// criteria.setKeywords(Arrays.asList("web"));
		candidates = (List<Candidate>) candidateService.findCandidates(criteria);
		assertNotNull(candidates);
		assertEquals(2, candidates.size());

		// Match ANY
		// criteria.setKeywords(Arrays.asList("advisor", "sun"));
		// criteria.setKeywordsQueryType(KeywordsQueryType.MATCH_ANY);
		candidates = (List<Candidate>) candidateService.findCandidates(criteria);
		assertNotNull(candidates);
		assertEquals(2, candidates.size());

		// Match ALL negative case
		// criteria.setKeywords(Arrays.asList("advisor", "sun", "NOTHERE"));
		// criteria.setKeywordsQueryType(KeywordsQueryType.MATCH_ALL);
		candidates = (List<Candidate>) candidateService.findCandidates(criteria);
		assertEquals(0, candidates.size());

		// Case insensitive
		// criteria.setKeywords(Arrays.asList("JaNe", "DOE"));
		// criteria.setKeywordsQueryType(KeywordsQueryType.MATCH_ALL);
		candidates = (List<Candidate>) candidateService.findCandidates(criteria);
		assertEquals(1, candidates.size());

		// criteria.setKeywords(Arrays.asList("advisor"));
		candidates = (List<Candidate>) candidateService.findCandidates(criteria);
		assertNotNull(candidates);
		assertEquals(1, candidates.size());
	}

	@Test
	public void findByProperties()
	{
		CandidateSearchDto criteria = new CandidateSearchDto();
		List<Candidate> candidates = null;

		log.info(">>>> Searching candidates by properties");

		HashMap<String, String> properties = new HashMap<String, String>();
		properties.put("Expertise", "Java Sr");
		criteria.setProperties(properties);
		candidates = (List<Candidate>) candidateService.findCandidates(criteria);
		assertNotNull(candidates);
		assertEquals(1, candidates.size());

		properties.put("available", "6 months");
		candidates = (List<Candidate>) candidateService.findCandidates(criteria);
		assertNotNull(candidates);
		assertEquals(0, candidates.size());

		// Match ANY
		properties = new HashMap<String, String>();
		properties.put("Expertise", "Java Sr");
		criteria.setProperties(properties);
		// criteria.setKeywords(Arrays.asList("advisor", "sun"));
		// criteria.setKeywordsQueryType(KeywordsQueryType.MATCH_ANY);
		candidates = (List<Candidate>) candidateService.findCandidates(criteria);
		assertNotNull(candidates);
		assertEquals(1, candidates.size());

		// Match ALL
		// criteria.setKeywords(Arrays.asList("advisor", "sun", "NONTHERE"));
		// criteria.setKeywordsQueryType(KeywordsQueryType.MATCH_ALL);
		candidates = (List<Candidate>) candidateService.findCandidates(criteria);
		assertEquals(0, candidates.size());
	}

	@Test
	public void findOrderBy()
	{
		CandidateSearchDto criteria = new CandidateSearchDto();
		List<Candidate> candidates = null;

		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -1);
		Date yesterday = cal.getTime();

		// Jane has less "allowing" hits and we are ordering ASC,
		// so Jane has to come first.
		// criteria.setKeywords(Arrays.asList("allowing"));
		criteria.setStartEntryDate(yesterday);
		criteria.setOrderByColumn(OrderByColumn.RELEVANCE);
		criteria.setOrderByType(OrderByType.ASC);
		candidates = (List<Candidate>) candidateService.findCandidates(criteria);
		assertNotNull(candidates);
		assertEquals("Jane", candidates.get(0).getFirstName());

		// Now order by Name, John should come first
		criteria.setOrderByColumn(OrderByColumn.FIRSTNAME);
		criteria.setOrderByType(OrderByType.DESC);
		candidates = (List<Candidate>) candidateService.findCandidates(criteria);
		assertNotNull(candidates);
		assertEquals("John", candidates.get(0).getFirstName());
	}

	@Test
	public void findPagination()
	{
		CandidateSearchDto criteria = new CandidateSearchDto();
		List<Candidate> candidates = null;

		// Default bring everything
		// criteria.setKeywords(Arrays.asList("allowing"));
		candidates = (List<Candidate>) candidateService.findCandidates(criteria);
		assertNotNull(candidates);
		assertEquals(2, candidates.size());

		// Now bring just one result using same query
		// criteria.setKeywords(Arrays.asList("allowing"));
		criteria.setMaxResults(1);
		candidates = (List<Candidate>) candidateService.findCandidates(criteria);
		assertNotNull(candidates);
		assertEquals(1, candidates.size());

		// Now order by Name, John should come first
		// So we are going to fetch Jane.
		criteria.setOrderByColumn(OrderByColumn.FIRSTNAME);
		criteria.setOrderByType(OrderByType.DESC);
		criteria.setStartPosition(1);
		criteria.setMaxResults(-1);
		candidates = (List<Candidate>) candidateService.findCandidates(criteria);
		assertNotNull(candidates);
		assertEquals("Jane", candidates.get(0).getFirstName());
	}

	@Test
	public void updateCandidate()
	{
		log.info(">>>> Updating candidate fields and resume");

		Candidate john = candidateService.getCandidate(johnId, false, false);
		john.getAddress().setZipcode(null);
		john.getAddress().setState("TX");
		john.setFirstName("John Daniel");
		john.setTitle("Actor");
		john.setDocument("Here we go".getBytes(), ContentType.PLAIN);

		try
		{
			candidateService.updateCandidate(john);
		}
		catch (ServiceException e)
		{
			fail("Update failed: " + e.getMessage());
		}
	}

	@Test
	public void deleteCandidate()
	{
		log.info(">>>> Deleting all candidates");

		try
		{
			CandidateSearchDto dto = new CandidateSearchDto();
			// dto.setKeywords(Arrays.asList("java"));

			List<Candidate> candidates = (List<Candidate>) candidateService.findCandidates(dto);
			for (Candidate candidate : candidates)
				candidateService.deleteCandidate(candidate.getId());
		}
		catch (ServiceException e)
		{
			fail("Delete failed: " + e.getMessage());
		}
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