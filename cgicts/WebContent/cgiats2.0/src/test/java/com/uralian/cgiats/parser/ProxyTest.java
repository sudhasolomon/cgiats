/*
 * ProxyTest.java Sep 21, 2012
 *
 * Copyright 2012 Uralian, LLC. All rights reserved.
 */
package com.uralian.cgiats.parser;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import com.uralian.cgiats.util.Utils;

/**
 * @author Vlad Orzhekhovskiy
 */
public class ProxyTest
{
	/**
	 * @throws IOException
	 */
	@Test
	public void test001() throws IOException
	{
		InputStream is = getClass().getResourceAsStream(
		    "/proxy/dice_search_results.html");
		String html = IOUtils.toString(is);

		StringBuffer sb = new StringBuffer();

		final String alreadyInDb = "<span style='background-color:red; color: white; font-weight: bold'> ALREADY DOWNLOADED </span>";

		Pattern p = Pattern.compile("<a class=\"positionTitle\"[^>]*>");
		Matcher m = p.matcher(html);
		while (m.find())
		{
			String match = m.group();
			String dockey = Utils.findSubstring(match, "dockey=", "&", 0);
			String id = getResumeId(dockey);
			System.out.println(id);
			String replacement = alreadyInDb + m.group();
			m.appendReplacement(sb, replacement);
		}
		m.appendTail(sb);

		File file = File.createTempFile("decorated", ".html");
		FileWriter out = new FileWriter(file);
		IOUtils.write(sb.toString(), out);
		out.close();
	}

	/**
	 * @param dockey
	 * @return
	 */
	private static String getResumeId(String dockey)
	{
		int index1 = dockey.lastIndexOf('/');
		int index2 = dockey.lastIndexOf('@');
		if (index1 < 0 || index2 < 0)
			return null;

		return dockey.substring(index1 + 1, index2);
	}
}