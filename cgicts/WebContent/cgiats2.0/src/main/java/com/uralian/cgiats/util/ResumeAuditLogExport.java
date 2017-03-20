/**
 * 
 */
package com.uralian.cgiats.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.uralian.cgiats.model.ResumeHistory;

/**
 * @author Parameshwar
 *
 */
public class ResumeAuditLogExport {
	
	protected final Logger log = LoggerFactory.getLogger(getClass());

	private HSSFCellStyle	cs		= null;
	private HSSFCellStyle	csBold	= null;
	
	public byte[] createExcel(List<ResumeHistory> resumeHistory) {

		byte[] output = null;
		try {

			HSSFWorkbook wb = new HSSFWorkbook();
			HSSFSheet sheet = wb.createSheet("Resume Audit Logs");

			// Setup some styles that we need for the Cells
			setCellStyles(wb);

			setColWidthAndMargin(sheet);

			int rowIndex = 0;
			rowIndex = insertHeaderInfo(sheet, rowIndex);
			rowIndex = insertJODetailInfo(sheet, rowIndex, resumeHistory);
			String timeStamp = new SimpleDateFormat("MMddyyyy_HHmmss").format(Calendar.getInstance().getTime());
			String fileName = "attachment;filename=ResumeAuditLogs_"+timeStamp+".xls";
			output = wb.getBytes();
			log.info("Workbook length in bytes: "+output.length);
			 FacesContext context = FacesContext.getCurrentInstance();
		        HttpServletResponse res = (HttpServletResponse) context.getExternalContext().getResponse();
		        res.setContentType("application/vnd.ms-excel");
		        res.setHeader("Content-disposition", fileName);
		        ServletOutputStream out = res.getOutputStream();
		        wb.write(out);
		        out.flush();
		        out.close();
		        FacesContext.getCurrentInstance().responseComplete();
		    } 
		 catch (Exception e) {
			log.info("Exception in exporting all records to excel :: " + e);
			e.printStackTrace();
		} 
		return output;
	}

	public void setColWidthAndMargin(HSSFSheet sheet) {
		// Set Column Widths
		sheet.setColumnWidth(0, 4000);
		sheet.setColumnWidth(1, 7000);
		sheet.setColumnWidth(2, 7000);
		sheet.setColumnWidth(3, 5000);
		sheet.setColumnWidth(4, 4000);		

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
		c.setCellValue("Candidate Id");
		c.setCellStyle(csBold);

		c = row.createCell(1);
		c.setCellValue("Viewed/Downloaded By");
		c.setCellStyle(csBold);
		
		c = row.createCell(2);
		c.setCellValue("Viewed/Downloaded Date");
		c.setCellStyle(csBold);
		
		c = row.createCell(3);
		c.setCellValue("Document Status");
		c.setCellStyle(csBold);
		
		c = row.createCell(4);
		c.setCellValue("Status");
		c.setCellStyle(csBold);
					
		rowIndex++;
		return rowIndex;
	}

	private int insertDetailInfo(HSSFSheet sheet, int index, ResumeHistory resumeHistory) {

		HSSFRow row = null;
		HSSFCell c = null;
		SimpleDateFormat df = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
		row = sheet.createRow(index);
		
		c = row.createCell(0);		
		if (resumeHistory.getCandidate() != null)
			c.setCellValue(resumeHistory.getCandidate());
		c.setCellStyle(cs);

		c = row.createCell(1);
		c.setCellValue(resumeHistory.getCreatedBy());
		c.setCellStyle(cs); 
		
		c = row.createCell(2);
		c.setCellValue(df.format(resumeHistory.getCreatedOn()));
		c.setCellStyle(cs);
		
		c = row.createCell(3);
		c.setCellValue(resumeHistory.getDocStatus());
		c.setCellStyle(cs);
		
		c = row.createCell(4);		
		c.setCellValue(resumeHistory.getStatus());		
		c.setCellStyle(cs);
			
		return index;

	}
	
	private int insertJODetailInfo(HSSFSheet sheet, int index, List<ResumeHistory> resumeHistory) {
		int rowIndex = 0;

		try {
			for (int i = 0; i < resumeHistory.size(); i++) {
				rowIndex = index + i;
					
				insertDetailInfo(sheet, rowIndex, resumeHistory.get(i));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rowIndex;
	}
}
