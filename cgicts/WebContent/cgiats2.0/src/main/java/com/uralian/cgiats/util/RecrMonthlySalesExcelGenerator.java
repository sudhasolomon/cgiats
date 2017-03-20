/**
 * 
 */
package com.uralian.cgiats.util;


import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.faces.context.FacesContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.springframework.beans.factory.annotation.Autowired;

import com.uralian.cgiats.dao.UserDao;
import com.uralian.cgiats.model.JobOrderSearchDto;
import com.uralian.cgiats.model.SalesQuotaView;
import com.uralian.cgiats.model.Submittal;
import com.uralian.cgiats.model.SubmittalStatus;
import com.uralian.cgiats.model.User;
import com.uralian.cgiats.service.ServiceException;
import com.uralian.cgiats.service.SubmittalService;
import com.uralian.cgiats.service.UserService;

/**
 * @author Parameshwar
 *
 */
public class RecrMonthlySalesExcelGenerator implements Serializable {	

	/**
	 * 
	 */
	private static final long serialVersionUID = -3622948571330871479L;
	private HSSFCellStyle	cs		= null;
	private HSSFCellStyle	csBold	= null;
	
	@Autowired
	private transient UserService userService;
	
	
	@Autowired
	private UserDao userDao;
	
	@Autowired
	private transient SubmittalService submittalService;

