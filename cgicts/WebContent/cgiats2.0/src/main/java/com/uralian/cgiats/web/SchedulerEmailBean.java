package com.uralian.cgiats.web;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

import com.uralian.cgiats.dto.JobOrderDto;
import com.uralian.cgiats.dto.ResumesUpdateCountDto;
import com.uralian.cgiats.model.CandidateSearchDto;
import com.uralian.cgiats.model.EmailConfiguration;
import com.uralian.cgiats.model.JobOrder;
import com.uralian.cgiats.model.User;
import com.uralian.cgiats.model.UserRole;
import com.uralian.cgiats.service.CandidateService;
import com.uralian.cgiats.service.CommService;
import com.uralian.cgiats.service.EmailConfigurationService;
import com.uralian.cgiats.service.JobOrderService;
import com.uralian.cgiats.service.ServiceException;
import com.uralian.cgiats.service.UserService;
import com.uralian.cgiats.util.AppConfig;
import com.uralian.cgiats.util.ExcelGeneratorJobOrderScheduler;
import com.uralian.cgiats.util.ReportNameConstants;
import com.uralian.cgiats.util.Utils;

/**
 * @author Parameshwar
 * 
 */
@Controller
public class SchedulerEmailBean {

	protected final Logger log = LoggerFactory.getLogger(getClass());

	private List<JobOrder> jobOrders;
	private List<User> users;
	private String emailTo = null;
	private int resumeTotal;
	private int updatedResumeTotal;
	private Map<String, Integer> resumeStatsByUser;
	private Map<String, Integer> resumeStatsByUserUpdates;
	@Autowired
	private EmailConfigurationService emailConfigurationService;

	@Autowired
	private JobOrderService jobOrderService;

	@Autowired
	private JobOrderService joOrderService;

	@Autowired
	private UserService userService;

	@Autowired
	private CommService commService;

	@Autowired
	private CandidateService candidateService;

	public SchedulerEmailBean() {

	}

	/**
	 * @return the list
	 */
	public List<JobOrder> getJobOrders() {
		return jobOrders;
	}

	/**
	 * @param list
	 *            the list to set
	 */
	public void setList(List<JobOrder> jobOrders) {
		this.jobOrders = jobOrders;
	}

	/**
	 * @return the users
	 */
	public List<User> getUsers() {
		return users;
	}

	/**
	 * @param users
	 *            the users to set
	 */
	public void setUsers(List<User> users) {
		this.users = users;
	}

	/**
	 * @return the emailTo
	 */
	public String getEmailTo() {
		return emailTo;
	}

	/**
	 * @param emailTo
	 *            the emailTo to set
	 */
	public void setEmailTo(String emailTo) {
		this.emailTo = emailTo;
	}

	/**
	 * @return the resumeTotal
	 */
	public int getResumeTotal() {
		return resumeTotal;
	}

	/**
	 * @param resumeTotal
	 *            the resumeTotal to set
	 */
	public void setResumeTotal(int resumeTotal) {
		this.resumeTotal = resumeTotal;
	}

	/**
	 * @return the resumeStatsByUser
	 */
	public Map<String, Integer> getResumeStatsByUser() {
		return resumeStatsByUser;
	}

	/**
	 * @param resumeStatsByUser
	 *            the resumeStatsByUser to set
	 */
	public void setResumeStatsByUser(Map<String, Integer> resumeStatsByUser) {
		this.resumeStatsByUser = resumeStatsByUser;
	}

	/**
	 * @return the resumeStatsByUserUpdates
	 */
	public Map<String, Integer> getResumeStatsByUserUpdates() {
		return resumeStatsByUserUpdates;
	}

	/**
	 * @param resumeStatsByUserUpdates
	 *            the resumeStatsByUserUpdates to set
	 */

	public void setResumeStatsByUserUpdates(Map<String, Integer> resumeStatsByUserUpdates) {
		this.resumeStatsByUserUpdates = resumeStatsByUserUpdates;
	}

