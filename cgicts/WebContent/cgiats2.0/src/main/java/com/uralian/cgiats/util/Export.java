/*
 * Export.java Sep 18, 2012
 *
 * Copyright 2012 Uralian, LLC. All rights reserved.
 */
package com.uralian.cgiats.util;

import static org.apache.log4j.Logger.getLogger;

import java.io.*;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.uralian.cgiats.dao.impl.CandidateDaoImpl;
import com.uralian.cgiats.model.Candidate;
import com.uralian.cgiats.model.CandidateSearchDto;
import com.uralian.cgiats.model.ContentType;
import com.uralian.cgiats.service.CandidateService;
import com.uralian.cgiats.service.impl.CandidateServiceImpl;

/**
 * @author Vlad Orzhekhovskiy
 */
public class Export implements Runnable
{
	@Autowired
	private CandidateService candidateService;

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run()
	{
		disableLogging();

		CandidateSearchDto criteria = new CandidateSearchDto();
		criteria.setName("DICE");
		List<Candidate> candidates = (List<Candidate>) candidateService.findCandidates(criteria);

		try
		{
			File file = File.createTempFile("dice", ".csv");
			PrintWriter out = new PrintWriter(file);
			for (Candidate candidate : candidates)
			{
				if (candidate.getDocumentType() != ContentType.MS_WORD)
					continue;

				int id = candidate.getId();

				candidate = candidateService.getCandidate(id, true, false);
				byte[] doc = candidate.getDocument();
				String preview = renderPreview(doc, 5);
				String text = preview.replace("\n", " ").replace(',', ' ')
				    .replace("\r", " ");

				out.println(id + ", \"" + text.trim() + "\"");
			}
			out.close();
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * @param doc
	 * @param lines
	 * @return
	 */
	private String renderPreview(byte[] doc, int lines)
	{
		try
		{
			ByteArrayInputStream bais = new ByteArrayInputStream(doc);
			WordExtractor we = new WordExtractor(bais);
			String[] paragraphs = we.getParagraphText();

			int count = 0;
			StringBuffer sb = new StringBuffer();
			sb.append(we.getHeaderText());
			for (String paragraph : paragraphs)
			{
				if (count >= lines && lines > 0)
					break;
				sb.append(paragraph);
				sb.append("\n");
				count++;
			}

			return sb.toString();
		}
		catch (IOException e)
		{
			return "";
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
	 * Program entry point.
	 * 
	 * @param args
	 */
	public static void main(String[] args)
	{
		Export exp = new Export();

		ConfigurableApplicationContext context = new ClassPathXmlApplicationContext(
		    "/spring/util-context.xml");
		context.registerShutdownHook();
		context.getAutowireCapableBeanFactory().autowireBean(exp);

		System.out.println("Processing started");
		exp.run();
	}
}