/*
 * ImportFiles.java May 16, 2012
 *
 * Copyright 2012 Uralian, LLC. All rights reserved.
 */
package com.uralian.cgiats.util;

import static org.apache.log4j.Logger.getLogger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.Date;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Level;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.uralian.cgiats.dao.impl.CandidateDaoImpl;
import com.uralian.cgiats.model.Candidate;
import com.uralian.cgiats.model.ContentType;
import com.uralian.cgiats.service.CandidateService;
import com.uralian.cgiats.service.impl.CandidateServiceImpl;
import com.uralian.cgiats.web.UIBean;

/**
 * Run it with:
 * 
 * <pre>
 * mvn exec:java -Dexec.mainClass="com.uralian.cgiats.util.ImportFiles" -Dexec.args="importDir limit" -Dexec.classpathScope=test
 * </pre>
 * 
 * @author Vlad Orzhekhovskiy
 */
public class ImportFiles extends UIBean implements Runnable
{
	private String directory;
	private String createdBy;
	private Integer limit;
	private String diceEmail;
	private String atsUserId;
	@Autowired
	private CandidateService candidateService;

	/**
   */
	public ImportFiles()
	{
		disableLogging();
	}

	/**
	 * @return the directory.
	 */
	public String getDirectory()
	{
		return directory;
	}

	/**
	 * @param directory the directory to set.
	 */
	public void setDirectory(String directory)
	{
		this.directory = directory;
	}

	/**
	 * @return the limit.
	 */
	public Integer getLimit()
	{
		return limit;
	}

	/**
	 * @param limit the limit to set.
	 */
	public void setLimit(Integer limit)
	{
		this.limit = limit;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run()
	{
		importData();
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
	 */
	public void importData()
	{
		File folder = new File(directory);
		if (!folder.exists())
		{
			System.err.println("\nFolder " + directory + " does not exist");
			return;
		}

		File[] files = folder.listFiles(new FilenameFilter()
		{
			@Override
			public boolean accept(File dir, String name)
			{
				return ContentType.resolveByFileName(name) != null;
			}
		});
		System.out.println("\n" + files.length + " files(s) found in folder "
		    + directory);

		int resumeCount = 0;
		NumberFormat fmt = NumberFormat.getIntegerInstance();
		
	//	Principal principal = getFacesContext().getExternalContext().getUserPrincipal();		
	//	String userId = principal.getName();
	//	log.info("userId::::::"+userId);
		//if(userId == null){
		//	userId = "test";
		//}
		
		for (File file : files)
		{
			try
			{
				Candidate candidate = new Candidate();
				candidate.setLastName("Unknown");
				candidate.addProperty("srcHost", "CGI");
				candidate.addProperty("srcFile", file.getName());

				byte[] data = loadFile(file);
				ContentType contentType = ContentType.resolveByFileName(file.getName());
				candidate.setDocument(data, contentType);
				candidate.parseDocument();
				if(createdBy!=null && createdBy.trim().length()>0){
					candidate.setCreatedUser(createdBy);	
				}else{
				candidate.setCreatedUser("Dice");
				}
				candidate.setDeleteFlag(0);
				candidate.setCreatedOn(new Date());
				candidate.setPortalEmail(diceEmail);
				candidate.setPortalViewedBy(atsUserId);
				candidateService.saveCandidate(candidate);
				resumeCount++;
			}
			catch (Exception e)
			{
				e.printStackTrace();
				System.err.println("Error occurred while loading file " + file);
			}

			if (resumeCount % 500 == 0)
				System.out.println(fmt.format(resumeCount) + " candidates saved");

			if (limit != null && resumeCount >= limit)
				break;
		}

		System.out.println("\nProcessing complete: " + fmt.format(resumeCount)
		    + " records created.");
	}

	/**
	 * @param file
	 * @return
	 * @throws IOException
	 */
	private static byte[] loadFile(File file) throws IOException
	{
		FileInputStream fis = new FileInputStream(file);
		byte[] document = IOUtils.toByteArray(fis);
		fis.close();

		return document;
	}

	/**
	 * Program entry point.
	 * 
	 * @param args
	 */
	public static void main(String[] args)
	{
		try{
		if (args.length < 1)
		{
			System.err
			    .println("Directory argument missing. Usage: ImportFiles import-dir [limit]");
			return;
		}

		ImportFiles imp = new ImportFiles();

		ConfigurableApplicationContext context = new ClassPathXmlApplicationContext(
		    "/spring/util-context.xml");
		context.registerShutdownHook();
		context.getAutowireCapableBeanFactory().autowireBean(imp);

		System.out.println("Processing started");
		imp.setDirectory(args[0]);
		if (args.length > 1)
		{
			System.out.println("args[1]>>"+args[1]);
			imp.setDiceEmail(args[1]);
		}
		if (args.length > 2)
		{
			System.out.println("args[2]>>"+args[2]);
			imp.setAtsUserId(args[2]);
		}
		if (args.length > 3)
		{
			System.out.println("args[3]>>"+args[3]);
			int limit = Integer.parseInt(args[3]);
			imp.setLimit(limit);
		}
		if (args.length > 4)
		{
			System.out.println("args[4]>>"+args[4]);
			imp.setCreatedBy(args[4]);
		}
		imp.run();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public String getDiceEmail() {
		return diceEmail;
	}

	public String getAtsUserId() {
		return atsUserId;
	}

	public void setDiceEmail(String diceEmail) {
		this.diceEmail = diceEmail;
	}

	public void setAtsUserId(String atsUserId) {
		this.atsUserId = atsUserId;
	}

	/**
	 * @return the createdBy
	 */
	public String getCreatedBy() {
		return createdBy;
	}

	/**
	 * @param createdBy the createdBy to set
	 */
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
}