	/**
	 * @return the updatedResumeTotal
	 */

	public int getUpdatedResumeTotal() {
		return updatedResumeTotal;
	}

	/**
	 * @param updatedResumeTotal
	 *            the updatedResumeTotal to set
	 */

	public void setUpdatedResumeTotal(int updatedResumeTotal) {
		this.updatedResumeTotal = updatedResumeTotal;
	}

	// every Day below mentioned cron time
	@Scheduled(cron = "0 10 7 * * ?")
	// @Scheduled(cron = "0 0/5 * 1/1 * ?")
	public void myProcess() throws ServiceException {

		log.info("Schedduler thread is started	::");
		StringBuffer sb = new StringBuffer();
		List<User> emUsers = getEMList();

		// StringBuffer sb = new StringBuffer();
		for (User u : emUsers) {
			String email = u.getEmail();
			log.info("User EM email	::" + emUsers.size());
			if (!Utils.isBlank(email)) {
				if (email.contains("@")) {
					sb.append(email).append(";");
				}
			}
		}
		setEmailTo(emailIds(ReportNameConstants.JobOrdersWithOutSubmittals.toString()));

		getAllExportToExcel();
		log.info("Schedduler thread is ended	::");
	}

	private String emailIds(String reportName) {
		StringBuffer sb = new StringBuffer();
		List<EmailConfiguration> emailCfg = new ArrayList<EmailConfiguration>(Arrays.asList(emailConfigurationService.getEmails()));
		String[] emails = null;
		log.info("Emails" + emailCfg.size());
		for (EmailConfiguration email : emailCfg) {

			if (email.getReportName().equals(reportName)) {
				emails = email.getEmails().split(",");
				for (String e : emails) {
					sb.append(e + ";");
				}
			}
		}
		return sb.toString();
	}

	@Scheduled(cron = "0 15 7 * * ?")
	// @Scheduled(cron = "0 0/1 * 1/1 * ?")
	public void sendJOOpen() throws ServiceException {

		log.info("Schedduler thread is started	::");
		List<JobOrder> jobOrders = jobOrderService.findOpenJobOrders();
		StringBuffer sb = new StringBuffer();

		String sbstr = new String();
		for (JobOrder jo : jobOrders) {
			String createdBy = jo.getCreatedBy();
			log.info("createdBY>>" + createdBy);
			User user = userService.loadUser(createdBy);
			if (user != null) {
				if (user.getStatus().equals("ACTIVE")) {
					if (user != null && user.getEmail() != null && sbstr != null && !sbstr.contains(user.getEmail())) {
						if (!Utils.isBlank(user.getEmail())) {
							if (user.getEmail().contains("@")) {
								sb.append(user.getEmail()).append(";");
								sbstr = sb.toString();
							}
						}
					}
				}
			}
		}
		log.info("CreatedEmail" + sbstr);
		List<User> emUsers = getEMList();
		for (User u : emUsers) {
			String email = u.getEmail();
			log.info("User EM email	::" + emUsers.size());
			if (!Utils.isBlank(email)) {
				if (email.contains("@")) {
					sb.append(email).append(";");
				}
			}
		}

		List<EmailConfiguration> emailCfg = new ArrayList<EmailConfiguration>(Arrays.asList(emailConfigurationService.getEmails()));
		String[] emails = null;
		log.info("Emails" + emailCfg.size());
		for (EmailConfiguration email : emailCfg) {
			log.info("ReportName" + email.getReportName());
			if (email.getReportName().equals(ReportNameConstants.JobOrdersInOpenStatus.toString())) {
				log.info("from if");
				emails = email.getEmails().split(",");
				for (String e : emails) {
					sb.append(e + ";");
				}
			}
		}

		setEmailTo(sb.toString());

		log.info("getemailsss" + getEmailTo());
		getAllOpenJosExportToExcel();
		log.info("Schedduler thread is ended	::");
	}

