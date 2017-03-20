/**
 * 
 */
package com.uralian.cgiats.util;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringTokenizer;

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

import com.uralian.cgiats.model.ClientInfo;
import com.uralian.cgiats.model.SubmittalStatus;

/**
 * @author Parameshwar
 *
 */
public class MonthlySalesExcelGenerator {	

	private HSSFCellStyle	cs		= null;
	private HSSFCellStyle	csBold	= null;


	public byte[] createExcel(List<String> submittalBdms,Map<SubmittalStatus, Integer> map) {

		byte[] output = null;
		try {

			HSSFWorkbook wb = new HSSFWorkbook();
			HSSFSheet sheet = wb.createSheet("Consultant Status List");

			// Setup some styles that we need for the Cells
			setCellStyles(wb);

			setColWidthAndMargin(sheet);

			int rowIndex = 0;
			rowIndex = insertHeaderInfo(sheet, rowIndex);
			rowIndex = insertJODetailInfoDM(sheet, rowIndex, submittalBdms,map);

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
	private int insertJODetailInfoDM(HSSFSheet sheet, int index, List<String> submittalBdms,Map<SubmittalStatus, Integer> status) {
		int rowIndex = 0;

		String strRow = null;
		String columnRow = null;
		System.out.println("entered insertJODetailInfoDM");
		try {
			for (int i = 0; i < submittalBdms.size(); i++) {
				rowIndex = index + i;

				for (SubmittalStatus subEntry : status.keySet())
				{						 
					System.out.println("subEntry.getKey()>>"+subEntry);
					if(subEntry.equals("CONFIRMED")){
						recCONFIRMED = status.get(subEntry);
						recCONFIRMEDTotal = recCONFIRMEDTotal + recCONFIRMED;
					}
					//					  else if((subEntry.getKey()).toString().equals("STARTED")){
					//						  recSTARTED = subEntry.getValue();
					//						  recSTARTEDTotal = recSTARTEDTotal + recSTARTED;	
					//					  }
					//					  else if((subEntry.getKey()).toString().equals("BACKOUT")){
					//						  recBACKOUT = subEntry.getValue();
					//						  recBACKOUTTotal = recBACKOUTTotal + recBACKOUT;
					//					  }

				}		
				System.out.println("submittalBdms.get(i)>>"+submittalBdms.get(i));
				insertDetailInfoDM(sheet, rowIndex, submittalBdms.get(i),recCONFIRMEDTotal,recSTARTEDTotal,recBACKOUTTotal);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rowIndex;
	}

	private int insertDetailInfoDM(HSSFSheet sheet, int index, String dmName,Integer recCONFIRMEDTotal,Integer recSTARTEDTotal,Integer recBACKOUTTotal) {

		HSSFRow row = null;
		HSSFCell c = null;
		SimpleDateFormat df = new SimpleDateFormat("MM-dd-yyyy");
		row = sheet.createRow(index);	

		c = row.createCell(0);		
		c.setCellValue(dmName);
		c.setCellStyle(cs);

		c = row.createCell(1);		
		c.setCellValue(recCONFIRMEDTotal);
		c.setCellStyle(cs);

		c = row.createCell(2);		
		c.setCellValue(recSTARTEDTotal);
		c.setCellStyle(cs);

		c = row.createCell(3);		
		c.setCellValue(recBACKOUTTotal);
		c.setCellStyle(cs);
		
		return index;
	}

	public byte[] createExcel(Map<String, Map<SubmittalStatus, Integer>> submittalStatusByUser,Map<SubmittalStatus, Integer> assignedBdms,Map<String, String> bdmLocations,Map<String,String> dmMonthlyData) {

		byte[] output = null;
		try {

			HSSFWorkbook wb = new HSSFWorkbook();
			HSSFSheet sheet = wb.createSheet("DM Monthly Sales Quotas");

			// Setup some styles that we need for the Cells
			setCellStyles(wb);
			setColWidthAndMargin(sheet);

			int rowIndex = 0;
			rowIndex = insertHeaderInfo(sheet, rowIndex);
			System.out.println("rowIndex:::"+rowIndex);			
			rowIndex = insertSubmittalsInfo(sheet, rowIndex, submittalStatusByUser,assignedBdms,bdmLocations,dmMonthlyData);
			String timeStamp = new SimpleDateFormat("MMddyyyy_HHmmss").format(Calendar.getInstance().getTime());
			String fileName = "attachment;filename=DMMonthlySalesQuotas_"+timeStamp+".xls";
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

	public void setColWidthAndMargin(HSSFSheet sheet) {
		// Set Column Widths
		sheet.setColumnWidth(0, 5000);
		sheet.setColumnWidth(1, 4000);
		sheet.setColumnWidth(2, 5000);
		sheet.setColumnWidth(3, 3000);
		sheet.setColumnWidth(4, 3000);
		//		sheet.setColumnWidth(5, 3500);
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
		c.setCellValue("DM Name");
		c.setCellStyle(csBold);

		c = row.createCell(1);
		c.setCellValue("CONFIRMED");
		c.setCellStyle(csBold);

		c = row.createCell(2);
		c.setCellValue("STARTED");
		c.setCellStyle(csBold);		

		c = row.createCell(3);
		c.setCellValue("BACKOUT");
		c.setCellStyle(csBold);

		c = row.createCell(4);
		c.setCellValue("Sales Quota");
		c.setCellStyle(csBold);

		rowIndex++;
		return rowIndex;
	}
	Map<SubmittalStatus, Integer> submittalTotalsByStatus=new  HashMap<SubmittalStatus, Integer>();
	SubmittalStatus status=null;
	private int insertDetailInfo(HSSFSheet sheet, int index,String recId,String user,Map<SubmittalStatus, Integer> map,String bdmLocation,String strRow,Map<String,String> dmMonthlyData) {		
		HSSFRow row = null;
		HSSFCell c = null;	
		row = sheet.createRow(index);
		c = row.createCell(0);
		c.setCellValue(recId);
		c.setCellStyle(cs);
		Integer count1=null;Integer oldCount=null;
		int newCount=0;
		int totalByUser = 0;
		ArrayList<SubmittalStatus> maps = new ArrayList<SubmittalStatus>(map.keySet());
		String mapDm = dmMonthlyData.get(user);
		if(map!=null && map.size()>0){
			if(!map.entrySet().toString().contains("CONFIRMED")){
				c = row.createCell(1);
				c.setCellValue("");
				c.setCellStyle(cs);
			}
			if(!map.entrySet().toString().contains("STARTED")){
				c = row.createCell(2);
				c.setCellValue("");
				c.setCellStyle(cs);
			}
			if(!map.entrySet().toString().contains("BACKOUT")){
				c = row.createCell(3);
				c.setCellValue("");
				c.setCellStyle(cs);
			}
			for (Map.Entry<SubmittalStatus, Integer> entry1 : map.entrySet())
			{		
				status = entry1.getKey();
				count1 =entry1.getValue();
				if(status!=null && status.equals(SubmittalStatus.CONFIRMED) ){
					c = row.createCell(1);
					c.setCellValue(count1);
					c.setCellStyle(cs);
				}
				else if(status!=null && status.equals(SubmittalStatus.STARTED)){
					c = row.createCell(2);
					c.setCellValue(count1);
					c.setCellStyle(cs);
				}
				else if(status!=null && status.equals(SubmittalStatus.BACKOUT)){
					c = row.createCell(3);
					c.setCellValue(count1);
					c.setCellStyle(cs);
				}
				oldCount = submittalTotalsByStatus.get(status)!=null && !submittalTotalsByStatus.get(status).equals("")?submittalTotalsByStatus.get(status):0;
				newCount = oldCount != null ? oldCount + count1 : count1;
				submittalTotalsByStatus.put(status, newCount);
				totalByUser += count1;
			}
			c = row.createCell(4);
			if(mapDm!=null){
				c.setCellValue(Integer.parseInt(mapDm));
			}else{
				c.setCellValue("");
			}
			c.setCellStyle(cs);

		}
		return index;
	}

	private int insertDetailInfoTotal(HSSFSheet sheet, int index,String columnRow,Map<SubmittalStatus, Integer> submittalTotalsByStatus,int totalSalesCount) {
		HSSFRow row = null;
		HSSFCell c = null;	
		row = sheet.createRow(index+2);

		c = row.createCell(0);
		c.setCellValue("TOTAL:");
		c.setCellStyle(csBold);


		for(Entry<SubmittalStatus, Integer> entry : submittalTotalsByStatus.entrySet()){

			if(status!=null && entry.getKey().equals(SubmittalStatus.CONFIRMED)){
				c = row.createCell(1);
				c.setCellValue(entry.getValue()!=null && !entry.getValue().equals("")?entry.getValue():0);
				c.setCellStyle(csBold);

			}
			if(status!=null && entry.getKey().equals(SubmittalStatus.STARTED)){
				c = row.createCell(2);
				c.setCellValue(entry.getValue()!=null && !entry.getValue().equals("")?entry.getValue():0);
				c.setCellStyle(csBold);

			}
			if(status!=null && entry.getKey().equals(SubmittalStatus.BACKOUT)){
				c = row.createCell(3);
				c.setCellValue(entry.getValue()!=null && !entry.getValue().equals("")?entry.getValue():0);
				c.setCellStyle(csBold);

			}
			c = row.createCell(4);
			c.setCellValue(totalSalesCount);
			c.setCellStyle(csBold);
		}


		return index;
	}
	int totalSalesCount=0;
	private int insertSubmittalsInfo(HSSFSheet sheet, int index, Map<String, Map<SubmittalStatus, Integer>> submittalStatusByUser,Map<SubmittalStatus,Integer> assignedBdms,Map<String, String> bdmLocations,Map<String,String> dmMonthlyData) {
		int rowIndex = 0;

		String strRow = null;

		String columnRow = null;String recName = null;
		try {				
			for (Map.Entry<String,Map<SubmittalStatus, Integer> > entry : submittalStatusByUser.entrySet())
			{
				rowIndex = rowIndex + 1;	
				recName = entry.getKey();			
				insertDetailInfo(sheet, rowIndex,entry.getKey(),recName,submittalStatusByUser.get(recName),null,strRow,dmMonthlyData);
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
