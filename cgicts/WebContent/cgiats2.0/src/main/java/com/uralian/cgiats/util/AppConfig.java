/*
 * AppConfig.java Jun 27, 2012
 *
 * Copyright 2012 Uralian, LLC. All rights reserved.
 */
package com.uralian.cgiats.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.uralian.cgiats.proxy.CGIATSConstants;

/**
 * @author Vlad Orzhekhovskiy
 */
@XmlRootElement(name = "Configuration")
public class AppConfig
{
	private static AppConfig instance = null;

	private static final Logger log = LoggerFactory.getLogger(AppConfig.class);

	/*private String homeDir;

	private int attachmentMaxSize;
	private String attachmentTypes;
*/
	private String smtpHost;
	private int smtpPort;
	private String smtpUsername;
	private String smtpPassword;
	private String emailFrom;

	private int proxyPort;
	private boolean autostartProxy;
	private String cbuilderFilterUrls;
	private String monsterFilterUrls;
	
	private String techFetchUrls;

	private String timeZoneId;
	
	private List<String> list;
	
	
	 
	/*private String reportsUrl;

	private List<String> resumeCategories = new ArrayList<String>();
	private List<String> standardResumeProps = new ArrayList<String>();
	private Map<CandidateStatus, String> statusColors = new TreeMap<CandidateStatus, String>();*/

	private String joNotifyEmail;

	/**
	 * @return
	 */
	public static synchronized AppConfig getInstance()
	{
		if (instance == null)
			instance = init();
		return instance;
	}

	/**
	 */
	public AppConfig()
	{
		reset();
	}

	/**
	 * @return the homeDir.
	 *//*
	public String getHomeDir()
	{
		return homeDir;
	}

	*//**
	 * @param homeDir the homeDir to set.
	 *//*
	public void setHomeDir(String homeDir)
	{
		this.homeDir = homeDir;
	}

	*//**
	 * @return the attachmentMaxSize.
	 *//*
	public int getAttachmentMaxSize()
	{
		return attachmentMaxSize;
	}

	*//**
	 * @param attachmentMaxSize the attachmentMaxSize to set.
	 *//*
	public void setAttachmentMaxSize(int attachmentMaxSize)
	{
		this.attachmentMaxSize = attachmentMaxSize;
	}

	*//**
	 * @return the attachmentTypes.
	 *//*
	public String getAttachmentTypes()
	{
		return attachmentTypes;
	}

	*//**
	 * @param attachmentTypes the attachmentTypes to set.
	 *//*
	public void setAttachmentTypes(String attachmentTypes)
	{
		this.attachmentTypes = attachmentTypes;
	}*/

	/**
	 * @return the smtpHost.
	 */
	public String getSmtpHost()
	{
		return smtpHost;
	}

	/**
	 * @param smtpHost the smtpHost to set.
	 */
	public void setSmtpHost(String smtpHost)
	{
		this.smtpHost = smtpHost;
	}

	/**
	 * @return the smtpPort.
	 */
	public int getSmtpPort()
	{
		return smtpPort;
	}

	/**
	 * @param smtpPort the smtpPort to set.
	 */
	public void setSmtpPort(int smtpPort)
	{
		this.smtpPort = smtpPort;
	}

	/**
	 * @return the smtpUsername.
	 */
	public String getSmtpUsername()
	{
		return smtpUsername;
	}

	/**
	 * @param smtpUsername the smtpUsername to set.
	 */
	public void setSmtpUsername(String smtpUsername)
	{
		this.smtpUsername = smtpUsername;
	}

	/**
	 * @return the smtpPassword.
	 */
	public String getSmtpPassword()
	{
		return smtpPassword;
	}

	/**
	 * @param smtpPassword the smtpPassword to set.
	 */
	public void setSmtpPassword(String smtpPassword)
	{
		this.smtpPassword = smtpPassword;
	}

	/**
	 * @return the emailFrom.
	 */
	public String getEmailFrom()
	{
		return emailFrom;
	}