	// @Scheduled(cron = "0 0/5 * 1/1 * ?")
//	@Scheduled(cron = "*/10 * * * * *")
	@Scheduled(cron = "0 0 7 * * ?")
	public void myProcess1() throws ServiceException {
		log.info("Schedduler thread for Resume Count is Started	::");
//		Date from = getFromDate();
//		Date to = getFromDate();
		
		
		
		Calendar date = Calendar.getInstance();
//		startDate.setTime(new Date());
		date.add(Calendar.DATE, -1);
//		startDate.set(Calendar.HOUR_OF_DAY, 21);
//		startDate.set(Calendar.MINUTE, 0);
//		startDate.set(Calendar.SECOND, 0);
//
//		Calendar endDate = Calendar.getInstance();
//		endDate.setTime(new Date());
////		endDate.add(Calendar.DATE, -1);
//		endDate.set(Calendar.HOUR_OF_DAY, 9);
//		endDate.set(Calendar.MINUTE, 0);
//		endDate.set(Calendar.SECOND, 0);
		
		
		
		
		
		
		Calendar startDate = Calendar.getInstance();
		startDate.setTime(date.getTime());
		startDate.add(Calendar.DATE, -1);
		startDate.set(Calendar.HOUR, 21);

		Calendar endDate = Calendar.getInstance();
		endDate.setTime(date.getTime());
		endDate.set(Calendar.HOUR_OF_DAY, 9);
		
		String actualStrFromDate = Utils.convertDateToString(date.getTime());
		Integer countValue=0;
		
		if (startDate.getTime() != null && endDate.getTime() != null) {
			resumeStatsByUser = candidateService.getCandidatesByUser(startDate.getTime(), endDate.getTime(), null);
			//resumeStatsByUserUpdates = candidateService.getCandidatesOnUpdatedDate(startDate.getTime(), endDate.getTime());
			List<ResumesUpdateCountDto> resumesupdatecountdata = new ArrayList<ResumesUpdateCountDto>();
			resumesupdatecountdata = userService.getResumesUpdateCount(startDate.getTime(), endDate.getTime(),actualStrFromDate,actualStrFromDate);
			
			for(int i=0; i<resumesupdatecountdata.size(); i++){
				countValue+= ((Double)resumesupdatecountdata.get(i).getResumes_count()).intValue();
			}
		}
		
		
		resumeTotal = 0;
		updatedResumeTotal = 0;

		for (Integer count : resumeStatsByUser.values())
			resumeTotal += count;

		/*for (Integer updatedCount : resumeStatsByUserUpdates.values())
			updatedResumeTotal += updatedCount;*/

		CandidateSearchDto criteria = new CandidateSearchDto();
		int rowCount = candidateService.findCandidatesCount(criteria);
		List<EmailConfiguration> emailCfg = new ArrayList<EmailConfiguration>(Arrays.asList(emailConfigurationService.getEmails()));
		String[] emails = null;
		log.info("Emails" + emailCfg.size());
		for (EmailConfiguration email : emailCfg) {

			if (email.getReportName().equals("Resume Uploaded/Updated")) {
				emails = email.getEmails().split(",");

			}
		}
	
		// String[] email = CGIATSConstants.SCHEDULED_MAIL;
//		SimpleDateFormat df = new SimpleDateFormat("MM-dd-yyyy");
		commService.sendEmail(null, emails, null, "CGIATS: Resume upload Report",
				"Hi all, <br/><br/>&nbsp;&nbsp;&nbsp;&nbsp;No. of CGIATS Resumes uploaded on " + "" + actualStrFromDate + " = " + resumeTotal + "."
						+ "<br/>&nbsp;&nbsp;&nbsp;&nbsp;No. of CGIATS Resumes updated on " + actualStrFromDate + "=" + countValue + "."
						+ "<br/>&nbsp;&nbsp;&nbsp;&nbsp;Total Resumes: " + rowCount
						+ ".<br/><br/><b>*** This is an automatically generated email, please do not reply ***</b> ");
		log.info("Schedduler thread for Resume Count is Ended	::");
	}

