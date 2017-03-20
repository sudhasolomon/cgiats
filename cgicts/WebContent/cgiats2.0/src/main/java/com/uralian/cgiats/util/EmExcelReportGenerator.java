package com.uralian.cgiats.util;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.uralian.cgiats.model.JobOrder;
import com.uralian.cgiats.model.JobOrderDefaults;
import com.uralian.cgiats.model.JobType;

public class EmExcelReportGenerator {

	
	protected final Logger log = LoggerFactory.getLogger(getClass());
	
	private HSSFCellStyle	cs		= null;
	private HSSFCellStyle	csBold	= null;

	public byte[] createExcel(JobOrder selectedOrder) {

		byte[] output = null;
		try {

			HSSFWorkbook wb = new HSSFWorkbook();
			HSSFSheet sheet = wb.createSheet("Job Order # " + selectedOrder.getId());

			// Setup some styles that we need for the Cells
			setCellStyles(wb);

			// Get current Date and Time
			/*
			 * Date date = new Date(System.currentTimeMillis()); 
			 * DateFormat df = new SimpleDateFormat("MM/dd/yy HH:mm:ss");
			 */

			setColWidthAndMargin(sheet);

			int rowIndex = 0;
			rowIndex = insertHeaderInfo(sheet, rowIndex);
			rowIndex = insertDetailInfo(sheet, rowIndex, selectedOrder);
			
			String fileName = "attachment;filename=EMJobOrders_"+selectedOrder.getId()+".xls";
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
			// Write the Excel file
			/*
			 * fileOut = new FileOutputStream("c:/reports/myReport2.xls"); wb.write(fileOut);
			 */
			output = wb.getBytes();
			log.info("Workbook length in bytes: "+output.length);

		} catch (Exception e) {
			log.info("Exception in exporting to excel :: " + e);
			e.printStackTrace();
		}
		return output;
	}
	
