/*
 * JdbcTest.java Feb 15, 2012
 *
 * Copyright 2012 Uralian, LLC. All rights reserved.
 */
package com.uralian.cgiats.dao;

import static org.junit.Assert.assertNotNull;

import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author Vlad Orzhekhovskiy
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/spring/test-context.xml")
public class JdbcTest
{
	@Autowired
	private DataSource dataSource;

	@Test
	public void testConnection()
	{
		JdbcTemplate template = new JdbcTemplate(dataSource);
		List<Map<String, Object>> rows = template
		    .queryForList("select * from candidate");
		assertNotNull(rows);
	}
}
