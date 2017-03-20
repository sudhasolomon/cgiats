/*
 * UtilityBean.java Mar 8, 2012
 *
 * Copyright 2012 Uralian, LLC. All rights reserved.
 */
package com.uralian.cgiats.web;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.convert.Converter;
import javax.faces.convert.EnumConverter;
import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.uralian.cgiats.model.CandidateStatus;
import com.uralian.cgiats.model.JobOrderPriority;
import com.uralian.cgiats.model.JobOrderStatus;
import com.uralian.cgiats.model.JobType;
import com.uralian.cgiats.model.ReportTimeRange;
import com.uralian.cgiats.model.StatsTimeRange;
import com.uralian.cgiats.model.SubmittalStatus;
import com.uralian.cgiats.model.UserRole;
import com.uralian.cgiats.proxy.CGIATSConstants;
import com.uralian.cgiats.util.AppConfig;
import com.uralian.cgiats.util.Utils;

/**
 * This is a helper bean that provides some general purposes methods an object
 * collections to use by other beans and JSF components.
 * 
 * @author Vlad Orzhekhovskiy
 */
@Component
@Scope("application")
public class UtilityBean extends UIBean
{
	
	public static final String[] US_STATES = { "Others", "AL", "AK", "AZ", "AR", "CA",
		"CO", "CT", "DE", "DC", "FL", "GA", "HI", "IA", "ID", "IL", "IN", "KS",
		"KY", "LA", "MA", "MD", "ME", "MI", "MN", "MO", "MS", "MT", "NC", "ND",
		"NE", "NH", "NJ", "NM", "NV", "NY", "OH", "OK", "OR", "PA", "RI", "SC",
		"SD", "TN", "TX", "UT", "VA", "VT", "WA", "WI", "WV", "WY" };

	public static final String[] STATES = { "Others", "AL", "AK", "AZ", "AR", "CA",
		"CO", "CT", "DE", "DC", "FL", "GA", "HI", "IA", "ID", "IL", "IN", "KS",
		"KY", "LA", "MA", "MD", "ME", "MI", "MN", "MO", "MS", "MT", "NC", "ND",
		"NE", "NH", "NJ", "NM", "NV", "NY", "OH", "OK", "OR", "PA", "RI", "SC",
		"SD", "TN", "TX", "UT", "VA", "VT", "WA", "WI", "WV", "WY" };
	
	public static final String[] INDIA_STATES={"HYDERABAD","NOIDA","PUNE"};


	public static final String[] TIME_ZONES = { "US/Eastern", "US/Central",
		"US/Mountain", "US/Pacific" };

	public static final String[] VISA_TYPES = {"Not Available","US Citizen","Green Card","EAD","TN Visa","Canadian","H1B Visa","H4 Visa","L1 Visa","L2 Visa","OPT","CPT","Corp to Corp","Citizen"};
	public static final String[] RESUME_UPDATES = {"Last 3 months","Last 6 months","Last one year","None"};
	public static final String[] OFFICE_LOCATION = {"HYD", "NOIDA", "PUNE", "ATL", "AZ", "IL", "NY"};

	public static final String[] PORTAL_NAMES = {"Dice","Monster","Careerbuilder","CGI"};

	public static final String[] PORTAL_EMAILS = {"mark@charterglobal.com", "awilson@charterglobal.com","cjenkins@charterglobal.com","tbailey@charterglobal.com",
		"jlee@charterglobal.com","psmith@charterglobal.com","arichardson@charterglobal.com","igrayson@charterglobal.com",
		"kjones@charterglobal.com","dclassen@charterglobal.com","ejoy@charterglobal.com","fmiller@charterglobal.com",
		"gsnover@charterglobal.com","hreed@charterglobal.com","lmenzin@charterglobal.com","nlevine@charterglobal.com",
		"omorrison@charterglobal.com","qbutler@charterglobal.com","rdavis@charterglobal.com","swalters@charterglobal.com"};
	
	public static final String[] CB_PORTAL_EMAILS = {"jleonard@charterglobal.com","cjenkins@charterglobal.com",
		"tbailey@charterglobal.com","dshah@charterglobal.com","Mreddy@charterglobal.com",
		"Sharsh@charterglobal.com","msava@charterglobal.com","vmula@charterglobal.com",     
		"bvincent@charterglobal.com","nkashyap@charterglobal.com","rtelleysh@charterglobal.com","santosh@charterglobal.com"};