	/**
	 * @param emailFrom the emailFrom to set.
	 */
	public void setEmailFrom(String emailFrom)
	{
		this.emailFrom = emailFrom;
	}

	/**
	 * @return the proxyPort.
	 */
	public int getProxyPort()
	{
		return proxyPort;
	}

	/**
	 * @param proxyPort the proxyPort to set.
	 */
	public void setProxyPort(int proxyPort)
	{
		this.proxyPort = proxyPort;
	}

	/**
	 * @return the autostartProxy.
	 */
	public boolean isAutostartProxy()
	{
		return autostartProxy;
	}

	/**
	 * @param autostartProxy the autostartProxy to set.
	 */
	public void setAutostartProxy(boolean autostartProxy)
	{
		this.autostartProxy = autostartProxy;
	}

	/**
	 * @return the cbuilderFilterUrls.
	 */
	public String getCbuilderFilterUrls()
	{
		return cbuilderFilterUrls;
	}

	/**
	 * @param cbuilderFilterUrls the cbuilderFilterUrls to set.
	 */
	public void setCbuilderFilterUrls(String cbuilderFilterUrls)
	{
		this.cbuilderFilterUrls = cbuilderFilterUrls;
	}

	/**
	 * @return the monsterFilterUrls.
	 */
	public String getMonsterFilterUrls()
	{
		return monsterFilterUrls;
	}

	/**
	 * @param monsterFilterUrls the monsterFilterUrls to set.
	 */
	public void setMonsterFilterUrls(String monsterFilterUrls)
	{
		this.monsterFilterUrls = monsterFilterUrls;
	}

	/**
	 * @return the timeZoneId.
	 */
	public String getTimeZoneId()
	{
		return timeZoneId;
	}

	/**
	 * @return
	 */
	public TimeZone getTimeZone()
	{
		return TimeZone.getTimeZone(timeZoneId);
	}

	/**
	 * @param timeZoneId the timeZoneId to set.
	 */
	public void setTimeZoneId(String timeZoneId)
	{
		this.timeZoneId = timeZoneId;
	}
	
	@XmlElementWrapper(name="indiaClients")
	@XmlElement(name="Value")
	public void setList(List<String> list) {
		this.list = list;
	}
	
	public List<String> getList() {
		return list;
	}
	
	 
	 

	/**
	 * @return the reportsUrl.
	 *//*
	public String getReportsUrl()
	{
		return reportsUrl;
	}

	*//**
	 * @param reportsUrl the reportsUrl to set.
	 *//*
	public void setReportsUrl(String reportsUrl)
	{
		this.reportsUrl = reportsUrl;
	}

	*//**
	 * @return the resumeCategories.
	 *//*
	public List<String> getResumeCategories()
	{
		return resumeCategories;
	}

	*//**
	 * @param resumeCategories the resumeCategories to set.
	 *//*
	public void setResumeCategories(List<String> resumeCategories)
	{
		this.resumeCategories = resumeCategories;
	}

	*//**
	 * @return the standardResumeProps.
	 *//*
	public List<String> getStandardResumeProps()
	{
		return standardResumeProps;
	}

	*//**
	 * @param standardResumeProps the standardResumeProps to set.
	 *//*
	public void setStandardResumeProps(List<String> standardResumeProps)
	{
		this.standardResumeProps = standardResumeProps;
	}

	*//**
	 * @return the statusColors.
	 *//*
	public Map<CandidateStatus, String> getStatusColors()
	{
		return statusColors;
	}

	*//**
	 * @param statusColors the statusColors to set.
	 *//*
	public void setStatusColors(Map<CandidateStatus, String> statusColors)
	{
		this.statusColors = statusColors;
	}*/

	/**
	 * @return the joNotifyEmail.
	 */
	public String getJoNotifyEmail()
	{
		return joNotifyEmail;
	}

	/**
	 * @param joNotifyEmail the joNotifyEmail to set.
	 */
	public void setJoNotifyEmail(String joNotifyEmail)
	{
		this.joNotifyEmail = joNotifyEmail;
	}

