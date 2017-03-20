package com.uralian.cgiats.util;

import java.text.SimpleDateFormat;
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

import com.uralian.cgiats.model.IndiaJobOrder;

public class IndiaJobOrdersExcelGenerator {
	
	protected final Logger log = LoggerFactory.getLogger(getClass());

	private HSSFCellStyle	cs		= null;
	private HSSFCellStyle	csBold	= null;
	
	
	public byte[] createExcel(List<IndiaJobOrder> jobOrderList,String fileName) {

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
	
	public void setColWidthAndMargin(HSSFSheet sheet) {
		// Set Column Widths
		sheet.setColumnWidth(0, 3000);
		sheet.setColumnWidth(1, 3000);
		sheet.setColumnWidth(2, 4000);
		sheet.setColumnWidth(3, 5000);
		sheet.setColumnWidth(4, 8000);
		sheet.setColumnWidth(5, 6000);
		sheet.setColumnWidth(6, 4000);
		sheet.setColumnWidth(7, 8000);
		sheet.setColumnWidth(8, 4000);
		sheet.setColumnWidth(9, 6000);
		/*sheet.setColumnWidth(10, 3000);*/
		sheet.setColumnWidth(10, 6000);

		// Setup the Page margins - Left, Right, Top and Bottom
		sheet.setMargin(HSSFSheet.LeftMargin, 0.25);
		sheet.setMargin(HSSFSheet.RightMargin, 0.25);
		sheet.setMargin(HSSFSheet.TopMargin, 0.75);
		sheet.setMargin(HSSFSheet.BottomMargin, 0.75);
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
		c.setCellValue("Assigned To");
		c.setCellStyle(csBold);
		
		c = row.createCell(9);
		c.setCellValue("Date Updated");
		c.setCellStyle(csBold);
		
		/*c = row.createCell(10);
		c.setCellValue("SBM");
		c.setCellStyle(csBold);*/
		
		c = row.createCell(10);
		c.setCellValue("Active days");
		c.setCellStyle(csBold);
		
		rowIndex++;
		return rowIndex;
	}
	
	private int insertJODetailInfo(HSSFSheet sheet, int index, List<IndiaJobOrder> jobOrderList) {
		int rowIndex = 0;

		try {
			for (int i = 0; i < jobOrderList.size(); i++) {
				rowIndex = index + i;
					
				insertDetailInfo(sheet, rowIndex, jobOrderList.get(i));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rowIndex;
	}

	
	private int insertDetailInfo(HSSFSheet sheet, int index, IndiaJobOrder indiaJobOrder) {

		HSSFRow row = null;
		HSSFCell c = null;
		SimpleDateFormat df = new SimpleDateFormat("MM-dd-yyyy");
		row = sheet.createRow(index);
		c = row.createCell(0);
		if (indiaJobOrder.getId() != null)
			c.setCellValue(indiaJobOrder.getId());
		c.setCellStyle(cs);

		c = row.createCell(1);
		c.setCellValue(indiaJobOrder.getPriority().name());
		c.setCellStyle(cs); 
		
		c = row.createCell(2);
		c.setCellValue(indiaJobOrder.getStatus().name());
		c.setCellStyle(cs);
		
		c = row.createCell(3);
		c.setCellValue(indiaJobOrder.getJobType().name());
		c.setCellStyle(cs);
		
		c = row.createCell(4);
		if(indiaJobOrder.getTitle()!=null)
		c.setCellValue(indiaJobOrder.getTitle());
		else
		c.setCellValue("");	
		c.setCellStyle(cs);

		c = row.createCell(5);
		if(indiaJobOrder.getCustomer()!=null&&!indiaJobOrder.getCustomerHidden())
		c.setCellValue(indiaJobOrder.getCustomer());
		else
		c.setCellValue("");	
		c.setCellStyle(cs);

		c = row.createCell(6);
		if(indiaJobOrder.getLocation()!=null)
		c.setCellValue(indiaJobOrder.getLocation());
		else
		c.setCellValue("");	
		c.setCellStyle(cs);
		
		c = row.createCell(7);
		c.setCellValue(indiaJobOrder.getCreatedBy());
		c.setCellStyle(cs);
		
		c = row.createCell(8);
		if(indiaJobOrder.getAssignedTo()!=null)
		c.setCellValue(indiaJobOrder.getAssignedToUser().getFullName());
		else
		c.setCellValue("");	
		c.setCellStyle(cs);
		
		c = row.createCell(9);
		c.setCellValue(df.format(indiaJobOrder.getLastModified()));
		c.setCellStyle(cs);

		/*c = row.createCell(10);
		c.setCellValue(indiaJobOrder.getSubmittalList().size());
		c.setCellStyle(cs);*/
		
		c = row.createCell(10);
		c.setCellValue(indiaJobOrder.getDays());
		c.setCellStyle(cs);
		
		return index;

	}
}