	public static final String[] MONSTER_PORTAL_EMAILS = {"xcharterpx42","xcharterpx031","xcharterpx44","xcharterpx45","xcharterpx46","xcharterpx47","xcharterpx48","xcharterpx49","xcharterpx50",    
		"xcharterpx51","xcharterpx52","xcharterpx53","xcharterpx54","xcharterpx55","xcharterpx56"};
	
	public static final String[] TECHFETCH_PORTAL_EMAILS = {"jpeterson@charterglobal.com","schaudhary@charterglobal.com","ssohane@charterglobal.com","Bkumar@charterglobal.com","Sverma@charterglobal.com","aparmar@charterglobal.com","vmishra@charterglobal.com"
		,"Amishra@charterglobal.com"
		,"rkamlesh@charterglobal.com"
		,"snitin@charterglobal.com"
		,"ksubodh@charterglobal.com"
		,"asmak@charterglobal.com"
		,"Trathore@charterglobal.com"
		,"rsingh@charterglobal.com"};

	public static final String[] HYD_MAILS=CGIATSConstants.HYD_MAILS;
	public static final String[] PUNE_MAILS=CGIATSConstants.PUNE_MAILS;
	public static final String[] NOIDA_MAILS=CGIATSConstants.NOIDA_MAILS;
	public static final String[] REC_HEAD= CGIATSConstants.REC_HEAD;
	
	private SortedMap<String, String> themes;
	private ArrayList<SelectItem> usStates;
	private ArrayList<SelectItem> states;
	
	private ArrayList<SelectItem> indiaStates;
	
	private ArrayList<SelectItem> userRoles;
	private ArrayList<SelectItem> jobOrderPriorities;
	private ArrayList<SelectItem> jobOrderStatuses;
	private ArrayList<SelectItem> jobTypes;
	private ArrayList<SelectItem> submittalStatuses;
	private ArrayList<SelectItem> submittalStatuses1;
	private ArrayList<SelectItem> timeZones;
	private ArrayList<SelectItem> candidateStatuses;
	private ArrayList<SelectItem> visaType;
	private ArrayList<SelectItem> resumeUpdate;
	private ArrayList<SelectItem> officeLocation;
	private ArrayList<SelectItem> portalNames;
	private ArrayList<SelectItem> portalemails;
	private ArrayList<SelectItem> cbportalemails;
	private ArrayList<SelectItem> monsterPortalEmails;
	private ArrayList<SelectItem> techFetchPortalEmails;



	private Converter priorityConverter;
	private Converter statusConverter;
	private Converter jobTypeConverter;
	private Converter statsRangeConverter;
	private Converter reportRangeConverter;


	@PostConstruct
	public void init()
	{
		initThemes();
		initUsStates();
		initStates();
		initIndiaStates();
		initUserRoles();
		initCandidateStatuses();
		initJobOrderPriorities();
		initJobOrderStatuses();
		initSubmittalStatuses();
		initSubmittalStatuses1();
		initJobTypes();
		initTimeZones();
		initVisaTypes();
		initResumeUpdates();
		initOfficeLocation();
		initConverters();
		initPortalNames();
		initPortalEmails();
		initcbPortalEmails();
		initMonsterPortalEmails();
		initTechFetchPortalEmails();
	}


	/**
	 * @return
	 */
	public Converter getPriorityConverter()
	{
		return priorityConverter;
	}

	/**
	 * @return the statusConverter.
	 */
	public Converter getStatusConverter()
	{
		return statusConverter;
	}

	/**
	 * @return the jobTypeConverter.
	 */
	public Converter getJobTypeConverter()
	{
		return jobTypeConverter;
	}

	/**
	 * @return
	 */
	public Converter getStatsRangeConverter()
	{
		return statsRangeConverter;
	}

	/**
	 * @return the reportRangeConverter
	 */
	public Converter getReportRangeConverter() {
		return reportRangeConverter;
	}

	/**
	 * @return
	 */
	public Map<String, String> getThemes()
	{
		return themes;
	}

	/**
	 * @return
	 */
	public List<SelectItem> getUsStates()
	{
		return usStates;
	}

	/**
	 * @return
	 */
	public ArrayList<SelectItem> getStates() {
		return states;
	}

