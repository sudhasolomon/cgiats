/*
 * GenerateSampleData.java May 16, 2012
 *
 * Copyright 2012 Uralian, LLC. All rights reserved.
 */
package com.uralian.cgiats.util;

import static org.apache.log4j.Logger.getLogger;

import java.io.IOException;
import java.io.InputStream;
import java.security.Principal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.faces.context.FacesContext;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Level;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.uralian.cgiats.dao.impl.CandidateDaoImpl;
import com.uralian.cgiats.model.Candidate;
import com.uralian.cgiats.model.ContentType;
import com.uralian.cgiats.service.CandidateService;
import com.uralian.cgiats.service.ServiceException;
import com.uralian.cgiats.service.impl.CandidateServiceImpl;
import com.uralian.cgiats.web.UIBean;

/**
 * Run it with:
 * 
 * <pre>
 * mvn exec:java -Dexec.mainClass="com.uralian.cgiats.util.GenerateSampleData" -Dexec:args="resumeCount" -Dexec.classpathScope=test
 * </pre>
 * 
 * @author Vlad Orzhekhovskiy
 */
public class GenerateSampleData extends UIBean implements Runnable
{
	private static final String[] firstNames = { "John", "Mike", "Joe", "Alex",
	    "Jack", "Matt", "Max", "Bob", "Todd", "Bill", "Greg", "Alice", "Sandra",
	    "Barbara", "Jane", "Robert", "Andrew", "Mark", "Ryan", "Jamie", "Eve",
	    "Clare", "Cody", "Nelson", "Allie", "Hugh", "Loraine", "Mathew",
	    "Darryl", "Neil", "Katy", "Clayton", "Ted", "Christian", "Todd", "Erik",
	    "Chandra", "Kurt", "Tyrone", "Zelma", "Lance", "Darren", null };
	private static final String[] lastNames = { "Smith", "White", "Black",
	    "Brown", "Green", "Doe", "Johnson", "Hughes", "Fox", "Rabbit", "Wolf",
	    "Fisher", "Carpenter", "Dalton", "Busy", "Bear", "Eagle", "Sparrow",
	    "Dunworth", "Bonnet", "Filmore", "Dieter", "Merck", "Felty", "Harbert",
	    "Parcell", "Lerman", "Dunnam", "Cossey", "Axton", "Zarella", "Brandow",
	    "Leavy", "Markin", "Jeppson", "Rosinski", "Galindez", null };
	private static final String[] cities = { "Roswell", "Sandy Springs",
	    "Fairview", "Doraville", "Decatur", "Dunwoody", "Austell", "Buffalo",
	    "Mobile", "Sacramento", "Chicago", "Reno", "Nashville", "Indianapolis",
	    "Omaha", "Ocala", "Naples", "Charleston", "Jacksonville", "Columbus",
	    "New Orleans", "Bangor", "Springfield", "Fort Walton", "Monroe",
	    "Wausau", null };
	private static final String[] states = { "AL", "NC", "GA", "NJ", "NY", "FL",
	    "CO", "WY", "CA", "KY", "TX", "MO", "IL", "OH", "SC", "MT", "NE", "OR",
	    "CA", "TN", "PA", "CT", null };
	private static final String[] streets = { "Main St", "Peachtree Rd",
	    "1st St", "Park Ave", "Windy Hill Rd", "Coleman Rd", "Sunset Blvd",
	    "Druid Hills", "Campbell Rd", "Anchor Dr", "Beacon Av", "Bluff St",
	    "Butterfly Crest Rd", "Cider Rd", "Cloud Blvd", "Emerald Creek Pkwy",
	    "Honey Hills Rd", "Island St", "Sample St", "Pine Rd", null };
	private static final String[] titles = { "Developer", "Analyst",
	    "Sr Developer", "Software Engineer", "Architect", "Enterprise Architect",
	    "Business Analyst", "Project Manager", "Team Lead", "Data Analyst",
	    "Network Security Administrator", "Data Modeler", "QA Manager", null };

	protected final Random rnd = new Random();

	private int resumeCount;

	@Autowired
	private CandidateService candidateService;

	/**
	 * @return the resumeCount.
	 */
	public int getResumeCount()
	{
		return resumeCount;
	}

	/**
	 * @param resumeCount the resumeCount to set.
	 */
	public void setResumeCount(int resumeCount)
	{
		this.resumeCount = resumeCount;
	}
	
