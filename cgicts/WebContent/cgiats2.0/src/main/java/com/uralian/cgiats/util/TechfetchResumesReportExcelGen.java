package com.uralian.cgiats.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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


public class TechfetchResumesReportExcelGen {

	private HSSFCellStyle	cs		= null;
	private HSSFCellStyle	csBold	= null;
	
	
	
	public void setColWidthAndMargin(HSSFSheet sheet) {
		// Set Column Widths
		sheet.setColumnWidth(0, 8000);
		sheet.setColumnWidth(1, 3000);
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

	

	
	public byte[] createExcel(Map<String, Integer> resumeStatsByUser,List<String> portalEmails) {

		byte[] output = null;
		try {

			HSSFWorkbook wb = new HSSFWorkbook();
			HSSFSheet sheet = wb.createSheet("Techfetch Resumes Report");

			// Setup some styles that we need for the Cells
			setCellStyles(wb);

			setColWidthAndMargin(sheet);

			int rowIndex = 0;

			rowIndex = insertHeaderInfo1(sheet, rowIndex);
			rowIndex = insertTechfetchDetailInfo1(sheet, rowIndex,resumeStatsByUser,portalEmails);

			// Write the Excel file
			//FileOutputStream fileOut = new FileOutputStream("c:/reports/myReport2.xls"); wb.write(fileOut);
			String timeStamp = new SimpleDateFormat("MMddyyyy_HHmmss").format(Calendar.getInstance().getTime());
			String fileName = "attachment;filename=TechfetchIdResumesReport"+timeStamp+".xls";
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


	private int insertHeaderInfo1(HSSFSheet sheet, int index) {
		int rowIndex = index;
		HSSFRow row = null;
		HSSFCell c = null;

		row = sheet.createRow(rowIndex);
		
		c = row.createCell(0);
		c.setCellValue("User");
		c.setCellStyle(csBold);

		c = row.createCell(1);
		c.setCellValue("Resumes");
		c.setCellStyle(csBold);
		
		rowIndex++;
		return rowIndex;
	}
	
	private int insertTechfetchDetailInfo1(HSSFSheet sheet, int index,Map<String, Integer> resumeStatsByUser,List<String> portalEmails) {
		int rowIndex = 0;
		int i=0;
		try {
			int resumeTotal = 0;
			Iterator ie =portalEmails.iterator();
			String name=null;
			while(ie.hasNext()){
				for (Integer count : resumeStatsByUser.values()){
					rowIndex=index+i;
					name=(String)ie.next();
					resumeTotal += count;
					insertMonsterDetailInfo1(sheet, rowIndex,name,count);
					i++;
				}
			}
				
			insertFooterInfo1(sheet, i+1,resumeTotal);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return rowIndex;
	}
	
	private int insertMonsterDetailInfo1(HSSFSheet sheet, int index, String userId,int count) {

		HSSFRow row = null;
		HSSFCell c = null;
		row = sheet.createRow(index);	

		c = row.createCell(0);		
		c.setCellValue(userId);
		c.setCellStyle(cs);
		
		c = row.createCell(1);		
		c.setCellValue(count);
		c.setCellStyle(cs);

		return index;
	}
	private int insertFooterInfo1(HSSFSheet sheet,int index, int resumeTotal) {
		int rowIndex = index;
		HSSFRow row = null;
		HSSFCell c = null;

		row = sheet.createRow(rowIndex);
		c = row.createCell(0);
		c.setCellValue("Total:");
		c.setCellStyle(csBold);
		
		c = row.createCell(1);
		c.setCellValue(resumeTotal);
		c.setCellStyle(csBold);

		return rowIndex;
	}
}