	public ArrayList<SelectItem> getIndiaStates() {
		return indiaStates;
	}


	public void setIndiaStates(ArrayList<SelectItem> indiaStates) {
		this.indiaStates = indiaStates;
	}


	/**
	 * @return
	 */
	public List<SelectItem> getUserRoles()
	{
		return userRoles;
	}

	/**
	 * @return
	 */
	public List<SelectItem> getCandidateStatuses()
	{
		return candidateStatuses;
	}


	/**
	 * @return
	 */
	public List<SelectItem> getJobCategories()
	{
		AppConfig config = AppConfig.getInstance();

		List<SelectItem> categories = new ArrayList<SelectItem>();
		categories.add(new SelectItem("", "Select..."));
		/*for (String category : config.getResumeCategories())
			categories.add(new SelectItem(category));*/

		return categories;
	}

	/**
	 * @return
	 */
	public List<SelectItem> getJobOrderPriorities()
	{
		return jobOrderPriorities;
	}

	/**
	 * @return
	 */
	public List<JobOrderPriority> getJobPriorityList()
	{
		return Arrays.asList(JobOrderPriority.values());
	}

	/**
	 * @return
	 */
	public List<SelectItem> getJobOrderStatuses()
	{
		return jobOrderStatuses;
	}

	/**
	 * @return
	 */
	public List<JobOrderStatus> getJobStatusList()
	{
		return Arrays.asList(JobOrderStatus.values());
	}

	/**
	 * @return
	 */
	public List<SubmittalStatus> getSubmittalStatusList()
	{
		return Arrays.asList(SubmittalStatus.values());
	}


	/**
	 * @return
	 */
	public List<SelectItem> getJobTypes()
	{
		return jobTypes;
	}

	/**
	 * @return
	 */
	public List<JobType> getJobTypeList()
	{
		return Arrays.asList(JobType.values());
	}

	/**
	 * @return
	 */
	public List<SelectItem> getSubmittalStatuses()
	{
		return submittalStatuses;
	}

	/**
	 * @return
	 */
	public List<StatsTimeRange> getStatsTimeRanges()
	{
		return Arrays.asList(StatsTimeRange.values());
	}

	/**
	 * @return
	 */
	public List<ReportTimeRange> getreportTimeRanges()
	{
		return Arrays.asList(ReportTimeRange.values());
	}

	/**
	 * @return
	 *//*
	public int getAttachmentMaxSize()
	{
		return AppConfig.getInstance().getAttachmentMaxSize();
	}*/

	/**
	 * @return
	 *//*
	public String getAttachmentTypes()
	{
		String types = AppConfig.getInstance().getAttachmentTypes();
		if (Utils.isBlank(types))
			return ".*";

		return types.trim().replaceAll("(\\s|\\p{Punct})+", "|");
	}*/

	/**
	 * @return
	 */
	public List<SelectItem> getTimeZones()
	{
		return timeZones;
	}

	/**
	 * @return
	 */


	public List<SelectItem> getVisaType() {
		return visaType;
	}
public List<SelectItem> getResumeUpdate() {
		return resumeUpdate;
	}
	public ArrayList<SelectItem> getOfficeLocation() {
		return officeLocation;
	}


	public ArrayList<SelectItem> getPortalNames() {
		return portalNames;
	}

	public void setPortalNames(ArrayList<SelectItem> portalNames) {
		this.portalNames = portalNames;
	}


	public ArrayList<SelectItem> getPortalemails() {
		return portalemails;
	}

	public void setPortalemails(ArrayList<SelectItem> portalemails) {
		this.portalemails = portalemails;
	}


	/**
	 * @return
	 */
	public AppConfig getConfig()
	{
		return AppConfig.getInstance();
	}