	/**
	 * @return
	 */
	public static FacesContext getFacesContext()
	{
		return FacesContext.getCurrentInstance();
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
	    generateResumes();
    }
    catch (ServiceException e)
    {
	    e.printStackTrace();
    }
    catch (IOException e)
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
	 * @throws IOException
	 */
	public void generateResumes() throws ServiceException, IOException
	{
		NumberFormat fmt = NumberFormat.getIntegerInstance();
		System.out.println("Generating " + fmt.format(resumeCount) + " resumes");

		final int batchSize = 100;
		final int batchCount = resumeCount / batchSize;

		List<Candidate> candidates = new ArrayList<Candidate>();
		
		Principal principal = getFacesContext().getExternalContext().getUserPrincipal();
		if (principal == null)
			return;
		String userId = principal.getName();
		log.info("userId::::::"+userId);
		if(userId == null){
			userId = "test";
		}
		
		for (int j = 0; j < batchCount; j++)
		{
			candidates.clear();

			for (int i = 0; i < batchSize; i++)
			{
				Candidate candidate = new Candidate();
				candidate.setFirstName(firstNames[rnd.nextInt(firstNames.length)]);
				candidate.setLastName(lastNames[rnd.nextInt(lastNames.length)]);
				candidate.setEmail(candidate.getFirstName() + "@"
				    + candidate.getLastName() + ".com");

				if (rnd.nextFloat() < 0.8)
				{
					StringBuffer sb = new StringBuffer();
					sb.append("(");
					sb.append(rnd.nextInt(800) + 100);
					sb.append(") ");
					sb.append(rnd.nextInt(800) + 100);
					sb.append("-");
					sb.append(rnd.nextInt(8000) + 1000);
					candidate.setPhone(sb.toString());
				}

				candidate.getAddress().setStreet1(
				    "" + (rnd.nextInt(5000) + 1) + " "
				        + streets[rnd.nextInt(streets.length)]);
				candidate.getAddress().setCity(cities[rnd.nextInt(cities.length)]);
				candidate.getAddress().setState(states[rnd.nextInt(states.length)]);

				if (rnd.nextFloat() < 0.7)
					candidate.getAddress().setZipcode("" + (rnd.nextInt(80000) + 10000));

				candidate.setTitle(titles[rnd.nextInt(titles.length)]);

				int resIndex = rnd.nextInt(3) + 1;
				if (rnd.nextFloat() < 0.4)
					loadMsWordFile(candidate, "/docs/resume" + resIndex + ".doc");
				else if (rnd.nextFloat() < 0.8)
					loadTextFile(candidate, "/docs/resume" + resIndex + ".txt");
				candidate.parseDocument();

				candidate.addProperty("externalId",
				    Integer.toHexString(rnd.nextInt(50000)));
				switch (rnd.nextInt(10))
				{
				case 0:
					candidate.addProperty("education", "High School");
					break;
				case 1:
					candidate.addProperty("education", "Bachelor");
					break;
				case 2:
					candidate.addProperty("education", "Master");
					break;
				case 3:
					candidate.addProperty("education", "PhD");
					break;
				}
				candidate.setCreatedUser(userId);
				candidate.setDeleteFlag(0);
				candidates.add(candidate);				
			}

			candidateService.saveCandidates(candidates);
			System.out.println(fmt.format((j + 1) * batchSize) + " candidates saved");
		}
	}

	/**
	 * @param candidate
	 * @param resource
	 * @throws IOException
	 */
	protected static void loadMsWordFile(Candidate candidate, String resource)
	    throws IOException
	{
		InputStream is = GenerateSampleData.class.getResourceAsStream(resource);
		byte[] document = IOUtils.toByteArray(is);
		candidate.setDocument(document, ContentType.MS_WORD);
	}

	/**
	 * @param candidate
	 * @param resource
	 * @throws IOException
	 */
	protected static void loadTextFile(Candidate candidate, String resource)
	    throws IOException
	{
		InputStream is = GenerateSampleData.class.getResourceAsStream(resource);
		byte[] document = IOUtils.toByteArray(is);
		candidate.setDocument(document, ContentType.PLAIN);
	}

	/**
	 * Program entry point.
	 * 
	 * @param args
	 */
	public static void main(String[] args)
	{
		int resumeCount = 100;
		if (args.length > 0)
			resumeCount = Integer.parseInt(args[0]);

		GenerateSampleData gsd = new GenerateSampleData();

		ConfigurableApplicationContext context = new ClassPathXmlApplicationContext(
		    "/spring/util-context.xml");
		context.registerShutdownHook();
		context.getAutowireCapableBeanFactory().autowireBean(gsd);

		System.out.println("Processing started");
		gsd.setResumeCount(resumeCount);
		gsd.run();
	}
}