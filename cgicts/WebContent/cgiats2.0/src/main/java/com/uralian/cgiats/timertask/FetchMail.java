package com.uralian.cgiats.timertask;

import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import javax.naming.InitialContext;
import javax.sql.DataSource;

import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.uralian.cgiats.service.CommService;
import com.uralian.cgiats.service.CommService.AttachmentInfo;
import com.uralian.cgiats.service.impl.CommServiceImpl;

@Component
@Scope("session")
public class FetchMail extends TimerTask {

	protected final Logger	log	= LoggerFactory.getLogger(getClass());
	@Autowired
	public CommService	commService;
	@Autowired
	@Qualifier("dataSource")
	public DataSource dataSource;
	
	public String[] emailAddrArray = null;
    private List<String> reportXmlPath = null;

	/**
	 * Construct and use a TimerTask and Timer.
	 */

	// perform the task once a day at 6 a.m., starting tomorrow morning

	public void getTimerStart(String[] emailAddrArray, List<String> reportXmlPath) {
		log.info(":: FetchMail Timer Started :: ");
		this.emailAddrArray = emailAddrArray;
		this.reportXmlPath = reportXmlPath;
		try {
			Timer timer = new Timer();
			System.out.println("FetchMail.getReportData()...commService obj: "+commService);
			System.out.println("FetchMail.getReportData()...dataSource obj: "+dataSource);
			timer.scheduleAtFixedRate(this, getTomorrowMorning6am(), fONCE_PER_DAY);
		} catch (Exception e) {
			log.error("Exception in TimerTask: " + e.fillInStackTrace());
		}
	}

	/**
	 * 
	 * Implements TimerTask's abstract run method.
	 */

	public void run() {
		// run implementation
		log.info(":: Entered into run() of Fetching mail :: ");
		getDailyReportData();
		getTotalResumeCountReport();
	}

	// expressed in milliseconds

	private final static long	fONCE_PER_DAY	= 1000 * 60 * 60 * 24;

	private final static int	fONE_DAY		= 0;

	private final static int	fSIX_AM		= 6;

	private final static int	fZERO_MINUTES	= 0;