	/**
	 */
	private void initThemes()
	{
		themes = new TreeMap<String, String>();
		themes.put("Aristo", "aristo");
		themes.put("Black Tie", "black-tie");
		themes.put("Blitzer", "blitzer");
		themes.put("Casablanca", "casablanca");
		themes.put("Cupertino", "cupertino");
		themes.put("Dark-Hive", "dark-hive");
		themes.put("Dot-Luv", "dot-luv");
		themes.put("Eggplant", "eggplant");
		themes.put("Excite-Bike", "excite-bike");
		themes.put("Flick", "flick");
		themes.put("Host-Sneaks", "hot-sneaks");
		themes.put("Humanity", "humanity");
		themes.put("Le-Frog", "le-frog");
		themes.put("Midnight", "midnight");
		themes.put("Mint-Choc", "mint-choc");
		themes.put("Overcast", "overcast");
		themes.put("Pepper-Grinder", "pepper-grinder");
		themes.put("Redmond", "redmond");
		themes.put("Rocket", "rocket");
		themes.put("Sam", "sam");
		themes.put("Smoothness", "smoothness");
		themes.put("South-Street", "south-street");
		themes.put("Start", "start");
		themes.put("Sunny", "sunny");
		themes.put("Swanky-Purse", "swanky-purse");
		themes.put("Trontastic", "trontastic");
		themes.put("UI-Darkness", "ui-darkness");
		themes.put("UI-Lightness", "ui-lightness");
		themes.put("Vader", "vader");
		themes.put("Bluesky", "bluesky");
		themes.put("Glass-X", "glass-x");
		themes.put("Home", "home");
		log.debug("Themes initialized");
	}

	/**
	 */
	private void initUsStates()
	{
		usStates = new ArrayList<SelectItem>();
		usStates.add(new SelectItem("", "Choose..."));
		for (String state : US_STATES)
			usStates.add(new SelectItem(state));
		log.debug("US States initialized");
	}

	/**
	 */
	private void initStates()
	{
		states = new ArrayList<SelectItem>();
		for (String state : STATES)
			states.add(new SelectItem(state));
		log.debug("States initialized");
	}
	
    private void initIndiaStates() {
		indiaStates=new ArrayList<SelectItem>();
		for (String inState : INDIA_STATES)
			indiaStates.add(new SelectItem(inState));
		log.debug("India States initialized");
    	
    	
	}

	/**
	 */
	private void initUserRoles()
	{
		userRoles = new ArrayList<SelectItem>();
		userRoles.add(new SelectItem("", "Select..."));
		for (UserRole role : UserRole.values())
			userRoles.add(new SelectItem(role));
		log.debug("User roles initialized");
	}

	/**
	 */
	private void initCandidateStatuses()
	{
		candidateStatuses = new ArrayList<SelectItem>();
		candidateStatuses.add(new SelectItem("", "Select..."));
		for (CandidateStatus status : CandidateStatus.values())
			candidateStatuses.add(new SelectItem(status));
		log.debug("Candidate Statuses initialized");
	}

	/**
	 */
	private void initJobOrderPriorities()
	{
		jobOrderPriorities = new ArrayList<SelectItem>();
		jobOrderPriorities.add(new SelectItem("", "Select..."));
		for (JobOrderPriority priority : JobOrderPriority.values())
			jobOrderPriorities.add(new SelectItem(priority));
		log.debug("JO priorities initialized");
	}

	/**
	 */
	private void initJobOrderStatuses()
	{
		jobOrderStatuses = new ArrayList<SelectItem>();
		jobOrderStatuses.add(new SelectItem("", "Select..."));
		for (JobOrderStatus status : JobOrderStatus.values()){
			jobOrderStatuses.add(new SelectItem(status));
		}
		log.debug("JO statuses initialized");
	}

	/**
	 */
	private void initJobTypes()
	{
		jobTypes = new ArrayList<SelectItem>();
		jobTypes.add(new SelectItem("", "Select..."));
		for (JobType type : JobType.values())
			jobTypes.add(new SelectItem(type));
		log.debug("Job types initialized");
	}

	/**
	 */
	private void initSubmittalStatuses()
	{
		submittalStatuses = new ArrayList<SelectItem>();
		submittalStatuses.add(new SelectItem("", "Select..."));
		for (SubmittalStatus status : SubmittalStatus.values())
			submittalStatuses.add(new SelectItem(status));
		log.debug("Submittal statuses initialized");
	}
	/**
	 */
	private void initSubmittalStatuses1()
	{
		submittalStatuses1 = new ArrayList<SelectItem>();
		submittalStatuses1.add(new SelectItem("All", "All"));
		for (SubmittalStatus status : SubmittalStatus.values())
			submittalStatuses1.add(new SelectItem(status));
		log.debug("Submittal statuses initialized");
	}

	/**
	 */
	private void initTimeZones()
	{
		timeZones = new ArrayList<SelectItem>();
		for (String tzId : TIME_ZONES)
			timeZones.add(new SelectItem(tzId));
		log.debug("US Time Zones initialized");
	}

