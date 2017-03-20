/*
 * PasswordTest.java Jun 6, 2012
 *
 * Copyright 2012 Uralian, LLC. All rights reserved.
 */
package com.uralian.cgiats.model;

import org.junit.Test;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;

/**
 * @author Vlad Orzhekhovskiy
 */
public class PasswordTest
{
	@Test
	public void testPassword()
	{
		String raw = "12345";
		
		Md5PasswordEncoder encoder = new Md5PasswordEncoder();
		String encoded = encoder.encodePassword(raw, "CGI");
		
		System.out.println(encoded);
	}
}