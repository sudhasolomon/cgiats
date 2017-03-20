package com.uralian.cgiats.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
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

import com.uralian.cgiats.model.SubmittalStatus;

public class RecActivityExcelGenerator {
	private HSSFCellStyle	cs		= null;
	private HSSFCellStyle	csBold	= null;

	public byte[] createRecActivityExcel(Map<String,String> recData, Map<String,Integer> submittalTotalsByUser,Map<String, Map<SubmittalStatus,Integer>> submittalsByRecruiter) {

		byte[] output = null;
		try {

			HSSFWorkbook wb = new HSSFWorkbook();
			HSSFSheet sheet = wb.createSheet("All Recruiters Activity Report");

			// Setup some styles that we need for the Cells
			setCellStyles(wb);
			setColWidthAndMargin(sheet);

			int rowIndex = 0;
			rowIndex = insertHeaderInfo(sheet, rowIndex);
			System.out.println("rowIndex:::"+rowIndex);			
			rowIndex = insertRecSubmittalsInfo(sheet, rowIndex, recData,submittalTotalsByUser,submittalsByRecruiter);
			String timeStamp = new SimpleDateFormat("MMddyyyy_HHmmss").format(Calendar.getInstance().getTime());
			String fileName = "attachment;filename=Activity_Report_of_AllRecruiters_"+timeStamp+".xls";
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
		sheet.setColumnWidth(5, 3500);
		sheet.setColumnWidth(6, 4000);
		sheet.setColumnWidth(7, 3000);
		sheet.setColumnWidth(8, 3000);
		sheet.setColumnWidth(9, 3000);
		sheet.setColumnWidth(10, 3000);
		sheet.setColumnWidth(11, 4000);

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

		int rowIndex = index;
		HSSFRow row = null;
		HSSFCell c = null;

		row = sheet.createRow(rowIndex);
		c = row.createCell(0);
		c.setCellValue("RECRUITER");
		c.setCellStyle(csBold);

		c = row.createCell(1);
		c.setCellValue("LOC");
		c.setCellStyle(csBold);			

		c = row.createCell(2);
		c.setCellValue("SUBMITTED");
		c.setCellStyle(csBold);

		c = row.createCell(3);
		c.setCellValue("DMREJ");
		c.setCellStyle(csBold);

		c = row.createCell(4);
		c.setCellValue("ACCEPTED");
		c.setCellStyle(csBold);

		c = row.createCell(5);
		c.setCellValue("INTERVIEWING");
		c.setCellStyle(csBold);

		c = row.createCell(6);
		c.setCellValue("CONFIRMED");
		c.setCellStyle(csBold);

		c = row.createCell(7);
		c.setCellValue("REJECTED");
		c.setCellStyle(csBold);

		c = row.createCell(8);
		c.setCellValue("STARTED");
		c.setCellStyle(csBold);	

		c = row.createCell(9);
		c.setCellValue("BACKOUT");
		c.setCellStyle(csBold);

		c = row.createCell(10);
		c.setCellValue("OUTOFPROJ");
		c.setCellStyle(csBold);	

		rowIndex++;
		return rowIndex;
	}

	private int insertDetailInfo(HSSFSheet sheet, int index,String recName , String recLoc, String recCount) {		

		HSSFRow row = null;
		HSSFCell c = null;	
		row = sheet.createRow(index);

		c = row.createCell(0);
		c.setCellValue(recName);
		c.setCellStyle(cs);

		c = row.createCell(1);
		c.setCellValue(recLoc);
		c.setCellStyle(cs);
		
		StringTokenizer st = new StringTokenizer(recCount,",");
		int i=2;
		while(st.hasMoreTokens()){
			c = row.createCell(i);
			c.setCellValue(st.nextToken());			   
			c.setCellStyle(cs);
			i =i+1;	
		}
		return index;
	}

	private int insertDetailInfoTotal(HSSFSheet sheet, int index,String columnRow) {
		HSSFRow row = null;
		HSSFCell c = null;	
		row = sheet.createRow(index+1);

		c = row.createCell(0);
		c.setCellValue("TOTAL:");
		c.setCellStyle(csBold);

		StringTokenizer st1 = new StringTokenizer(columnRow,",");

		int i=2;
		while(st1.hasMoreTokens()){
			c = row.createCell(i);
			c.setCellValue(st1.nextToken());			   
			c.setCellStyle(cs);
			i =i+1;	
		}
		return index;
	}

	private int insertRecSubmittalsInfo(HSSFSheet sheet, int index, Map<String,String> recData, Map<String,Integer> submittalTotalsByUser,Map<String, Map<SubmittalStatus,Integer>> submittalsByRecruiter) {

		int rowIndex = 0;
		Integer recBDMREJ = 0;
		Integer recOUTOFPROJ = 0;
		Integer recACCEPTED = 0;
		Integer recSUBMITTED =0;
		Integer recINTERVIEWING= 0;
		Integer recCONFIRMED = 0;
		Integer recSTARTED= 0;
		Integer recBACKOUT = 0;
		Integer recCLOSED = 0;
		Integer recREJECTED = 0;
		Integer recACCEPTEDTotal = 0;
		Integer recSUBMITTEDTotal =0;
		Integer recINTERVIEWINGTotal= 0;
		Integer recCONFIRMEDTotal = 0;
		Integer recSTARTEDTotal= 0;
		Integer recBACKOUTTotal= 0;
		Integer recREJECTEDTotal = 0;
		Integer recBDMREJTotal = 0;
		Integer recOUTOFPROJTotal = 0;
		String strRow = null;
		String recCount = null;
		String recColValue = null;
		int total =0;	
		int count = 0;
		String columnRow = null;
		try {
			for (Map.Entry<String,Map<SubmittalStatus, Integer> > entry : submittalsByRecruiter.entrySet()){
				rowIndex = rowIndex + 1;	
				String recName = entry.getKey();	
				String recLoc = recData.get(recName);
				for(Map.Entry<SubmittalStatus,Integer> submittedEntry : entry.getValue().entrySet()){
						if((submittedEntry.getKey()).toString().equals("SUBMITTED")){
							recSUBMITTED = submittedEntry.getValue();
							recSUBMITTEDTotal = recSUBMITTEDTotal + recSUBMITTED;
						}else if((submittedEntry.getKey()).toString().equals("DMREJ")){
							recBDMREJ = submittedEntry.getValue();
							recBDMREJTotal = recBDMREJTotal + recBDMREJ;
						}else if((submittedEntry.getKey()).toString().equals("ACCEPTED")){
							recACCEPTED = submittedEntry.getValue();
							recACCEPTEDTotal = recACCEPTEDTotal + recACCEPTED;
						}else if((submittedEntry.getKey()).toString().equals("INTERVIEWING")){
							recINTERVIEWING = submittedEntry.getValue();
							recINTERVIEWINGTotal = recINTERVIEWINGTotal + recINTERVIEWING;
						}else if((submittedEntry.getKey()).toString().equals("CONFIRMED")){
							recCONFIRMED = submittedEntry.getValue();
							recCONFIRMEDTotal = recCONFIRMEDTotal + recCONFIRMED;
						}else if((submittedEntry.getKey()).toString().equals("STARTED")){
							recSTARTED = submittedEntry.getValue();
							recSTARTEDTotal = recSTARTEDTotal + recSTARTED;	
						}else if((submittedEntry.getKey()).toString().equals("BACKOUT")){
							recBACKOUT = submittedEntry.getValue();
							recBACKOUTTotal = recBACKOUTTotal + recBACKOUT;
						}else if((submittedEntry.getKey()).toString().equals("REJECTED")){
							recREJECTED = submittedEntry.getValue();
							recREJECTEDTotal = recREJECTEDTotal + recREJECTED;
						}else if((submittedEntry.getKey()).toString().equals("OUTOFPROJ")){
							recOUTOFPROJ = submittedEntry.getValue();
							recOUTOFPROJTotal = recOUTOFPROJTotal + recOUTOFPROJ;
						}
				}
				total = recSUBMITTED+recBDMREJ+recACCEPTED+recINTERVIEWING+recCONFIRMED+recSTARTED+recBACKOUT+recREJECTED+recOUTOFPROJ;
				count = recSUBMITTEDTotal+recBDMREJTotal+recACCEPTEDTotal+recINTERVIEWINGTotal+recCONFIRMEDTotal+recSTARTEDTotal+recBACKOUTTotal+recOUTOFPROJTotal+recREJECTEDTotal;
				strRow = recSUBMITTED+","+recBDMREJ+","+recACCEPTED+","+recINTERVIEWING+","+recCONFIRMED+","+recREJECTED+","+recSTARTED+","+recBACKOUT+","+recOUTOFPROJ+","+total;
				recCount = total +","+recBDMREJ+","+recACCEPTED+","+recINTERVIEWING+","+recCONFIRMED+","+recREJECTED+","+recSTARTED+","+recBACKOUT+","+recOUTOFPROJ;
				insertDetailInfo(sheet, rowIndex,recName,recLoc,recCount);
			}
			columnRow =  recSUBMITTEDTotal+","+recBDMREJTotal+","+recACCEPTEDTotal+","+recINTERVIEWINGTotal+","+recCONFIRMEDTotal+","+recREJECTEDTotal+","+recSTARTEDTotal+","+recBACKOUTTotal+","+recOUTOFPROJTotal+","+count;
			recColValue = count+","+recBDMREJTotal+","+recACCEPTEDTotal+","+recINTERVIEWINGTotal+","+recCONFIRMEDTotal+","+recREJECTEDTotal+","+recSTARTEDTotal+","+recBACKOUTTotal+","+recOUTOFPROJTotal;
			insertDetailInfoTotal(sheet, rowIndex,recColValue);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rowIndex;
	}
}
