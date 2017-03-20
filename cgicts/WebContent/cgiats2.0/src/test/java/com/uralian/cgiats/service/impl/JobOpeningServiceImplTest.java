package com.uralian.cgiats.service.impl;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

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
import com.uralian.cgiats.service.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/spring/test-context.xml")
@Transactional
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = false)
@SuppressWarnings("unused")
public class JobOpeningServiceImplTest {
//	protected final Logger log = LoggerFactory.getLogger(getClass());
//
//	@Autowired
//	private JobOrderService jobOpeningSevice;
//
//	@Autowired
//	private CandidateService candidateService;
//
//	@Autowired
//	private UserService userService;
//
//	@Autowired
//	private DataSource dataSource;
//
//	private static int johnId;
//	private static int janeId;
//	private static String userId = "TestingUser";
//
//	@Test
//	public void initDB() {
//		JdbcTemplate jt = new JdbcTemplate(dataSource);
//		jt.execute("select count(*) from dual");
//		jt.update("delete from keyword");
//		jt.update("delete from property");
//		jt.update("delete from candidate");
//		jt.update("delete from resume");
//
//		jt.update("delete from JOB_ASSIGNED_TO");
//		jt.update("delete from JOB_CANDIDATE");
//		jt.update("delete from job_opening_field");
//		jt.update("delete from job_opening");
//		jt.update("delete from USER_ACCT where USER_ID = '" + userId + "'");
//	}
//
//	@Test
//	public void saveMsWord() throws IOException {
//		log.info(">>>> Saving candidate 1");
//
//		Candidate candidate = new Candidate();
//		candidate.setEmail("john@doe.com");
//		candidate.setFirstName("John");
//		candidate.setLastName("Doe");
//		candidate.setPhone("505-123-4444");
//		candidate.getAddress().setState("CO");
//		candidate.getAddress().setCity("Atlanta");
//		candidate.getAddress().setZipcode("80525");
//		candidate.setTitle("Developer");
//		loadMsWordFile(candidate, "/docs/ResumeTest.doc");
//		candidate.parseDocument();
//
//		HashMap<String, String> properties = new HashMap<String, String>();
//		properties.put("available", "6 months");
//		candidate.addProperties(properties);
//
//		try {
//			candidateService.saveCandidate(candidate);
//			assertNotNull(candidate.getId());
//			johnId = candidate.getId();
//		} catch (ServiceException e) {
//			fail("Save failed: " + e.getMessage());
//		}
//	}
//
//	@Test
//	public void saveText() throws IOException {
//		log.info(">>>> Saving candidate 2");
//
//		Candidate candidate = new Candidate();
//		candidate.setEmail("jane@doe.com");
//		candidate.setFirstName("Jane");
//		candidate.setLastName("Doe");
//		candidate.setPhone("505-404-2030");
//		candidate.getAddress().setState("GA");
//		candidate.getAddress().setZipcode("30309");
//		candidate.setTitle("Enterprise Architect");
//		loadTextFile(candidate, "/docs/ResumeTest.txt");
//		candidate.parseDocument();
//
//		HashMap<String, String> properties = new HashMap<String, String>();
//		properties.put("Expertise", "Java Sr");
//		candidate.addProperties(properties);
//
//		try {
//			candidateService.saveCandidate(candidate);
//			assertNotNull(candidate.getId());
//			janeId = candidate.getId();
//		} catch (ServiceException e) {
//			fail("Save failed: " + e.getMessage());
//		}
//	}
//
//	@Test
//	public void saveUser() throws IOException {
//		User user = new User(userId, UserRole.Recruiter);
//		user.setFirstName("FirstName");
//		user.setLastName("LastName");
//		user.setPassword("mypassword");
//		try {
//			userService.updateUser(user);
//		} catch (ServiceException e) {
//			fail("Save failed: " + e.getMessage());
//		}
//
//	}
//
//	@Test
//	public void saveJobOpening() throws IOException {
//		log.info(">>>> Saving Job Opening 1");
//		JobOrder opening = new JobOrder(new JobOrderDefaults());
//		opening.setPriority(JobOrderPriority.MEDIUM);
//		opening.setStatus(JobOrderStatus.OPEN);
//
//		User user = userService.loadUser(userId);
//		opening.setAssignedTo(user);
//
//		Candidate candidate = candidateService.getCandidate(johnId, false,
//				false);
//		// opening.addCandidate(candidate);
//		// opening.addField(JobOrderFieldName.COMPANY, "Uralian", true);
//		// opening.setDescription("This is a Job Opening");
//
//		try {
//			jobOpeningSevice.saveJobOrder(opening);
//		} catch (ServiceException e) {
//			fail("Save failed: " + e.getMessage());
//		}
//
//	}
//
//	@Test
//	public void findJobOpenings() {
//		JobOrderSearchDto criteria = new JobOrderSearchDto();
//
//		// Find by priority
//		criteria.setPriorities(Arrays.asList(JobOrderPriority.MEDIUM));
//		List<JobOrder> openings = jobOpeningSevice
//				.findJobOrders(criteria, null);
//		assertNotNull(openings);
//		assertTrue(openings.size() > 0);
//
//		// Find by status
//		criteria = new JobOrderSearchDto();
//		criteria.setStatuses(Arrays.asList(JobOrderStatus.OPEN));
//		openings = jobOpeningSevice.findJobOrders(criteria, null);
//		assertNotNull(openings);
//		assertTrue(openings.size() > 0);
//
//		// Find by assigned to
//		criteria = new JobOrderSearchDto();
//		criteria.setAssignedTo(userId);
//		openings = jobOpeningSevice.findJobOrders(criteria, null);
//		assertNotNull(openings);
//		assertTrue(openings.size() > 0);
//
//		// Find by fields
//		criteria = new JobOrderSearchDto();
//		Map<String, String> map = new HashMap<String, String>();
//		// map.put(JobOrderFieldName.COMPANY, "Uralian");
//		criteria.setFields(map);
//		openings = jobOpeningSevice.findJobOrders(criteria, null);
//		assertNotNull(openings);
//		assertTrue(openings.size() > 0);
//	}
//
//	/**
//	 * @param candidate
//	 * @param resource
//	 * @throws IOException
//	 */
//	protected void loadMsWordFile(Candidate candidate, String resource)
//			throws IOException {
//		InputStream is = getClass().getResourceAsStream(resource);
//		byte[] document = IOUtils.toByteArray(is);
//		candidate.setDocument(document, ContentType.MS_WORD);
//	}
//
//	/**
//	 * @param candidate
//	 * @param resource
//	 * @throws IOException
//	 */
//	protected void loadTextFile(Candidate candidate, String resource)
//			throws IOException {
//		InputStream is = getClass().getResourceAsStream(resource);
//		byte[] document = IOUtils.toByteArray(is);
//		candidate.setDocument(document, ContentType.PLAIN);
//	}
}
