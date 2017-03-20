package com.uralian.cgiats.util;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

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
import com.uralian.cgiats.web.SchedulerEmailBean;

public class ExcelGeneratorJobOrderScheduler {

	protected final Logger log = LoggerFactory.getLogger(getClass());

	private HSSFCellStyle cs = null;
	private HSSFCellStyle csBold = null;

	public byte[] createExcel(List<JobOrder> jobOrderList, String email) {

		byte[] output = null;
		try {

			HSSFWorkbook wb = new HSSFWorkbook();
			HSSFSheet sheet = wb.createSheet("Job Orders List");

			// Setup some styles that we need for the Cells
			setCellStyles(wb);

			setColWidthAndMargin(sheet);

			int rowIndex = 0;
			rowIndex = insertHeaderInfo(sheet, rowIndex, false);
			rowIndex = insertJODetailInfo(sheet, rowIndex, jobOrderList, null);
			String timeStamp = new SimpleDateFormat("MMddyyyy_HHmmss").format(Calendar.getInstance().getTime());
			if (rowIndex >= 1) {

				String fileName = "JobOrder_" + timeStamp + ".xls";
				output = wb.getBytes();
				SchedulerEmailBean seb = new SchedulerEmailBean();
				if (email != null) {
					seb.sendEmailWithAttachment(email, "CGIATS: Report of JobOrders without Submittals",
							"Hi all, <br/><br/>&nbsp;&nbsp;&nbsp;&nbsp;Please find the attachment containing list of joborders without submittals from more than 48 hours.<br/><br/><b>*** This is an automatically generated email, please do not reply ***</b>",
							fileName, wb, rowIndex);
				} else {
					log.info("To Address is Empty ::");
				}

				try {
					FileOutputStream fileOut = new FileOutputStream("./Mail/JobOrder/" + fileName);
					wb.write(fileOut);
					fileOut.close();
					System.out.println("file was copied " + fileName);

				} catch (FileNotFoundException fnfe) {

				}
			}
		} catch (Exception e) {
			log.info("Exception in exporting all records to excel :: " + e);
			e.printStackTrace();
		}
		return output;
	}

	public byte[] createExcelForOneDayWithoutSubmittals(Map<Integer, Integer> jobOrderWithCountMap, List<JobOrder> jobOrderList, String email) {

		byte[] output = null;
		try {

			HSSFWorkbook wb = new HSSFWorkbook();
			HSSFSheet sheet = wb.createSheet("Job Orders List");

			// Setup some styles that we need for the Cells
			setCellStyles(wb);

			setColWidthAndMargin(sheet);

			int rowIndex = 0;
			rowIndex = insertHeaderInfo(sheet, rowIndex, true);
			rowIndex = insertJODetailInfo(sheet, rowIndex, jobOrderList, jobOrderWithCountMap);
			String timeStamp = new SimpleDateFormat("MMddyyyy_HHmmss").format(Calendar.getInstance().getTime());
			if (rowIndex >= 1) {

				String fileName = "JobOrder_" + timeStamp + ".xls";
				output = wb.getBytes();
				SchedulerEmailBean seb = new SchedulerEmailBean();
				if (email != null) {
					seb.sendEmailWithAttachment(email, "CGIATS: Report of JobOrders without required Submittals in more than 24 hours",
							"Hi all, <br/><br/>&nbsp;&nbsp;&nbsp;&nbsp;Please find the attachment containing list of joborders without required submittals from more than 24 hours.<br/><br/><b>*** This is an automatically generated email, please do not reply ***</b>",
							fileName, wb, rowIndex);
				} else {
					log.info("To Address is Empty ::");
				}

				try {
					FileOutputStream fileOut = new FileOutputStream("./Mail/JobOrder/" + fileName);
					wb.write(fileOut);
					fileOut.close();
					System.out.println("file was copied " + fileName);

				} catch (FileNotFoundException fnfe) {
					fnfe.printStackTrace();
					log.error(fnfe.getMessage());
				}
			}
		} catch (Exception e) {
			log.info("Exception in exporting all records to excel :: " + e);
			e.printStackTrace();
		}
		return output;
	}

