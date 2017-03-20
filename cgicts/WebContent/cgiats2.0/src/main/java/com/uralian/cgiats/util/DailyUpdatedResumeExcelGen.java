package com.uralian.cgiats.util;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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



/**
 * @author Rajashekhar
 *
 */

public class DailyUpdatedResumeExcelGen {
	
	private HSSFCellStyle	cs		= null;
	private HSSFCellStyle	csBold	= null;
	long resumesTotal=0;

	public byte[] createExcel(Map<String, Integer> resumeStatsofUser) {
		// TODO Auto-generated method stub
		
		
		byte[] output = null;
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet("Daily Updated Resumes");
		
		// Setup some styles that we need for the Cells
					setCellStyles(wb);

					setColWidthAndMargin(sheet);

					int rowIndex = 0;
					
					rowIndex = insertHeaderInfo(sheet, rowIndex);
					rowIndex = insertData(sheet, rowIndex,resumeStatsofUser);
					insertFooterInfo(sheet,rowIndex);
					
					
					String timeStamp = new SimpleDateFormat("MMddyyyy_HHmmss").format(Calendar.getInstance().getTime());
					String fileName = "attachment;filename=dailyUpdatedResumes"+timeStamp+".xls";
					output = wb.getBytes();
					FacesContext context = FacesContext.getCurrentInstance();
					HttpServletResponse res = (HttpServletResponse) context.getExternalContext().getResponse();
					res.setContentType("application/vnd.ms-excel");
					res.setHeader("Content-disposition", fileName);
					ServletOutputStream out;
					try {
						out = res.getOutputStream();
						wb.write(out);
						out.flush();
						out.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					FacesContext.getCurrentInstance().responseComplete();
		return output;
	}
	
	

	private int insertData(HSSFSheet sheet, int rowIndex,Map<String, Integer> resumeStatsofUser) {
		int rowNumber=1;
		try {
                 for (Map.Entry<String,Integer> entry : resumeStatsofUser.entrySet()) {
                 String key = entry.getKey();
                 Integer value = entry.getValue();
                 resumesTotal+=value;
         		   insertRow(sheet, rowNumber,key,value);
         		  rowNumber++;
                   }
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rowNumber;
	}



	private int insertRow(HSSFSheet sheet, int index, String user,Integer resumeCount) {

		HSSFRow row = null;
		HSSFCell c = null;
		row = sheet.createRow(index);	

		c = row.createCell(0);		
		c.setCellValue(user);
		c.setCellStyle(cs);

		c = row.createCell(1);		
		c.setCellValue(resumeCount);
		c.setCellStyle(cs);
		return index;
	}


	public void setColWidthAndMargin(HSSFSheet sheet) {
		// Set Column Widths
		sheet.setColumnWidth(0, 4000);
		sheet.setColumnWidth(1, 3000);

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
		c.setCellValue("User");
		c.setCellStyle(csBold);

		c = row.createCell(1);
		c.setCellValue("Resumes");
		c.setCellStyle(csBold);
		rowIndex++;
		return rowIndex;
	}
	
	private void insertFooterInfo(HSSFSheet sheet, int rowIndex) {

		HSSFRow row = null;
		HSSFCell c = null;

		row = sheet.createRow(rowIndex);
		c = row.createCell(0);
		c.setCellValue("Total");
		c.setCellStyle(csBold);
		c = row.createCell(1);
		c.setCellValue(resumesTotal);
		c.setCellStyle(csBold);
	}
}