	/**
	 * @return
	 */
	private static AppConfig init()
	{
		File configFile = getConfigFile();

		if (configFile.exists())
		{
			try
			{
				AppConfig config = load(configFile);
				
				return config;
			}
			catch (IOException e)
			{
				log.error("Error loading the configuration, using default "
				    + e.getMessage());
				AppConfig config = new AppConfig();
				return config;
			}
		}
		else
		{
			log.debug("Configuration file not found, using default");
			AppConfig config = new AppConfig();
			return config;
		}
	}

	/**
	 * @return
	 */
	public void reset()
	{
		/*homeDir = System.getProperty("user.home") + File.separator + ".cgiats";

		attachmentMaxSize = 2000000;
		attachmentTypes = "gif jpeg jpg png doc docx xls xlsx pdf ppt rtf txt tiff";*/
		
		smtpHost = "smtp.office365.com";
		//smtpHost = "smtp.1and1.com";
		smtpPort = 587;
		emailFrom = CGIATSConstants.PROD_MAIL;

		/*proxyPort = 9191;
		autostartProxy = false;
		cbuilderFilterUrls = "www.careerbuilder.com";
		monsterFilterUrls = "hiring.monster.com cookie.monster.com";*/
		timeZoneId = "US/Eastern"; 
//		timeZoneId = TimeZone.getDefault().getID();

		/*reportsUrl = "http://192.168.1.85:8181/jasperserver";

		resumeCategories.clear();
		standardResumeProps.clear();
		statusColors.clear();
		statusColors.put(CandidateStatus.Available, "e5fcfa");
		statusColors.put(CandidateStatus.ResumeSent, "fff9db");
		statusColors.put(CandidateStatus.OnAssignment, "ffeff0");*/
	}

	/**
	 * @throws IOException
	 */
	public synchronized void save() throws IOException
	{
		File configFile = getConfigFile();
		save(configFile);
	}

	/**
	 * @param configFile
	 * @throws IOException
	 */
	public synchronized void save(File configFile) throws IOException
	{
		log.debug("Saving configuration to file: " + configFile);

		File folder = configFile.getParentFile();
		if (!folder.exists())
			folder.mkdirs();

		try
		{
			JAXBContext ctx = JAXBContext.newInstance(AppConfig.class);
			Marshaller m = ctx.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

			m.marshal(this, configFile);
			log.info("Configuration saved");
		}
		catch (Exception e)
		{
			throw new IOException(e);
		}
	}

	/**
	 * @return
	 * @throws IOException
	 */
	public static AppConfig load() throws IOException
	{
		File configFile = getConfigFile();
		return load(configFile);
	}

	/**
	 * @param configFile
	 * @return
	 * @throws IOException
	 */
	public static AppConfig load(File configFile) throws IOException
	{
		log.debug("Loading configuration from file: " + configFile);

		try
		{
			JAXBContext ctx = JAXBContext.newInstance(AppConfig.class);
			Unmarshaller um = ctx.createUnmarshaller();
			AppConfig config = (AppConfig) um.unmarshal(configFile);
			log.info("Configuration loaded");

			return config;
		}
		catch (JAXBException e)
		{
			e.printStackTrace();
			return null;
		}
		
	}

	/**
	 * @return
	 */
	public static File getConfigFile()
	{
		String configFileName = System.getProperty("cgiats.config");
		if (configFileName == null)
			configFileName = System.getProperty("user.home") + File.separator
			    + ".cgiats" + File.separator + "config.xml";
		File configFile = new File(configFileName);

//		File configFile = new File(AppConfig.class.getClassLoader().getResource("config.xml").getFile());
		return configFile;
	}
	/**
	 * @return the techFetchUrls
	 */
	public String getTechFetchUrls() {
		return techFetchUrls;
	}

	/**
	 * @param techFetchUrls the techFetchUrls to set
	 */
	public void setTechFetchUrls(String techFetchUrls) {
		this.techFetchUrls = techFetchUrls;
	}
}