	/**
	 */
	private void initVisaTypes()
	{
		visaType = new ArrayList<SelectItem>();
		visaType.add(new SelectItem("", "None..."));
		for (String vType : VISA_TYPES)
			visaType.add(new SelectItem(vType));
		log.debug("Visa Types initialized");
	}
	private void initResumeUpdates()
	{
		resumeUpdate = new ArrayList<SelectItem>();
		resumeUpdate.add(new SelectItem("","Last 1 month"));
		for (String resumeUp : RESUME_UPDATES)
			resumeUpdate.add(new SelectItem(resumeUp));
		log.debug("resumeUpdates initialized");
	}

	/**
	 */
	private void initOfficeLocation()
	{
		officeLocation = new ArrayList<SelectItem>();
		officeLocation.add(new SelectItem("", "None..."));
		for (String ofcLocation : OFFICE_LOCATION)
			officeLocation.add(new SelectItem(ofcLocation));
		log.debug("Visa Types initialized");
	}


	/**
	 */
	private void initConverters()
	{
		priorityConverter = new EnumConverter(JobOrderPriority.class);
		statusConverter = new EnumConverter(JobOrderStatus.class);
		jobTypeConverter = new EnumConverter(JobType.class);
		statsRangeConverter = new EnumConverter(StatsTimeRange.class);
		reportRangeConverter = new EnumConverter(ReportTimeRange.class);

	}


	/**
	 */
	private void initPortalNames() {

		portalNames = new ArrayList<SelectItem>();
		portalNames.add(new SelectItem("","Select..."));
		for(String portalName : PORTAL_NAMES)
			portalNames.add(new SelectItem(portalName));
		log.debug("Portal Names initialized");
	}

	/**
	 */
	private void initPortalEmails() {

		portalemails = new ArrayList<SelectItem>();
		portalemails.add(new SelectItem("","Select..."));
		for(String portalemail : PORTAL_EMAILS)
			portalemails.add(new SelectItem(portalemail));
		log.debug("Portal Emails initialized");
	}
	/**
	 */
	private void initcbPortalEmails() {

		cbportalemails = new ArrayList<SelectItem>();
		//		cbportalemails.add(new SelectItem("","Select..."));
		for(String portalemail : CB_PORTAL_EMAILS)
			cbportalemails.add(new SelectItem(portalemail));
		log.debug("CB Portal Emails initialized");
	}
	/**
	 */
	private void initMonsterPortalEmails() {
		monsterPortalEmails = new ArrayList<SelectItem>();
		for(String portalemail : MONSTER_PORTAL_EMAILS)
			monsterPortalEmails.add(new SelectItem(portalemail));
		log.debug("Monster Portal Emails initialized");
	}
	/**
	 */
	private void initTechFetchPortalEmails() {
		techFetchPortalEmails = new ArrayList<SelectItem>();
		for(String portalemail : TECHFETCH_PORTAL_EMAILS)
			techFetchPortalEmails.add(new SelectItem(portalemail));
		log.debug("TechFetch Portal Emails initialized");
	}

	/**
	 * @return
	 */
	public List<SubmittalStatus> getSelectedSubmittalStatusList()
	{
		List<SubmittalStatus> submittalStatus =Arrays.asList(SubmittalStatus.values());
		List<SubmittalStatus> subStatus = new ArrayList<SubmittalStatus>();
		Iterator ir = submittalStatus.iterator();
		while(ir.hasNext()){
			SubmittalStatus status = (SubmittalStatus)ir.next();
			if(status.equals(SubmittalStatus.STARTED) || status.equals(SubmittalStatus.CONFIRMED) || status.equals(SubmittalStatus.BACKOUT)){
				subStatus.add(status);
			}
		}
		return subStatus;
	}

	/**
	 * @return
	 */