	public byte[] createExcel(List<JobOrder> jobOrderList) {

		byte[] output = null;
		try {

			HSSFWorkbook wb = new HSSFWorkbook();
			HSSFSheet sheet = wb.createSheet("Job Orders List");

			// Setup some styles that we need for the Cells
			setCellStyles(wb);

			setColWidthAndMargin(sheet);

			int rowIndex = 0;
			rowIndex = insertHeaderInfo(sheet, rowIndex);
			rowIndex = insertJODetailInfo(sheet, rowIndex, jobOrderList);
			String timeStamp = new SimpleDateFormat("MMddyyyy_HHmmss").format(Calendar.getInstance().getTime());
			String fileName = "attachment;filename=EMJobOrder_"+timeStamp+".xls";
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
		sheet.setColumnWidth(0, 3000);
		sheet.setColumnWidth(1, 3000);
		sheet.setColumnWidth(2, 3000);
		sheet.setColumnWidth(3, 4000);
		sheet.setColumnWidth(4, 6000);
		sheet.setColumnWidth(5, 6000);
		sheet.setColumnWidth(6, 4000);
		sheet.setColumnWidth(7, 4000);
		sheet.setColumnWidth(8, 4000);
		sheet.setColumnWidth(9, 6000);
		sheet.setColumnWidth(10, 3000);
		sheet.setColumnWidth(11, 6000);

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
		c.setCellValue("Job Order #");
		c.setCellStyle(csBold);

		c = row.createCell(1);
		c.setCellValue("Priority");
		c.setCellStyle(csBold);
		
		c = row.createCell(2);
		c.setCellValue("Status");
		c.setCellStyle(csBold);
		
		c = row.createCell(3);
		c.setCellValue("Position Type");
		c.setCellStyle(csBold);
		
		c = row.createCell(4);
		c.setCellValue("Title");
		c.setCellStyle(csBold);
		
		c = row.createCell(5);
		c.setCellValue("Client");
		c.setCellStyle(csBold);
		
		c = row.createCell(6);
		c.setCellValue("Location");
		c.setCellStyle(csBold);
		
		c = row.createCell(7);
		c.setCellValue("DM");
		c.setCellStyle(csBold);
		
		c = row.createCell(8);
		c.setCellValue("Em Name");
		c.setCellStyle(csBold);
		
		c = row.createCell(9);
		c.setCellValue("Date Updated");
		c.setCellStyle(csBold);
		
		c = row.createCell(10);
		c.setCellValue("SBM");
		c.setCellStyle(csBold);
		
		c = row.createCell(11);
		c.setCellValue("Active days");
		c.setCellStyle(csBold);
		
		rowIndex++;
		return rowIndex;
	}

	private int insertDetailInfo(HSSFSheet sheet, int index, JobOrder jobOrder) {

		HSSFRow row = null;
		HSSFCell c = null;
		SimpleDateFormat df = new SimpleDateFormat("MM-dd-yyyy");
		row = sheet.createRow(index);
		c = row.createCell(0);
		if (jobOrder.getId() != null)
			c.setCellValue(jobOrder.getId());
		c.setCellStyle(cs);

		c = row.createCell(1);
		c.setCellValue(jobOrder.getPriority().name());
		c.setCellStyle(cs); 
		
		c = row.createCell(2);
		c.setCellValue(jobOrder.getStatus().name());
		c.setCellStyle(cs);
		
		c = row.createCell(3);
		c.setCellValue(jobOrder.getJobType().name());
		c.setCellStyle(cs);
		
		c = row.createCell(4);
		if(jobOrder.getTitle()!=null)
		c.setCellValue(jobOrder.getTitle());
		else
		c.setCellValue("");	
		c.setCellStyle(cs);

		c = row.createCell(5);
		if(jobOrder.getCustomer()!=null)
		c.setCellValue(jobOrder.getCustomer());
		else
		c.setCellValue("");	
		c.setCellStyle(cs);

		c = row.createCell(6);
		if(jobOrder.getLocation()!=null)
		c.setCellValue(jobOrder.getLocation());
		else
		c.setCellValue("");	
		c.setCellStyle(cs);
		
		c = row.createCell(7);
		c.setCellValue(jobOrder.getCreatedBy());
		c.setCellStyle(cs);
		
		c = row.createCell(8);
		if(jobOrder.getEmName()!=null)
		c.setCellValue(jobOrder.getEmName());
		else
		c.setCellValue("");	
		c.setCellStyle(cs);
		
		c = row.createCell(9);
		c.setCellValue(df.format(jobOrder.getLastModified()));
		c.setCellStyle(cs);

		c = row.createCell(10);
		c.setCellValue(jobOrder.getSubmittalList().size());
		c.setCellStyle(cs);
		
		c = row.createCell(11);
		c.setCellValue(jobOrder.getDays());
		c.setCellStyle(cs);
		
	/*	log.info("ExcelGenerator.insertDetailInfo()...jobOrder.getCreatedOn(): " + jobOrder.getCreatedOn());
		SimpleDateFormat df = new SimpleDateFormat("MM/dd/yy HH:mm:ss");
		c = row.createCell(6);
		c.setCellValue(df.format(jobOrder.getCreatedOn()));
		c.setCellStyle(cs);*/
		return index;

	}
	
	private int insertJODetailInfo(HSSFSheet sheet, int index, List<JobOrder> jobOrders) {
		int rowIndex = 0;

		try {
			for (int i = 0; i < jobOrders.size(); i++) {
				rowIndex = index + i;
					
				insertDetailInfo(sheet, rowIndex, jobOrders.get(i));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rowIndex;
	}
	
	public static void main(String[] args) {

		JobOrder jo = new JobOrder(new JobOrderDefaults());
		jo.setTitle("test job order");
		jo.setDescription("test job order description");
		jo.setJobType(JobType.CONTRACT);
		jo.setCreatedBy("abc");
		jo.setCreatedOn(new Date());
		ExcelGenerator myReport = new ExcelGenerator();
		byte[] b = myReport.createExcel(jo);
		FileOutputStream fileOut;
		try {
			fileOut = new FileOutputStream("c:/reports/myReport21.xls");
			fileOut.write(b);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