	public Date getTomorrowMorning6am() {
	
		Calendar tomorrow = new GregorianCalendar();

		tomorrow.add(Calendar.DATE, fONE_DAY);

		Calendar result = new GregorianCalendar(

		tomorrow.get(Calendar.YEAR),

		tomorrow.get(Calendar.MONTH),

		tomorrow.get(Calendar.DATE),

		fSIX_AM,

		fZERO_MINUTES

		);
		Date tmrTime = result.getTime();
		log.info(":: FetchMail.getTomorrowMorning6am()..." + tmrTime);
		return tmrTime;
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	public void getDailyReportData() {
		Connection connection = null;
		CommServiceImpl commServiceImpl = null;
		try {
			log.info(":: Inside getDailyReportData():: ");
			//InputStream inputStream = new FileInputStream("c:/reports/ResumeByTitle_toexport.jrxml");
			InputStream inputStream = new FileInputStream(reportXmlPath.get(0));

			connection = getConnection();

			// loading the report
			JasperDesign jasperDesign = JRXmlLoader.load(inputStream);
			// adding parameters to report
			Map parameters = new HashMap();
			parameters.put("report_begin_date", getYesterdayStartDate());
			parameters.put("report_end_date", getYesterdayEndDate());
			// compile report
			JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);
			// fill report
			JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, connection);
			// export to pdf
			byte[] pdfdata = JasperExportManager.exportReportToPdf(jasperPrint);
			
			String name = "resumes_by_title_daily_report.pdf"; // report filename
			AttachmentInfo ai = buildAttachmentInfo(pdfdata,name);
			//send mail
			commServiceImpl = new CommServiceImpl();
			commServiceImpl.sendEmail(null, emailAddrArray, null, "Resumes by Title Daily Report", "Resumes by Title Daily Report generated.", ai);
			log.info("File Generated...");
		} catch (Exception e) {
			log.error(" Exception in getDailyReportData :: "+e.getMessage());
			e.printStackTrace();
		}
		finally{
			if(connection!= null)
			{
				try {
					connection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			commServiceImpl = null;
		}
	}
	
	/**
	 * Used to get the grand total of resumes loaded in ATS
	 * @param emailAddrArray
	 */
	@SuppressWarnings({"unchecked", "rawtypes"})
	public void getTotalResumeCountReport() {
		Connection connection = null;
		CommServiceImpl commServiceImpl = null;
		try {
			log.info(":: Inside getTotalResumeCountReport():: ");
			//InputStream inputStream = new FileInputStream("c:/reports/GrandResumeCount_toexport.jrxml");
			InputStream inputStream = new FileInputStream(reportXmlPath.get(1));

			connection = getConnection();

			// loading the report
			JasperDesign jasperDesign = JRXmlLoader.load(inputStream);
			// adding parameters to report
			Map parameters = new HashMap();
			parameters.put("report_begin_date", getYesterdayStartDate());
			parameters.put("report_end_date", new Timestamp(System.currentTimeMillis()));
			// compile report
			JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);
			// fill report
			JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, connection);
			// export to pdf
			byte[] pdfdata = JasperExportManager.exportReportToPdf(jasperPrint);
			String name = "grand_total_resumes_by_title_report.pdf"; // report filename
			AttachmentInfo ai = buildAttachmentInfo(pdfdata,name);
			
			//send mail
			commServiceImpl = new CommServiceImpl();
			commServiceImpl.sendEmail(null, emailAddrArray, null, "Grand Total Resumes by Title Report", "Grand Total Resumes by Title Report generated.", ai);
			log.info("Report Generated...");
		} catch (Exception e) {
			log.error(" Exception in getTotalResumeCountReport:: "+e.getMessage());
			e.printStackTrace();
		}
		finally{
			if(connection!= null)
			{
				try {
					connection.close();
				} catch (SQLException e) {
					log.error(" Exception in finally of getTotalResumeCountReport :: "+e.getMessage());
					e.printStackTrace();
				}
			}
			commServiceImpl = null;
		}
	}

	//get the connection
	private Connection getConnection() throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		Connection connection = null;
		try {
			//Class.forName("org.postgresql.Driver").newInstance();
			//connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/cgiats", "postgres", "cgi@123");
			//connection = DriverManager.getConnection("jdbc:postgresql://192.168.1.9:5432/cgiats", "postgres", "Atscharter123");
			/*JdbcTemplate template = new JdbcTemplate(dataSource);
			connection = template.getDataSource().getConnection();*/
			InitialContext ctx = new InitialContext();  
			DataSource db = (DataSource) ctx.lookup("java:comp/env/jdbc/cgiatsDS");  
			connection = db.getConnection();
		} catch (Exception e) {
			log.error(" Exception in getConnection:: "+e.getMessage());
			e.printStackTrace();
		}
		return connection;
	}

	public Date getYesterdayStartDate() {
		Date yesterday = null;
		try {
			Calendar cal = Calendar.getInstance();
			DateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy 00:00:00");
			log.info(":: Today's start date is: " + dateFormat.format(cal.getTime()));

			cal.add(Calendar.DATE, -1);
			yesterday = dateFormat.parse(dateFormat.format(cal.getTime()));
		} catch (ParseException e) {
			log.error(" Exception in getYesterdayStartDate:: "+e.getMessage());
			e.printStackTrace();
		}
		log.info(":: Yesterday's start date was: " + yesterday);
		return yesterday;
	}
	
	public Timestamp getYesterdayEndDate() {
		Timestamp yesterday = null;
		try {
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.HOUR, 11);
			cal.set(Calendar.MINUTE, 59);
			cal.set(Calendar.SECOND, 59);
			DateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy hh:mm:ss");
			log.info(":: Today's end date is: " + dateFormat.format(cal.getTime()));

			cal.add(Calendar.DATE, -1);
			yesterday = new Timestamp(cal.getTimeInMillis());
		} catch (Exception e) {
			log.error(" Exception in getYesterdayStartDate:: "+e.getMessage());
			e.printStackTrace();
		}
		log.info(":: Yesterday's end date was: " + yesterday);
		return yesterday;
	}

	private AttachmentInfo buildAttachmentInfo(byte[] doc,String name) {
		AttachmentInfo ai = new AttachmentInfo(name, doc, "application/pdf");
		return ai;
	}
	public static void main(String[] args) {
		//String[] emailAddrArray = new String[]{"myamulapally@charterglobal.com"};
		new FetchMail().getDailyReportData();
	}
}