	public byte[] createExcelOpenJOs(List<JobOrder> jobOrderList, String email) {

		byte[] output = null;
		try {

			HSSFWorkbook wb = new HSSFWorkbook();
			HSSFSheet sheet = wb.createSheet("Job Orders List");

			// Setup some styles that we need for the Cells
			setCellStyles(wb);

			setColWidthAndMargin(sheet);

			int rowIndex = 0;
			rowIndex = insertHeaderInfo(sheet, rowIndex, false);
			rowIndex = insertJODetailInfo(sheet, rowIndex, jobOrderList, null);
			String timeStamp = new SimpleDateFormat("MMddyyyy_HHmmss").format(Calendar.getInstance().getTime());
			log.info("rowIndex>>" + rowIndex);
			if (rowIndex >= 1) {

				String fileName = "JobOrder_" + timeStamp + ".xls";
				log.info("output>>" + output);
				output = wb.getBytes();
				log.info("output>>" + output);
				log.info("Workbook length in bytes: " + output.length);

				SchedulerEmailBean seb = new SchedulerEmailBean();
				if (email != null) {
					seb.sendEmailWithAttachment(email, "CGIATS: Report of JobOrders in Open status from more than 10 Days",
							"Hi all, <br/><br/>&nbsp;&nbsp;&nbsp;&nbsp;Please find the attachment containing list of joborders in open status from more than 10 days.<br/><br/><b>*** This is an automatically generated email, please do not reply ***</b>",
							fileName, wb, rowIndex);
				} else {
					log.info("To Address is Empty ::");
				}

				try {
					FileOutputStream fileOut = new FileOutputStream("./Mail/JobOrder/" + fileName);
					wb.write(fileOut);
					fileOut.close();
					System.out.println("file was copied " + fileName);

				} catch (FileNotFoundException fnfe) {

				}
			}
		} catch (Exception e) {
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
		sheet.setColumnWidth(10, 6000);

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

	private int insertHeaderInfo(HSSFSheet sheet, int index, boolean isExtraColumnRequired) {

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
		c.setCellValue("Assigned To");
		c.setCellStyle(csBold);

		c = row.createCell(9);
		c.setCellValue("Created Date");
		c.setCellStyle(csBold);

		c = row.createCell(10);
		c.setCellValue("Updated Date");
		c.setCellStyle(csBold);
		if (isExtraColumnRequired) {
			c = row.createCell(11);
			c.setCellValue("No Of Resumes Required");
			c.setCellStyle(csBold);

			c = row.createCell(12);
			c.setCellValue("Filled Submittals");
			c.setCellStyle(csBold);
		}
		rowIndex++;
		return rowIndex;
	}

	private int insertDetailInfo(HSSFSheet sheet, int index, JobOrder jobOrder, Map<Integer, Integer> jobOrderWithCountMap) {

		HSSFRow row = null;
		HSSFCell c = null;
		SimpleDateFormat df = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
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
		if (jobOrder.getTitle() != null)
			c.setCellValue(jobOrder.getTitle());
		else
			c.setCellValue("");
		c.setCellStyle(cs);

		c = row.createCell(5);
		if (jobOrder.getCustomer() != null)
			c.setCellValue(jobOrder.getCustomer());
		else
			c.setCellValue("");
		c.setCellStyle(cs);

		c = row.createCell(6);
		if (jobOrder.getLocation() != null)
			c.setCellValue(jobOrder.getLocation());
		else
			c.setCellValue("");
		c.setCellStyle(cs);

		c = row.createCell(7);
		c.setCellValue(jobOrder.getCreatedBy());
		c.setCellStyle(cs);

		c = row.createCell(8);
		if (jobOrder.getAssignedToUser() != null)
			c.setCellValue(jobOrder.getAssignedToUser().getFullName());
		else
			c.setCellValue("");
		c.setCellStyle(cs);

		c = row.createCell(9);
		c.setCellValue(df.format(jobOrder.getCreatedOn()));
		c.setCellStyle(cs);

		c = row.createCell(10);
		if (jobOrder.getUpdatedOn() != null)
			c.setCellValue(df.format(jobOrder.getUpdatedOn()));
		else
			c.setCellValue("");
		c.setCellStyle(cs);

		if (jobOrderWithCountMap != null) {
			c = row.createCell(11);
			if (jobOrder.getNoOfResumesRequired() != null) {
				c.setCellValue(jobOrder.getNoOfResumesRequired());
			} else {
				c.setCellValue("");
			}
			c.setCellStyle(cs);

			c = row.createCell(12);
			if (jobOrderWithCountMap.get(jobOrder.getId()) != null) {
				c.setCellValue(jobOrderWithCountMap.get(jobOrder.getId()));
			} else {
				c.setCellValue("");
			}
			c.setCellStyle(cs);
		}
		return index;

	}

	private int insertJODetailInfo(HSSFSheet sheet, int index, List<JobOrder> jobOrders, Map<Integer, Integer> jobOrderWithCountMap) {
		int rowIndex = 0;

		try {
			for (int i = 0; i < jobOrders.size(); i++) {
				rowIndex = index + i;

				insertDetailInfo(sheet, rowIndex, jobOrders.get(i), jobOrderWithCountMap);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rowIndex;
	}

}