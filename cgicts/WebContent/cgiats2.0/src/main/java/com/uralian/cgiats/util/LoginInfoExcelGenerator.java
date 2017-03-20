/**
 * 
 */
package com.uralian.cgiats.util;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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

import com.uralian.cgiats.model.LoginAttempts;

/**
 * @author Parameshwar
 *
 */
public class LoginInfoExcelGenerator {
	
	private HSSFCellStyle	cs		= null;
	private HSSFCellStyle	csBold	= null;
	
	public byte[] createExcel(List<LoginAttempts> loginAttempts) {

		byte[] output = null;
		try {

			HSSFWorkbook wb = new HSSFWorkbook();
			HSSFSheet sheet = wb.createSheet("Login Info List");

			// Setup some styles that we need for the Cells
			setCellStyles(wb);

			setColWidthAndMargin(sheet);

			int rowIndex = 0;
			rowIndex = insertHeaderInfo(sheet, rowIndex);
			rowIndex = insertJODetailInfo(sheet, rowIndex, loginAttempts);

			// Write the Excel file
			//FileOutputStream fileOut = new FileOutputStream("c:/reports/myReport2.xls"); wb.write(fileOut);
			String timeStamp = new SimpleDateFormat("MMddyyyy_HHmmss").format(Calendar.getInstance().getTime());
			String fileName = "attachment;filename=LoginInfo_"+timeStamp+".xls";
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
		sheet.setColumnWidth(0, 4000);
		sheet.setColumnWidth(1, 6000);
		sheet.setColumnWidth(2, 6000);	
		sheet.setColumnWidth(3, 4000);
		sheet.setColumnWidth(4, 6000);
		sheet.setColumnWidth(5, 2000);

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
		c.setCellValue("Login User");
		c.setCellStyle(csBold);

		c = row.createCell(1);
		c.setCellValue("Login Date & Time");
		c.setCellStyle(csBold);
		
		c = row.createCell(2);
		c.setCellValue("Logout Date & Time");
		c.setCellStyle(csBold);			

		c = row.createCell(3);
		c.setCellValue("Duration");
		c.setCellStyle(csBold);
		
		c = row.createCell(4);
		c.setCellValue("# of Login Attempts");
		c.setCellStyle(csBold);
		
		rowIndex++;
		return rowIndex;
	}

	private int insertDetailInfo(HSSFSheet sheet, int index, LoginAttempts loginAttempts) {

		HSSFRow row = null;
		HSSFCell c = null;
		SimpleDateFormat df = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
		row = sheet.createRow(index);		
		
		c = row.createCell(0);		
		c.setCellValue(loginAttempts.getCreatedBy());
		c.setCellStyle(cs);

		c = row.createCell(1);		
		c.setCellValue(df.format(loginAttempts.getLoginDate()));
		c.setCellStyle(cs);

		c = row.createCell(2);	
		if(loginAttempts.getLogoutDate() != null)
			c.setCellValue(df.format(loginAttempts.getLogoutDate()));	
		else
			c.setCellValue("");
		c.setCellStyle(cs);
		
		c = row.createCell(3);	
		if(loginAttempts.getDuration() != null)
			c.setCellValue(loginAttempts.getDuration());
		else
			c.setCellValue("");
		c.setCellStyle(cs);
		
		c = row.createCell(4);		
		c.setCellValue(loginAttempts.getTotal());
		c.setCellStyle(cs);		
		
		return index;
	}
	
	private int insertJODetailInfo(HSSFSheet sheet, int index, List<LoginAttempts> loginAttempts) {
		int rowIndex = 0;

		try {
			for (int i = 0; i < loginAttempts.size(); i++) {
				rowIndex = index + i;
				insertDetailInfo(sheet, rowIndex, loginAttempts.get(i));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rowIndex;
	}
	
}