	public List<SubmittalStatus> getRecrSelectedSubmittalStatusList()
	{
		List<SubmittalStatus> submittalStatus =Arrays.asList(SubmittalStatus.values());
		List<SubmittalStatus> subStatus = new ArrayList<SubmittalStatus>();
		Iterator ir = submittalStatus.iterator();
		while(ir.hasNext()){
			SubmittalStatus status = (SubmittalStatus)ir.next();
			if(status.equals(SubmittalStatus.SUBMITTED) || status.equals(SubmittalStatus.ACCEPTED) || status.equals(SubmittalStatus.DMREJ) || status.equals(SubmittalStatus.CONFIRMED) || status.equals(SubmittalStatus.INTERVIEWING) || status.equals(SubmittalStatus.REJECTED)){
				subStatus.add(status);
			}
		}
		return subStatus;
	}
	/**
	 * @return
	 */
	public List<SubmittalStatus> getDashboardSubmittalStatusList()
	{
		List<SubmittalStatus> submittalStatus =Arrays.asList(SubmittalStatus.values());
		List<SubmittalStatus> subStatus = new ArrayList<SubmittalStatus>();
		Iterator ir = submittalStatus.iterator();
		while(ir.hasNext()){
			SubmittalStatus status = (SubmittalStatus)ir.next();
			if(status.equals(SubmittalStatus.SUBMITTED) || status.equals(SubmittalStatus.STARTED) || status.equals(SubmittalStatus.BACKOUT) || status.equals(SubmittalStatus.OUTOFPROJ)){
				subStatus.add(status);
			}
		}
		System.out.println("subStatus...."+subStatus);
		return subStatus;
	}
	
	/**
	 * @return
	 */
	public List<JobOrderStatus> getDashboardJobOrdersList()
	{
		List<JobOrderStatus> jobOrderStatus =Arrays.asList(JobOrderStatus.values());
		List<JobOrderStatus> joStatus = new ArrayList<JobOrderStatus>();
		Iterator ir = jobOrderStatus.iterator();
		while(ir.hasNext()){
			JobOrderStatus status = (JobOrderStatus)ir.next();
			if(status.equals(JobOrderStatus.OPEN) || status.equals(JobOrderStatus.CLOSED)){
				joStatus.add(status);
			}
		}
		System.out.println("subStatus...."+joStatus);
		return joStatus;
	}
	/**
	 * @return
	 */
	public List<SubmittalStatus> getDmSubmittalStatusList()
	{
		List<SubmittalStatus> submittalStatus =Arrays.asList(SubmittalStatus.values());
		List<SubmittalStatus> subStatus = new ArrayList<SubmittalStatus>();
		Iterator ir = submittalStatus.iterator();
		while(ir.hasNext()){
			SubmittalStatus status = (SubmittalStatus)ir.next();
			if(!status.equals(SubmittalStatus.SUBMITTED)){
				subStatus.add(status);
			}
		}
		return subStatus;
	}
	public List<SubmittalStatus> getDmSubmittalStatusListActivityReport()
	{
		List<SubmittalStatus> submittalStatus =Arrays.asList(SubmittalStatus.values());
		List<SubmittalStatus> subStatus = new ArrayList<SubmittalStatus>();
		Iterator ir = submittalStatus.iterator();
		
		while(ir.hasNext()){
			SubmittalStatus status = (SubmittalStatus)ir.next();
			if(status.toString().equals(SubmittalStatus.BACKOUT.toString())||status.toString().equals(SubmittalStatus.OUTOFPROJ.toString())||status.toString().equals(SubmittalStatus.CONFIRMED.toString())){
				
			}else{
				subStatus.add(status);
			}
		}
		return subStatus;
	}

	public ArrayList<SelectItem> getSubmittalStatuses1() {
		return submittalStatuses1;
	}

	public void setSubmittalStatuses1(ArrayList<SelectItem> submittalStatuses1) {
		this.submittalStatuses1 = submittalStatuses1;
	}

	public ArrayList<SelectItem> getCbportalemails() {
		return cbportalemails;
	}

	public void setCbportalemails(ArrayList<SelectItem> cbportalemails) {
		this.cbportalemails = cbportalemails;
	}

	public ArrayList<SelectItem> getMonsterPortalEmails() {
		return monsterPortalEmails;
	}

	public void setMonsterPortalEmails(ArrayList<SelectItem> monsterPortalEmails) {
		this.monsterPortalEmails = monsterPortalEmails;
	}

	public ArrayList<SelectItem> getTechFetchPortalEmails() {
		return techFetchPortalEmails;
	}

	public void setTechFetchPortalEmails(ArrayList<SelectItem> techFetchPortalEmails) {
		this.techFetchPortalEmails = techFetchPortalEmails;
	}
}