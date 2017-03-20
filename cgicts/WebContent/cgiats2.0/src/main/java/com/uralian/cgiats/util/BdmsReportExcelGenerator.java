package com.uralian.cgiats.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
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
import org.apache.poi.ss.usermodel.CellStyle;

public class BdmsReportExcelGenerator {

	private HSSFCellStyle cs = null;
	private HSSFCellStyle csBold = null;

	String fileName = null;

	public byte[] createReportsExcel(List<Map<String, Object>> bdmReports,
			String reportType, Map<String, Integer> totals) {

		byte[] output = null;

		try {

			HSSFWorkbook wb = new HSSFWorkbook();
			HSSFSheet sheet = null;

			// Write the Excel file
			// FileOutputStream fileOut = new
			// FileOutputStream("c:/reports/myReport2.xls"); wb.write(fileOut);
			String timeStamp = new SimpleDateFormat("MMddyyyy_HHmmss")
					.format(Calendar.getInstance().getTime());
			if (reportType != null && reportType.equals("DM")) {
				fileName = "attachment;filename=DM_Jobs_Status_Report"
						+ timeStamp + ".xls";
				sheet = wb.createSheet("DMs Jobs Status Report");
			} else {
				fileName = "attachment;filename=ADM_Jobs_Status_Report"
						+ timeStamp + ".xls";
				sheet = wb.createSheet("ADMs Jobs Status Report");
			}

			// Setup some styles that we need for the Cells
			setCellStyles(wb);
			setColWidthAndMargin(sheet);
			int rowIndex = 0;
			rowIndex = insertHeaderInfo(sheet, rowIndex, reportType);
			rowIndex = insertReportsInfo(sheet, rowIndex, bdmReports,
					reportType);
			rowIndex = insertFooterInfo(sheet, rowIndex, totals);

			output = wb.getBytes();
			System.out.println("Workbook length in bytes: " + output.length);
			FacesContext context = FacesContext.getCurrentInstance();
			HttpServletResponse res = (HttpServletResponse) context
					.getExternalContext().getResponse();
			res.setContentType("application/vnd.ms-excel");
			res.setHeader("Content-disposition", fileName);
			ServletOutputStream out = res.getOutputStream();
			wb.write(out);
			out.flush();
			out.close();
			FacesContext.getCurrentInstance().responseComplete();
		} catch (Exception e) {
			System.out
					.println("Exception in exporting all records to excel :: "
							+ e);
			e.printStackTrace();
		}
		return output;
	}