	public byte[] createExcel(List<SalesQuotaView> submittalBdms,Map<String, Integer> totalCounts) {

		byte[] output = null;
		try {
			HSSFWorkbook wb = new HSSFWorkbook();
			HSSFSheet sheet = wb.createSheet("Consultant Status List");
			// Setup some styles that we need for the Cells
			setCellStyles(wb);

			setColWidthAndMargin(sheet);

			int rowIndex = 0;
			rowIndex = insertHeaderInfo(sheet, rowIndex);
			rowIndex = insertJODetailInfoDM(sheet, rowIndex, submittalBdms,totalCounts);

			// Write the Excel file
			//FileOutputStream fileOut = new FileOutputStream("c:/reports/myReport2.xls"); wb.write(fileOut);
			String timeStamp = new SimpleDateFormat("MMddyyyy_HHmmss").format(Calendar.getInstance().getTime());
			String fileName = "attachment;filename=DMMS"+timeStamp+".xls";
			output = wb.getBytes();
			FacesContext context = FacesContext.getCurrentInstance();
			HttpServletResponse res = (HttpServletResponse) context.getExternalContext().getResponse();
			res.setContentType("application/vnd.ms-excel");
			res.setHeader("Content-disposition", fileName);
			ServletOutputStream out = res.getOutputStream();
			wb.write(out);
			out.flush();
			out.close();
			FacesContext.getCurrentInstance().responseComplete();
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return output;
	}
	Integer recCONFIRMED = 0;
	Integer recSTARTED= 0;
	Integer recBACKOUT = 0;
	Integer recCONFIRMEDTotal = 0;
	Integer recSTARTEDTotal= 0;
	Integer recBACKOUTTotal= 0;
	private int insertJODetailInfoDM(HSSFSheet sheet, int index, List<SalesQuotaView> submittalBdms,Map<String,Integer> totalCounts) {
		
		int rowIndex = 0;

		String strRow = null;
		int total =0;	
		int count = 0;
		String columnRow = null;
		try {
			for (int i = 0; i < submittalBdms.size(); i++) 
			{
				rowIndex = index + i;	
				insertDetailInfoDM(sheet, rowIndex, submittalBdms.get(i));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		insertFooter(sheet, rowIndex+1, totalCounts);
		return rowIndex;
	}

	private void insertFooter(HSSFSheet sheet, int rowIndex,Map<String, Integer> totalCounts) 
	{
		HSSFRow row = sheet.createRow(rowIndex);	
		HSSFCell c = null;
		
		c = row.createCell(0);		
		c.setCellValue("Total");
		c.setCellStyle(cs);

		c = row.createCell(1);		
		c.setCellValue(totalCounts.get("submittedCount"));
		c.setCellStyle(cs);

		c = row.createCell(2);		
		c.setCellValue(totalCounts.get("dmRejectedCount"));
		c.setCellStyle(cs);

		c = row.createCell(3);		
		c.setCellValue(totalCounts.get("acceptedCount"));
		c.setCellStyle(cs);

		c = row.createCell(4);		
		c.setCellValue(totalCounts.get("interviewingCount"));
		c.setCellStyle(cs);
		
		c = row.createCell(5);		
		c.setCellValue(totalCounts.get("confirmedCount"));
		c.setCellStyle(cs);
		
		c = row.createCell(6);		
		c.setCellValue(totalCounts.get("rejectedCount"));
		c.setCellStyle(cs);
		
		c = row.createCell(7);		
		c.setCellValue(totalCounts.get("totalAcceptedPerc")+"%");
		c.setCellStyle(cs);
		
		c = row.createCell(8);		
		c.setCellValue(totalCounts.get("totalInterviewPerc")+"%");
		c.setCellStyle(cs);
		
		c = row.createCell(9);		
		c.setCellValue(totalCounts.get("salesQuotaCount"));
		c.setCellStyle(cs);
		
	}

	private int insertDetailInfoDM(HSSFSheet sheet, int index,SalesQuotaView salesQuotaView) {

		HSSFRow row = null;
		HSSFCell c = null;
		SimpleDateFormat df = new SimpleDateFormat("MM-dd-yyyy");
		row = sheet.createRow(index);	

		c = row.createCell(0);		
		c.setCellValue(salesQuotaView.getRecruiterName());
		c.setCellStyle(cs);

		c = row.createCell(1);		
		c.setCellValue(salesQuotaView.getSubmittedCount());
		c.setCellStyle(cs);

		c = row.createCell(2);		
		c.setCellValue(salesQuotaView.getDmrejCount());
		c.setCellStyle(cs);

		c = row.createCell(3);		
		c.setCellValue(salesQuotaView.getAcceptedCount());
		c.setCellStyle(cs);
		
		c = row.createCell(4);		
		c.setCellValue(salesQuotaView.getInterviewingCount());
		c.setCellStyle(cs);
		
		c = row.createCell(5);		
		c.setCellValue(salesQuotaView.getConfirmedCount());
		c.setCellStyle(cs);
		
		c = row.createCell(6);		
		c.setCellValue(salesQuotaView.getRejectedCount());
		c.setCellStyle(cs);
		
		c = row.createCell(7);		
		c.setCellValue(salesQuotaView.getAcceptedPerc()+"%");
		c.setCellStyle(cs);
		
		c = row.createCell(8);		
		c.setCellValue(salesQuotaView.getInterPerc()+"%");
		c.setCellStyle(cs);
		
		c = row.createCell(9);		
		c.setCellValue(salesQuotaView.getSalesquota());
		c.setCellStyle(cs);
		return index;
	}

	/*public byte[] createExcel(List<SalesQuotaView> submittalStatusByUser,Map<SubmittalStatus, Integer> assignedBdms,Map<String, String> bdmLocations,Map<String,String> dmMonthlyData,Date submittalFrom,Date submittalTo,Map<String, Map<SubmittalStatus, Integer>> submittalStatusByUser1) {

		byte[] output = null;
		try {

			HSSFWorkbook wb = new HSSFWorkbook();
			HSSFSheet sheet = wb.createSheet("Rcruiter Monthly Sales Quotas");

			// Setup some styles that we need for the Cells
			setCellStyles(wb);
			setColWidthAndMargin(sheet);

			int rowIndex = 0;
			rowIndex = insertHeaderInfo(sheet, rowIndex);
			rowIndex = insertSubmittalsInfo(sheet, rowIndex, submittalStatusByUser,assignedBdms,bdmLocations,dmMonthlyData,submittalFrom,submittalTo,submittalStatusByUser1);
			String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
			String fileName = "attachment;filename=RecruiterMonthlyQuotas_"+timeStamp+".xls";
			output = wb.getBytes();
			System.out.println("Workbook length in bytes: "+output.length);
			FacesContext context = FacesContext.getCurrentInstance();
			HttpServletResponse res = (HttpServletResponse) context.getExternalContext().getResponse();
			res.setContentType("application/vnd.ms-excel");
			res.setHeader("Content-disposition", fileName);
			ServletOutputStream out = res.getOutputStream();
			wb.write(out);
			out.flush();
			out.close();
			FacesContext.getCurrentInstance().responseComplete();

		} catch (Exception e) {
			System.out.println("Exception in exporting all records to excel :: " + e);
			e.printStackTrace();
		} 
		return output;
	}
*/
	public void setColWidthAndMargin(HSSFSheet sheet) {
		// Set Column Widths
		sheet.setColumnWidth(0, 5000);
		sheet.setColumnWidth(1, 4000);
		sheet.setColumnWidth(2, 3000);
		sheet.setColumnWidth(3, 4000);
		sheet.setColumnWidth(4, 4000);
		sheet.setColumnWidth(5, 4500);
		sheet.setColumnWidth(6, 7500);
		sheet.setColumnWidth(7, 7200);
		sheet.setColumnWidth(8, 7200);
		sheet.setColumnWidth(9, 4500);
		// Setup the Page margins - Left, Right, Top and Bottom
		sheet.setMargin(HSSFSheet.LeftMargin, 0.25);
		sheet.setMargin(HSSFSheet.RightMargin, 0.25);
		sheet.setMargin(HSSFSheet.TopMargin, 0.75);
		sheet.setMargin(HSSFSheet.BottomMargin, 0.75);
	}

	private void setCellStyles(HSSFWorkbook wb) {

		// font size 10
		HSSFFont f = wb.createFont();
		f.setFontHeightInPoints((short) 10);

		// Simple style
		cs = wb.createCellStyle();
		cs.setFont(f);

		// Bold Fond
		HSSFFont bold = wb.createFont();
		bold.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		bold.setFontHeightInPoints((short) 10);

		// Bold style
		csBold = wb.createCellStyle();
		csBold.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		csBold.setBorderRight(HSSFCellStyle.BORDER_THIN);
		csBold.setBottomBorderColor(HSSFColor.BLACK.index);
		csBold.setFont(bold);

		cs.setBorderTop((short) 1);
		cs.setBorderBottom((short) 1);
		cs.setBorderLeft((short) 1);
		cs.setBorderRight((short) 1);
		cs.setFont(f);
	}

	
	private int insertHeaderInfo(HSSFSheet sheet, int index) {
		System.out.println("entered insertHeaderInfo");
		int rowIndex = index;
		HSSFRow row = null;
		HSSFCell c = null;

		row = sheet.createRow(rowIndex);
		c = row.createCell(0);
		c.setCellValue("Recruiter Name");
		c.setCellStyle(csBold);

		c = row.createCell(1);
		c.setCellValue("SUBMITTED");
		c.setCellStyle(csBold);

		c = row.createCell(2);
		c.setCellValue("DMREJ");
		c.setCellStyle(csBold);		

		c = row.createCell(3);
		c.setCellValue("ACCEPTED");
		c.setCellStyle(csBold);	
		
		c = row.createCell(4);
		c.setCellValue("INTERVIEWING");
		c.setCellStyle(csBold);	
		
		c = row.createCell(5);
		c.setCellValue("CONFIRMED");
		c.setCellStyle(csBold);

		c = row.createCell(6);
		c.setCellValue("REJECTED");
		c.setCellStyle(csBold);
		
		c = row.createCell(7);
		c.setCellValue("ACCEPTED%SUBMITTED");
		c.setCellStyle(csBold);	
		
		c = row.createCell(8);
		c.setCellValue("INTERVIEWING%ACCEPTED");
		c.setCellStyle(csBold);	
		
		c = row.createCell(9);
		c.setCellValue("Sales Quota");
		c.setCellStyle(csBold);

		rowIndex++;
		return rowIndex;
	}
	Map<SubmittalStatus, Integer> submittalTotalsByStatus=new  HashMap<SubmittalStatus, Integer>();
	private Map<String, Map<SubmittalStatus, Integer>> submittalStatusByUser1;
	private List<String> submittalBdms1;
	private List<String> recs;
	private Map<String, Integer> submittalTotalsByUser1;
	private Map<SubmittalStatus, Integer> submittalTotalsByStatus1;
	private int submittalTotal1;
	Map<String, Map<SubmittalStatus, Integer>> submittalStatusByUserData = new HashMap<String, Map<SubmittalStatus,Integer>>();
	SubmittalStatus status=null;
	private int insertDetailInfo(HSSFSheet sheet, int index,String recId,String user,Map<SubmittalStatus, Integer> map,String bdmLocation,String strRow,Map<String,String> dmMonthlyData,Date submittalFrom,Date submittalTo,Map<String, Map<SubmittalStatus, Integer>> submittalStatusByUser1) {		
		HSSFRow row = null;
		int submittedCount=0;
		int acceptedCount=0;
		int interviewCount=0;
		int finalCount=0;
		int finalIntrAccpCount=0;
		HSSFCell c = null;	
		row = sheet.createRow(index);
		c = row.createCell(0);
		c.setCellValue(recId);
		c.setCellStyle(cs);
		Integer count1=null;Integer oldCount=null;
		int newCount=0;
		int totalByUser = 0;
		System.out.println("recId>>"+user);
		ArrayList<SubmittalStatus> maps = new ArrayList<SubmittalStatus>(map.keySet());
		String mapDm = dmMonthlyData.get(user);
		if(map!=null && map.size()>0){
			if(!map.entrySet().toString().contains("SUBMITTED")){
				c = row.createCell(1);
				c.setCellValue("");
				c.setCellStyle(cs);
			}
			if(!map.entrySet().toString().contains("DMREJ")){
				c = row.createCell(2);
				c.setCellValue("");
				c.setCellStyle(cs);
			}
			if(!map.entrySet().toString().contains("ACCEPTED")){
				c = row.createCell(3);
				c.setCellValue("");
				c.setCellStyle(cs);
			}
			if(!map.entrySet().toString().contains("INTERVIEWING")){
				c = row.createCell(4);
				c.setCellValue("");
				c.setCellStyle(cs);
			}
			if(!map.entrySet().toString().contains("CONFIRMED")){
				c = row.createCell(5);
				c.setCellValue("");
				c.setCellStyle(cs);
			}
			if(!map.entrySet().toString().contains("REJECTED")){
				c = row.createCell(6);
				c.setCellValue("");
				c.setCellStyle(cs);
			}
			for (Map.Entry<SubmittalStatus, Integer> entry1 : map.entrySet())
			{		
				status = entry1.getKey();
				count1 =entry1.getValue();
				//submitted, DMRej, Confirmed, CLRej, 
				if(status!=null && status.equals(SubmittalStatus.SUBMITTED)){
					c = row.createCell(1);
					c.setCellValue(count1);
					c.setCellStyle(cs);
				}
				else if(status!=null && status.equals(SubmittalStatus.DMREJ)){
					c = row.createCell(2);
					c.setCellValue(count1);
					c.setCellStyle(cs);
				}
				else if(status!=null && status.equals(SubmittalStatus.ACCEPTED)){
					c = row.createCell(3);
					c.setCellValue(count1);
					c.setCellStyle(cs);
				}
				else if(status!=null && status.equals(SubmittalStatus.INTERVIEWING)){
					c = row.createCell(4);
					c.setCellValue(count1);
					c.setCellStyle(cs);
				}
				else if(status!=null && status.equals(SubmittalStatus.CONFIRMED)){
					c = row.createCell(5);
					c.setCellValue(count1);
					c.setCellStyle(cs);
				}
				else if(status!=null && status.equals(SubmittalStatus.REJECTED)){
					c = row.createCell(6);
					c.setCellValue(count1);
					c.setCellStyle(cs);
				}
				oldCount = submittalTotalsByStatus.get(status)!=null && !submittalTotalsByStatus.get(status).equals("")?submittalTotalsByStatus.get(status):0;
				newCount = oldCount != null ? oldCount + count1 : count1;
				submittalTotalsByStatus.put(status, newCount);
//				submittedCount=submittalTotalsByUser1.get(user);
				if(status!=null && status.equals(SubmittalStatus.SUBMITTED)){
					submittedCount=count1;
				}
				if(status!=null && status.equals(SubmittalStatus.ACCEPTED)){
					acceptedCount=count1;
				}
				if(status!=null && status.equals(SubmittalStatus.INTERVIEWING)){
					interviewCount=count1;
				}
				if(submittedCount!=0){
					float f=(float)acceptedCount/submittedCount;
					Float f2=f * 100.0f;
					finalCount= f2.intValue();
				}
				if(acceptedCount!=0){
//					System.out.println("interviewCount/acceptedCount>>>"+interviewCount/acceptedCount);
					float f=(float)interviewCount/acceptedCount;
					Float f2=f * 100.0f;
					finalIntrAccpCount= f2.intValue();
					//						log.info("finalIntrAccpCount>>"+finalIntrAccpCount);
				}
//				percentageIntrAccp.put(user, finalIntrAccpCount);
				if(recId.equalsIgnoreCase(user)){
					c = row.createCell(8);
					c.setCellValue(finalIntrAccpCount+"%");
					c.setCellStyle(cs);
					}
				if(recId.equalsIgnoreCase(user)){
					c = row.createCell(7);
					c.setCellValue(finalCount+"%");
					c.setCellStyle(cs);
					}
//				percentageSubAccp.put(user, finalCount);
				totalByUser += count1;
			}
			c = row.createCell(9);
			if(mapDm!=null){
				c.setCellValue(Integer.parseInt(mapDm));
			}else{
				c.setCellValue("");
			}
			c.setCellStyle(cs);
			
		}
		
		int totalSubmitted=0;
		int totalAccepted=0;int totalInterviewing=0;
		for(SubmittalStatus submittalStatus :submittalTotalsByStatus.keySet() ){
			if(submittalStatus.equals(SubmittalStatus.SUBMITTED))
				totalSubmitted=submittalTotalsByStatus.get(submittalStatus);
			if(submittalStatus.equals(SubmittalStatus.ACCEPTED))
				totalAccepted=submittalTotalsByStatus.get(submittalStatus);
			if(submittalStatus.equals(SubmittalStatus.INTERVIEWING))
				totalInterviewing=submittalTotalsByStatus.get(submittalStatus);
			if(totalSubmitted!=0)
			{
				float f=(float)totalAccepted/totalSubmitted;
				Float fTotal=f * 100.0f;
				totPercentage= fTotal.intValue();
			}
			if(totalAccepted!=0)
			{
				float f=(float)totalInterviewing/totalAccepted;
				Float fTotal=f * 100.0f;
				totIntrAccPercentage= fTotal.intValue();
			}
		}
		return index;
	}
	int totPercentage=0;
	int totIntrAccPercentage=0;
	private int insertDetailInfoTotal(HSSFSheet sheet, int index,String columnRow,Map<SubmittalStatus, Integer> submittalTotalsByStatus,int totalSalesCount) {
		HSSFRow row = null;
		HSSFCell c = null;	
		row = sheet.createRow(index+2);

		c = row.createCell(0);
		c.setCellValue("TOTAL:");
		c.setCellStyle(csBold);
		
		/*c = row.createCell(1);
		c.setCellValue(submittalTotal1);
		c.setCellStyle(csBold);*/
		for(Entry<SubmittalStatus, Integer> entry : submittalTotalsByStatus.entrySet()){
		
		if(status!=null && entry.getKey().equals(SubmittalStatus.SUBMITTED)){
			c = row.createCell(1);
			c.setCellValue(entry.getValue()!=null && !entry.getValue().equals("")?entry.getValue():0);
			c.setCellStyle(csBold);

		}
		if(status!=null && entry.getKey().equals(SubmittalStatus.DMREJ)){
			c = row.createCell(2);
			c.setCellValue(entry.getValue()!=null && !entry.getValue().equals("")?entry.getValue():0);
			c.setCellStyle(csBold);

		}
		if(status!=null && entry.getKey().equals(SubmittalStatus.ACCEPTED)){
			c = row.createCell(3);
			c.setCellValue(entry.getValue()!=null && !entry.getValue().equals("")?entry.getValue():0);
			c.setCellStyle(csBold);

		}
		if(status!=null && entry.getKey().equals(SubmittalStatus.INTERVIEWING)){
			c = row.createCell(4);
			c.setCellValue(entry.getValue()!=null && !entry.getValue().equals("")?entry.getValue():0);
			c.setCellStyle(csBold);

		}
		if(status!=null && entry.getKey().equals(SubmittalStatus.CONFIRMED)){
			c = row.createCell(5);
			c.setCellValue(entry.getValue()!=null && !entry.getValue().equals("")?entry.getValue():0);
			c.setCellStyle(csBold);

		}
		if(status!=null && entry.getKey().equals(SubmittalStatus.REJECTED)){
			c = row.createCell(6);
			c.setCellValue(entry.getValue()!=null && !entry.getValue().equals("")?entry.getValue():0);
			c.setCellStyle(csBold);

		}
		c = row.createCell(7);
		c.setCellValue(totPercentage+"%");
		c.setCellStyle(csBold);
		
		c = row.createCell(8);
		c.setCellValue(totIntrAccPercentage+"%");
		c.setCellStyle(csBold);
		
		c = row.createCell(9);
		c.setCellValue(totalSalesCount);
		c.setCellStyle(csBold);
		}
		
		
		return index;
	}
int totalSalesCount=0;
	private int insertSubmittalsInfo(HSSFSheet sheet, int index, Map<String, Map<SubmittalStatus, Integer>> submittalStatusByUser,Map<SubmittalStatus,Integer> assignedBdms,Map<String, String> bdmLocations,Map<String,String> dmMonthlyData,Date submittalFrom,Date submittalTo,Map<String, Map<SubmittalStatus, Integer>> submittalStatusByUser1) {
		int rowIndex = 0;
	
		String strRow = null;

		String columnRow = null;String recName = null;
		try {				
			for (Map.Entry<String,Map<SubmittalStatus, Integer> > entry : submittalStatusByUser.entrySet())
			{
				rowIndex = rowIndex + 1;	
				recName = entry.getKey();			
				insertDetailInfo(sheet, rowIndex,entry.getKey(),recName,submittalStatusByUser.get(recName),null,strRow,dmMonthlyData,submittalFrom,submittalTo,submittalStatusByUser1);
				String mapDm = dmMonthlyData.get(recName);
				int a=mapDm!=null?Integer.parseInt(mapDm):0;
				totalSalesCount=totalSalesCount+a;
				
				
				
			}
			insertDetailInfoTotal(sheet, rowIndex,columnRow,submittalTotalsByStatus,totalSalesCount);
		} catch (Exception e) {
			e.printStackTrace();
		}	

		return rowIndex;
	}
}
