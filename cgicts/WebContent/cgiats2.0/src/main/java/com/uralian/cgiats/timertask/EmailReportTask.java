package com.uralian.cgiats.timertask;

import java.util.List;
import java.util.TimerTask;

public class EmailReportTask extends TimerTask {

	  private List<String> emailAddresses;
  	  private List<String> reportXmlPath;
	  
	  public void setEmailAddresses(List<String> emailAddresses) {
	    this.emailAddresses = emailAddresses;
	  }
	  
	  public void setReportXmlPath(List<String> reportXmlPath) {
		this.reportXmlPath = reportXmlPath;
	  }

	public void run() {
		  
		  String[] emailAddrArray = new String[emailAddresses.size()];
		  if(emailAddresses!=null)
		  {
			  for (int i = 0; i < emailAddresses.size(); i++) {
				  emailAddrArray[i] = emailAddresses.get(i).toString();
			}
			System.out.println("EmailReport.run()...emailAddresses: " + emailAddrArray.length +" configured.");
			System.out.println("ReportXmlPath "+reportXmlPath.size()+" configured ....reportXmlPath: "+reportXmlPath.get(0) );
			
			FetchMail mail = new FetchMail();
			mail.getTimerStart(emailAddrArray,reportXmlPath);
		} else {
			System.err.println("TimerFactory not initialized.....");
		}
	}
}