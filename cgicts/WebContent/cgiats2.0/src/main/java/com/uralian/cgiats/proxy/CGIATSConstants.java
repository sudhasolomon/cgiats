package com.uralian.cgiats.proxy;

import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.uralian.cgiats.model.OfferLetterStatus;

/**
 * This class encapsulates HTTP keys used by various components.
 * 
 * @author Jorge
 */
public class CGIATSConstants
{
	public static final String HOST_KEY = "Host";
	public static final String USER_AGENT_KEY = "User-Agent";
	public static final String ACCEPT_KEY = "Accept";
	public static final String ACCEPT_LANGUAGE_KEY = "Accept-Language";
	public static final String ACCEPT_ENCODING_KEY = "Accept-Encoding";
	public static final String CONNECTION_KEY = "Connection";
	public static final String PROXY_CONNECTION_KEY = "Proxy-Connection";
	public static final String COOKIE_KEY = "Cookie";
	public static final String CONTENT_TYPE_KEY = "Content-Type";
	public static final String CONTENT_DISPOSITION_KEY = "Content-Disposition";
	/*For Prod Build*/
	
	public static final String WEBSERVICE_URI = "http://182.18.158.28:8181/cgiats/wsPortalCandidate?xsd=1";
	public static String PROD_MAIL = "cgiats@charterglobal.com";	
	public static String TEST_MAIL = "hsambalampalli@charterglobal.com";
	public static  String [] SCHEDULED_MAIL =	{"kreaves@charterglobal.com" , "hsambalampalli@charterglobal.com" ,"vpradeep@charterglobal.com" , "cgiatsteam@charterglobal.com" , "sponangi@charterglobal.com"};
	public static  String[] HYD_MAILS={"hydreqteam@charterglobal.com","kreaves@charterglobal.com","vpradeep@charterglobal.com"};
	public static String[] PUNE_MAILS={"punereqteam@charterglobal.com","kreaves@charterglobal.com","vpradeep@charterglobal.com"};
	public static  String[] NOIDA_MAILS={"noidareqteam@charterglobal.com","kreaves@charterglobal.com","vpradeep@charterglobal.com"};
	public static  String[] OFFERLETTER_MAILS={"hrteam@charterglobal.com"};
	
	
	public static final String WS_USER_NAME="atsweb";
	public static final String WS_PASSWORD="cgiservice";
	
	public static final String[] DM_AND_ADM_NAMES={"Ajay","Arjun","Devasya","Sachin","Saurabh","Ismail","Akash","Anuj","Shailendra"};
	public static final String[] ADD_SACHIN_NAMES={"Rocky","Vincent","Saritha","Munawer"};
	public static final String SACHIN_NAME="Sachin";
	public static  String []  REC_HEAD = {};
	
	public static  String  LUCENE_PATH;
	
	
	/*For QA Build*/
	/*
	public static  String PROD_MAIL = "atsqa@charterglobal.com";	
	public static  String TEST_MAIL = "rgovvala@charterglobal.com";
	public static  String [] SCHEDULED_MAIL ={"rgovvala@charterglobal.com"};
	public static  String[] HYD_MAILS={"rgovvala@charterglobal.com"};
	public static  String[] PUNE_MAILS={"rgovvala@charterglobal.com"};
	public static  String[] NOIDA_MAILS={"rgovvala@charterglobal.com"};
	public static  String[] OFFERLETTER_MAILS={"rgovvala@charterglobal.com"};
	
	
	public static final String[] DM_AND_ADM_NAMES={"rajaDM","raghavaDM","rajaADM","DM"};
	public static final String[] ADD_SACHIN_NAMES={"raghavaADM"};
	public static final String SACHIN_NAME="raghavaDM";
	
	public static final String WS_USER_NAME="atsqa";
	public static final String WS_PASSWORD="cgiats";
	
	public static final String WEBSERVICE_URI= "http://192.168.1.85:8181/cgiats/wsPortalCandidate?xsd=1";
//	
//	public static final String[] DM_AND_ADM_NAMES={"JBDM","SDM","JBADM","JyoDM"};
//	public static final String[] ADD_SACHIN_NAMES={"jyothiadm1"};
//	public static final String SACHIN_NAME="SDM";
	
	public static final String[] DM_AND_ADM_NAMES={"Ajay","Arjun","Devasya","Sachin","Saurabh","Ismail","Akash","Anuj","Shailendra"};
	public static final String[] ADD_SACHIN_NAMES={"Rocky","Vincent"};
	public static final String SACHIN_NAME="Sachin";*/
	// Added BY Sreenath
		// It Is Load The mailConfiguration.properties File
		public final static String PROPS_FILE = "/mailConfig.properties";
		private static Properties props;
		private static final Logger LOG = Logger.getLogger(CGIATSConstants.class);

		static {
			try {
				if (props == null) {
					props = new Properties();
					InputStream inputStream = CGIATSConstants.class
							.getResourceAsStream(PROPS_FILE);

					props.load(inputStream);
					PROD_MAIL = props.getProperty("PROD_MAIL");
					TEST_MAIL=props.getProperty("TEST_MAIL");
				String	scheduleMails=props.getProperty("SCHEDULED_MAIL");
				if(scheduleMails!=null&&scheduleMails.length()>0){
					SCHEDULED_MAIL=scheduleMails.split(",");
				}
				String hydMails=props.getProperty("HYD_MAILS");
				if(hydMails!=null&&hydMails.length()>0){
					HYD_MAILS=hydMails.split(",");
				}
				String puneMails=props.getProperty("PUNE_MAILS");
				if(puneMails!=null&&puneMails.length()>0){
					PUNE_MAILS=puneMails.split(",");
				}
				String noidaMails=props.getProperty("NOIDA_MAILS");
				if(noidaMails!=null&&noidaMails.length()>0){
					NOIDA_MAILS=noidaMails.split(",");
				}
				String offerLetterMails=props.getProperty("OFFERLETTER_MAILS");
				if(offerLetterMails!=null&&offerLetterMails.length()>0){
					OFFERLETTER_MAILS=offerLetterMails.split(",");
				}
				String recHeadMails=props.getProperty("REC_HEAD");
				if(recHeadMails!=null&&recHeadMails.length()>0){
					REC_HEAD=recHeadMails.split(",");
				}
				
				String lucenepath=props.getProperty("LUCENE_PATH");
				if(lucenepath!=null&&lucenepath.length()>0){
					LUCENE_PATH=lucenepath;
				}
				
				
					LOG.info("*************************PROD_MAIL*************************" + PROD_MAIL);
					LOG.info("*************************OFFERLETTER_MAILS*****************"+OFFERLETTER_MAILS[0]);

				}
			} catch (Exception e) {
				LOG.error("Error", e);
			}

		}

		public static void main(String args[]) {

		}
}