	// For every one hour
	 @Scheduled(cron = "0 0 0/3 * * ?")
//	@Scheduled(cron = "0 24 18 * * ?")
	public void convertJobOrdersToHot() throws ServiceException {
		try {

			log.info("<<<<<<<<<< Converting Job Orders To hot Job Order started >>>>>>>");
			List<JobOrder> jobOrderList = jobOrderService.getPossibleJobOrdersToHot();

			if (jobOrderList != null && jobOrderList.size() > 0) {
				log.info("<<<<<<<<<< Possible Job orders size to make hot is ::: " + jobOrderList.size() + " >>>>>>>");
				for (JobOrder jobOrder : jobOrderList) {
					jobOrder.setHot(!jobOrder.isHot());
					jobOrder.setReason("Need Help".getBytes());
					jobOrder.setUpdatedOn(new Date());
					jobOrderService.updateJobOrder(jobOrder);
					jobOrderService.sendHotJobMail(jobOrder, "All");
				}
			}
			sendMailForJobOrdersWithoutSubmittals();
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
//		sendMailForJobOrdersWithoutSubmittals();
	}

	// For every one hour
	@SuppressWarnings("unchecked")
//	 @Scheduled(cron = "0 0 0/3 * * ?")
//	@Scheduled(cron = "0 49 14 * * ?")
	public void sendMailForJobOrdersWithoutSubmittals() throws ServiceException {
		InputStream is = null;
		DefaultStreamedContent dsc = null;
		try {
			log.info("<<<<<<<<<< Getting all Joborders wihtout submittals started >>>>>>>");
			List<JobOrderDto> jobOrderDtoList = (List<JobOrderDto>) jobOrderService.getJobOrdersByWithoutSubmittals();

			if (jobOrderDtoList != null && jobOrderDtoList.size() > 0) {
				List<Integer> jobOrderIdList=new ArrayList<Integer>();
				Map<Integer,Integer> jobOrderWithCountMap=new HashMap<Integer,Integer>();
				for(JobOrderDto dto:jobOrderDtoList){
					jobOrderWithCountMap.put(dto.getJobOrderId(),dto.getCount());
					jobOrderIdList.add(dto.getJobOrderId());
				}
				
				log.info("<<<<<<<<<< Job Orders With Less required Submittals Map Ids ::: " + jobOrderWithCountMap + " >>>>>>>");
				List<JobOrder> jobOrderList = jobOrderService.getJobOrderByIds(jobOrderIdList);
				ExcelGeneratorJobOrderScheduler myReport = new ExcelGeneratorJobOrderScheduler();

				setEmailTo(emailIds(ReportNameConstants.JobOrdersWithLessNoOfSubmittals.toString()));
				
				byte[] document = myReport.createExcelForOneDayWithoutSubmittals(jobOrderWithCountMap,jobOrderList, getEmailTo().trim());
				log.info("getEmails" + getEmailTo().toString());
				is = new ByteArrayInputStream(document);

				String contentType = "application/vnd.ms-excel";
				String fileName = "JobOrder_" + new Date(System.currentTimeMillis()) + ".xls";
				dsc = new DefaultStreamedContent(is, contentType, fileName);
				dsc.setContentType(contentType);
			}
			log.info("<<<<<<<<<< Getting all Joborders wihtout submittals ended >>>>>>>");
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
	}

	public Date getFromDate() {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_YEAR, -1);
		setToDayStart(cal);
		return cal.getTime();
	}

	/**
	 * @param cal
	 */
	public static void setToDayStart(Calendar cal) {
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
	}

	public List<User> getEMList() {
		users = userService.listBdms();
		List<User> ems = new ArrayList<User>();
		for (User user : users) {
			if (user.getUserRole().equals(UserRole.EM)) {
				ems.add(user);
			}
		}
		return ems;
	}

	public StreamedContent getAllOpenJosExportToExcel() {
		log.info(":: Inside getAllOpenJosExportToExcel :: ");
		DefaultStreamedContent dsc = null;
		InputStream is = null;
		try {
			ExcelGeneratorJobOrderScheduler myReport = new ExcelGeneratorJobOrderScheduler();

			List<JobOrder> jobOrders = jobOrderService.findOpenJobOrders();
			log.info("jobOrders" + jobOrders.size());
			List<JobOrder> list = new ArrayList<JobOrder>();
			for (JobOrder jo : jobOrders) {
				JobOrder order = joOrderService.getJobOrder(jo.getId(), true, true);

				if (order.isCustomerHidden() == true) {
					order.setCustomer("");
				}

				Date d1 = order.getCreatedOn();
				Date d2 = new Date();

				SimpleDateFormat sd = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");

				Calendar cal1 = new GregorianCalendar();
				Calendar cal2 = new GregorianCalendar();

				SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
				Date date1 = null;
				Date date2 = null;
				try {
					date1 = sdf.parse(d1.toString());
					cal1.setTime(date1);

					date2 = sdf.parse(sd.format(d2));
					cal2.setTime(date2);
				} catch (ParseException e) {
					e.printStackTrace();
				}
				long daysDiff = date2.getTime() - date1.getTime();
				// long hours = daysDiff / (60 * 60 * 1000);
				long days = daysDiff / (24 * 60 * 60 * 1000);
				order.setDays(days);
				if (days >= 10) {
					list.add(order);
				}
			}
			jobOrders = list;

			byte[] document = myReport.createExcelOpenJOs(jobOrders, getEmailTo().trim());
			is = new ByteArrayInputStream(document);

			String contentType = "application/vnd.ms-excel";
			String fileName = "JobOrder_" + new Date(System.currentTimeMillis()) + ".xls";
			dsc = new DefaultStreamedContent(is, contentType, fileName);
			dsc.setContentType(contentType);
		} catch (Exception e) {
			e.printStackTrace();
			// addErrorMessage("Problem in AllExportToExcel() ", SEVERITY_ERROR,
			// e);
		} finally {
			if (is != null)
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		log.info(":: End of getAllExportToExcel :: ");
		return dsc;
	}

	public StreamedContent getAllExportToExcel() {
		log.info(":: Inside getAllExportToExcel :: ");
		DefaultStreamedContent dsc = null;
		InputStream is = null;
		try {
			ExcelGeneratorJobOrderScheduler myReport = new ExcelGeneratorJobOrderScheduler();

			List<JobOrder> jobOrders = jobOrderService.findEmptySbmJobOrders();

			List<JobOrder> list = new ArrayList<JobOrder>();
			for (JobOrder jo : jobOrders) {
				JobOrder order = joOrderService.getJobOrder(jo.getId(), true, true);

				if (order.isCustomerHidden() == true) {
					order.setCustomer("");
				}

				Date d1 = order.getCreatedOn();
				Date d2 = new Date();

				SimpleDateFormat sd = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");

				Calendar cal1 = new GregorianCalendar();
				Calendar cal2 = new GregorianCalendar();

				SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
				Date date1 = null;
				Date date2 = null;
				try {
					date1 = sdf.parse(d1.toString());
					cal1.setTime(date1);

					date2 = sdf.parse(sd.format(d2));
					cal2.setTime(date2);
				} catch (ParseException e) {
					e.printStackTrace();
				}
				long daysDiff = date2.getTime() - date1.getTime();
				long hours = daysDiff / (60 * 60 * 1000);
				long days = daysDiff / (24 * 60 * 60 * 1000);
				order.setDays(days);
				if (hours >= 48) {
					list.add(order);
				}
			}
			jobOrders = list;
			if (jobOrders != null && jobOrders.size() > 0) {
				byte[] document = myReport.createExcel(jobOrders, getEmailTo().trim());
				log.info("getEmails" + getEmailTo().toString());
				is = new ByteArrayInputStream(document);

				String contentType = "application/vnd.ms-excel";
				String fileName = "JobOrder_" + new Date(System.currentTimeMillis()) + ".xls";
				dsc = new DefaultStreamedContent(is, contentType, fileName);
				dsc.setContentType(contentType);
			}
		} catch (Exception e) {
			e.printStackTrace();
			// addErrorMessage("Problem in AllExportToExcel() ", SEVERITY_ERROR,
			// e);
		} finally {
			if (is != null)
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		log.info(":: End of getAllExportToExcel :: ");
		return dsc;
	}

	public void sendEmailWithAttachment(String recipients, String subject, String body, String attachmentFileName, HSSFWorkbook hssWorkBook, int totalRows)
			throws ServiceException {

		final AppConfig config = AppConfig.getInstance();

		if (Utils.isBlank(config.getSmtpHost()))
			throw new ServiceException("SMTP host not set");
		if (config.getSmtpPort() <= 0)
			throw new ServiceException("SMTP port not set");
		String portNumber = String.valueOf(config.getSmtpPort());
		Properties props = System.getProperties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", config.getSmtpHost());
		props.put("mail.smtp.port", portNumber);

		Session session = Session.getInstance(props, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(config.getSmtpUsername(), config.getSmtpPassword());
			}
		});

		try {
			InternetAddress addrFrom;
			addrFrom = new InternetAddress(config.getEmailFrom());

			MimeMessage msg = new MimeMessage(session);
			DataSource ds = null;
			msg.setFrom(addrFrom);

			ArrayList<String> recipientsArray = new ArrayList<String>();
			StringTokenizer stringTokenizer = new StringTokenizer(recipients, ";");

			while (stringTokenizer.hasMoreTokens()) {
				recipientsArray.add(stringTokenizer.nextToken());
			}
			int sizeTo = recipientsArray.size();
			InternetAddress[] addressTo = new InternetAddress[sizeTo];
			for (int i = 0; i < sizeTo; i++) {
				addressTo[i] = new InternetAddress(recipientsArray.get(i).toString());
			}
			msg.setRecipients(Message.RecipientType.TO, addressTo);

			msg.setFrom(addrFrom);
			msg.addFrom(new Address[] { addrFrom });
			msg.setReplyTo(new Address[] { addrFrom });

			msg.setSubject(subject);
			// create and fill the first message part
			MimeBodyPart mimeBodyPart1 = new MimeBodyPart();
			mimeBodyPart1.setContent(body, "text/html");
			// mimeBodyPart1.setText(body);

			// create the second message part
			MimeBodyPart mimeBodyPart2 = new MimeBodyPart();

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			try {
				hssWorkBook.write(baos);
				byte[] bytes = baos.toByteArray();
				ds = new ByteArrayDataSource(bytes, "application/excel");
			} catch (IOException ioe) {
				// this.sendEmail(null,,"hobione@hobione.com",
				// "Survey excel file send error", "ByteArrayOutputStream: " +
				// ioe);
				ioe.printStackTrace();
			}
			DataHandler dh = new DataHandler(ds);
			mimeBodyPart2.setHeader("Content-Disposition", "attachment;filename=" + attachmentFileName + ".xls");
			mimeBodyPart2.setDataHandler(dh);
			mimeBodyPart2.setFileName(attachmentFileName);
			// create the Multipart and add its parts to it
			Multipart multiPart = new MimeMultipart();
			multiPart.addBodyPart(mimeBodyPart1);
			multiPart.addBodyPart(mimeBodyPart2);

			// add the Multipart to the message
			msg.setContent(multiPart);

			// set the Date: header
			msg.setSentDate(new Date());

			// send the message
			javax.mail.Transport.send(msg);
			System.out.println("Report emailed successfully to: " + recipients + " Total rows count:" + totalRows);

		} catch (MessagingException mex) {
			mex.printStackTrace();
			Exception ex = null;
			if ((ex = mex.getNextException()) != null) {
				// this.sendEmail("hobione@hobion.com",
				// "Survey excel file send error", "Print Stack Trace: " + ex);
				ex.printStackTrace();
			}
		}
	}
}
