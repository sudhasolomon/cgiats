/**
 * 
 */
package com.uralian.cgiats.util;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
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

import com.uralian.cgiats.model.OnsiteVisits;

/**
 * @author Parameshwar
 *
 */
public class OnsiteVisitsExcelGenerator {	

	private HSSFCellStyle	cs		= null;
	private HSSFCellStyle	csBold	= null;


	public byte[] createExcel(List<OnsiteVisits> onsiteVisits) {

		byte[] output = null;
		try {

			HSSFWorkbook wb = new HSSFWorkbook();
			HSSFSheet sheet = wb.createSheet("Onsite Visits");

			// Setup some styles that we need for the Cells
			setCellStyles(wb);

			setColWidthAndMargin(sheet);

			int rowIndex = 0;
			
			rowIndex = insertHeaderInfo(sheet, rowIndex);
			rowIndex = insertJODetailInfoDM(sheet, rowIndex, onsiteVisits);

			// Write the Excel file
			//FileOutputStream fileOut = new FileOutputStream("c:/reports/myReport2.xls"); wb.write(fileOut);
			String timeStamp = new SimpleDateFormat("MMddyyyy_HHmmss").format(Calendar.getInstance().getTime());
			String fileName = "attachment;filename=OnsiteVisits"+timeStamp+".xls";
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

	private int insertJODetailInfoDM(HSSFSheet sheet, int index,List<OnsiteVisits> onsiteVisits) {
		int rowIndex = 0;
		int i=0;
		String strRow = null;
		String columnRow = null;
		System.out.println("entered insertJODetailInfoDM");
		try {
			
			Iterator<OnsiteVisits> ie =onsiteVisits.iterator();
			while(ie.hasNext()){
				rowIndex=index+i;
			OnsiteVisits o=(OnsiteVisits) ie.next();
			insertDetailInfoDM(sheet, rowIndex,o.getDmName(),o.getClient(),o.getVisitDate(),o.getPurpose());
			i++;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rowIndex;
	}

	private int insertDetailInfoDM(HSSFSheet sheet, int index, String dmName,String client,Date visitDate,String purpose) {

		HSSFRow row = null;
		HSSFCell c = null;
		SimpleDateFormat df = new SimpleDateFormat("MM-dd-yyyy hh:mm aa");
		row = sheet.createRow(index);	

		c = row.createCell(0);		
		c.setCellValue(dmName);
		c.setCellStyle(cs);

		c = row.createCell(1);		
		c.setCellValue(client);
		c.setCellStyle(cs);

		c = row.createCell(2);		
		c.setCellValue(df.format(visitDate));
		c.setCellStyle(cs);

		c = row.createCell(3);		
		c.setCellValue(purpose);
		c.setCellStyle(cs);
		
		return index;
	}


	public void setColWidthAndMargin(HSSFSheet sheet) {
		// Set Column Widths
		sheet.setColumnWidth(0, 5000);
		sheet.setColumnWidth(1, 4000);
		sheet.setColumnWidth(2, 5000);
		sheet.setColumnWidth(3, 5000);
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

	private int insertHeaderInfo(HSSFSheet sheet, int index) {
		System.out.println("entered insertHeaderInfo");
		int rowIndex = index;
		HSSFRow row = null;
		HSSFCell c = null;

		row = sheet.createRow(rowIndex);
		c = row.createCell(0);
		c.setCellValue("Name");
		c.setCellStyle(csBold);

		c = row.createCell(1);
		c.setCellValue("Client Name");
		c.setCellStyle(csBold);

		c = row.createCell(2);
		c.setCellValue("Visit Date");
		c.setCellStyle(csBold);		

		c = row.createCell(3);
		c.setCellValue("Purpose of visit");
		c.setCellStyle(csBold);

		rowIndex++;
		return rowIndex;
	}

	
}