	public void setColWidthAndMargin(HSSFSheet sheet) {
		// Set Column Widths
		sheet.setColumnWidth(0, 6000);
		sheet.setColumnWidth(1, 4000);
		sheet.setColumnWidth(2, 4000);
		sheet.setColumnWidth(3, 7000);
		sheet.setColumnWidth(4, 4000);
		sheet.setColumnWidth(5, 4000);
		sheet.setColumnWidth(6, 4000);

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

	private int insertHeaderInfo(HSSFSheet sheet, int index, String reportType) {

		int rowIndex = index;
		HSSFRow row = null;
		HSSFCell c = null;

		row = sheet.createRow(rowIndex);

		c = row.createCell(0);

		if (reportType != null && reportType.equals("DM")) {
			c.setCellValue("DM Name");
		} else {
			c.setCellValue("ADM Name");
		}
		c.setCellStyle(csBold);

		c = row.createCell(1);
		c.setCellValue("# Of Open JobOrders");
		c.setCellStyle(csBold);
		
		c = row.createCell(2);
		c.setCellValue("# Of Closed JobOrders");
		c.setCellStyle(csBold);

		c = row.createCell(3);
		c.setCellValue("# Of Submittals");
		c.setCellStyle(csBold);

		c = row.createCell(4);
		c.setCellValue("# Of End Client Interviews");
		c.setCellStyle(csBold);

		c = row.createCell(5);
		c.setCellValue("# Of Confirmed");
		c.setCellStyle(csBold);

		c = row.createCell(6);
		c.setCellValue("# Of Closers");
		c.setCellStyle(csBold);

		rowIndex++;
		return rowIndex;
	}
     
	private int insertFooterInfo(HSSFSheet sheet, int rowIndex,	Map<String, Integer> totals) {
		
		rowIndex+=1;
		HSSFRow row = null;
		HSSFCell c = null;
		row = sheet.createRow(rowIndex);
		
		    c = row.createCell(0);
		    c.setCellValue("Total:");
		    c.setCellStyle(csBold);
			
			/*c = row.createCell(1);
			c.setCellValue(totals.get("jobsTotal"));
			c.setCellStyle(csBold);
			c.setCellType(CellStyle.ALIGN_LEFT);*/
		    
		    c = row.createCell(1);
			c.setCellValue(totals.get("openJobCount"));
			c.setCellStyle(csBold);
			c.setCellType(CellStyle.ALIGN_LEFT);
			
			c = row.createCell(2);
			c.setCellValue(totals.get("closedJobCount"));
			c.setCellStyle(csBold);
			c.setCellType(CellStyle.ALIGN_LEFT);
			
			c = row.createCell(3);
			c.setCellValue(totals.get("submittalsTotal"));
			c.setCellStyle(csBold);
			c.setCellType(CellStyle.ALIGN_LEFT);
			
			c = row.createCell(4);
			c.setCellValue(totals.get("interviewingTotal"));
			c.setCellStyle(csBold);
			c.setCellType(CellStyle.ALIGN_LEFT);
			
			c = row.createCell(5);
			c.setCellValue(totals.get("confirmedTotal"));
			c.setCellStyle(csBold);
			c.setCellType(CellStyle.ALIGN_LEFT);
			
			c = row.createCell(6);
			c.setCellValue(totals.get("startedTotal"));
			c.setCellStyle(csBold);
			c.setCellType(CellStyle.ALIGN_LEFT);
		

		rowIndex++;
		return rowIndex;
	}
	
	
	
	private int insertRowInfo(HSSFSheet sheet, int index,
			Map<String, Object> map) {

		HSSFRow row = null;
		HSSFCell c = null;
		row = sheet.createRow(index);

		String value;

		c = row.createCell(0);
		c.setCellValue(map.get("userId").toString());
		c.setCellStyle(cs);

		/*c = row.createCell(1);
		c.setCellValue(map.get("totalJobOrders").toString());
		c.setCellStyle(cs);*/
		
		c = row.createCell(1);
		c.setCellValue(map.get("openJobCount").toString());
		c.setCellStyle(cs);
		
		c = row.createCell(2);
		c.setCellValue(map.get("closedJobCount").toString());
		c.setCellStyle(cs);

		c = row.createCell(3);
		value = StringValueOf(map.get("totalSubmittals"));
		c.setCellValue(value);
		c.setCellStyle(cs);

		c = row.createCell(4);
		value = StringValueOf(map.get("interviewingSubmittals"));
		c.setCellValue(value);
		c.setCellStyle(cs);

		c = row.createCell(5);
		value = StringValueOf(map.get("confirmedSubmittals"));
		c.setCellValue(value);
		c.setCellStyle(cs);

		c = row.createCell(6);
		value = StringValueOf(map.get("startedSubmittals"));
		c.setCellValue(value);
		c.setCellStyle(cs);

		return index;
	}

	private String StringValueOf(Object object) {

		String string;
		if (object != null) {
			string = object.toString();
		} else {
			string = "0";
		}

		return string;
	}

	private int insertReportsInfo(HSSFSheet sheet, int index,
			List<Map<String, Object>> bdmReports, String reportType) {
		int rowIndex = 0;

		try {
			for (int i = 0; i < bdmReports.size(); i++) {
				rowIndex = index + i;
				insertRowInfo(sheet, rowIndex, bdmReports.get(i));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rowIndex;
	}
}
