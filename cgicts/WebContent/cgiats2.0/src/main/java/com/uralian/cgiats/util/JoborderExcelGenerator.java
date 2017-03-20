/**
 * 
 */
package com.uralian.cgiats.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
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

import com.uralian.cgiats.model.JobOrderStatus;

/**
 * @author Parameshwar
 *
 */
public class JoborderExcelGenerator {

	private HSSFCellStyle	cs		= null;
	private HSSFCellStyle	csBold	= null;

	public byte[] createExcel(Map<String, Map<JobOrderStatus, Integer>> orderStatsByUser) {

		byte[] output = null;
		try {

			HSSFWorkbook wb = new HSSFWorkbook();
			HSSFSheet sheet = wb.createSheet("JobOrder Stats");

			// Setup some styles that we need for the Cells
			setCellStyles(wb);
			setColWidthAndMargin(sheet);

			int rowIndex = 0;
			rowIndex = insertHeaderInfo(sheet, rowIndex);
			System.out.println("rowIndex:::"+rowIndex);			
			rowIndex = insertSubmittalsInfo(sheet, rowIndex, orderStatsByUser);
			String timeStamp = new SimpleDateFormat("MMddyyyy_HHmmss").format(Calendar.getInstance().getTime());
			String fileName = "attachment;filename=JoborderStats"+timeStamp+".xls";
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

	public byte[] createExcel1(Map<String, Map<JobOrderStatus, Integer>> orderStatsByUser) {

		byte[] output = null;
		try {

			HSSFWorkbook wb = new HSSFWorkbook();
			HSSFSheet sheet = wb.createSheet("JobOrder Stats");

			// Setup some styles that we need for the Cells
			setCellStyles(wb);
			setColWidthAndMargin(sheet);

			int rowIndex = 0;
			rowIndex = insertHeaderInfo1(sheet, rowIndex);
			System.out.println("rowIndex:::"+rowIndex);			
			rowIndex = insertSubmittalsInfo(sheet, rowIndex, orderStatsByUser);
			String timeStamp = new SimpleDateFormat("MMddyyyy_HHmmss").format(Calendar.getInstance().getTime());
			String fileName = "attachment;filename=JoborderStats"+timeStamp+".xls";
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
		c.setCellValue("DM");
		c.setCellStyle(csBold);

		c = row.createCell(1);
		c.setCellValue("OPEN");
		c.setCellStyle(csBold);

		c = row.createCell(2);
		c.setCellValue("ASSIGNED");
		c.setCellStyle(csBold);			

		c = row.createCell(3);
		c.setCellValue("FILLED");
		c.setCellStyle(csBold);

		c = row.createCell(4);
		c.setCellValue("CLOSED");
		c.setCellStyle(csBold);

		c = row.createCell(5);
		c.setCellValue("REOPEN");
		c.setCellStyle(csBold);

		c = row.createCell(6);
		c.setCellValue("TOTAL");
		c.setCellStyle(csBold);

		rowIndex++;
		return rowIndex;
	}
	private int insertHeaderInfo1(HSSFSheet sheet, int index) {

		int rowIndex = index;
		HSSFRow row = null;
		HSSFCell c = null;

		row = sheet.createRow(rowIndex);
		c = row.createCell(0);
		c.setCellValue("Other than DMs");
		c.setCellStyle(csBold);

		c = row.createCell(1);
		c.setCellValue("OPEN");
		c.setCellStyle(csBold);

		c = row.createCell(2);
		c.setCellValue("ASSIGNED");
		c.setCellStyle(csBold);			

		c = row.createCell(3);
		c.setCellValue("FILLED");
		c.setCellStyle(csBold);

		c = row.createCell(4);
		c.setCellValue("CLOSED");
		c.setCellStyle(csBold);

		c = row.createCell(5);
		c.setCellValue("REOPEN");
		c.setCellStyle(csBold);

		c = row.createCell(6);
		c.setCellValue("TOTAL");
		c.setCellStyle(csBold);

		rowIndex++;
		return rowIndex;
	}

	private int insertDetailInfo(HSSFSheet sheet, int index,String bdmName,String strRow) {		

		HSSFRow row = null;
		HSSFCell c = null;	
		row = sheet.createRow(index);

		c = row.createCell(0);
		c.setCellValue(bdmName);
		c.setCellStyle(cs);		

		StringTokenizer st = new StringTokenizer(strRow,",");

		int i=1;
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
		row = sheet.createRow(index+2);

		c = row.createCell(0);
		c.setCellValue("TOTAL:");
		c.setCellStyle(csBold);		

		StringTokenizer st1 = new StringTokenizer(columnRow,",");

		int i=1;
		while(st1.hasMoreTokens()){
			c = row.createCell(i);
			c.setCellValue(st1.nextToken());			   
			c.setCellStyle(cs);
			i =i+1;	
		}
		return index;
	}

	private int insertSubmittalsInfo(HSSFSheet sheet, int index, Map<String, Map<JobOrderStatus, Integer>> orderStatsByUser) {

		int rowIndex = 0;
		Integer open = 0;
		Integer assigned =0;
		Integer filled= 0;
		Integer closed = 0;	
		Integer reopen = 0;
		Integer openTotal = 0;
		Integer assignedTotal =0;
		Integer filledTotal= 0;
		Integer closedTotal = 0;	
		Integer reopenTotal = 0;
		String strRow = null;
		int total =0;	
		int count = 0;
		String columnRow = null;


		try {				

			for (Map.Entry<String, Map<JobOrderStatus, Integer>>  entry : orderStatsByUser.entrySet())
			{
				rowIndex = rowIndex + 1;	
				String bdmName = entry.getKey();				 

				for (Map.Entry<JobOrderStatus, Integer>  subEntry : entry.getValue().entrySet())
				{						 
					if((subEntry.getKey()).toString().equals("OPEN")){
						open = subEntry.getValue();
						openTotal = openTotal + open;
					}
					else if((subEntry.getKey()).toString().equals("ASSIGNED")){
						assigned = subEntry.getValue();
						assignedTotal = assignedTotal + assigned;
					}
					else if((subEntry.getKey()).toString().equals("FILLED")){
						filled = subEntry.getValue();
						filledTotal = filledTotal + filled;
					}
					else if((subEntry.getKey()).toString().equals("CLOSED")){
						closed = subEntry.getValue();
						closedTotal = closedTotal + closed;
					}
					else if((subEntry.getKey()).toString().equals("REOPEN")){
						reopen = subEntry.getValue();
						reopenTotal = reopenTotal + reopen;
					}

				}					  
				total = open+assigned+filled+closed+reopen;
				count = openTotal+assignedTotal+filledTotal+closedTotal+reopenTotal;
				strRow = open+","+assigned+","+filled+","+closed+","+reopen+","+total;
				insertDetailInfo(sheet, rowIndex,entry.getKey(),strRow);
			}
			columnRow =  openTotal+","+assignedTotal+","+filledTotal+","+closedTotal+","+reopenTotal+","+count;
			insertDetailInfoTotal(sheet, rowIndex,columnRow);
		} catch (Exception e) {
			e.printStackTrace();
		}	

		return rowIndex;
	}
}
