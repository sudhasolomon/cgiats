/**
 * 
 */
package com.uralian.cgiats.util;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

/**
 * @author Parameshwar
 *
 */
public class PortalResumesExcelGenerator {	

	private HSSFCellStyle	cs		= null;
	private HSSFCellStyle	csBold	= null;


	public byte[] createExcel(Map<String, Map<String, Integer>> portalNameslst) {

		byte[] output = null;
		try {

			HSSFWorkbook wb = new HSSFWorkbook();
			HSSFSheet sheet = wb.createSheet("Portal Resumes Report");

			// Setup some styles that we need for the Cells
			setCellStyles(wb);

			setColWidthAndMargin(sheet);

			int rowIndex = 0;
			rowIndex = insertHeaderInfo(sheet, rowIndex);
			rowIndex = insertDetailInfoDM(sheet, rowIndex, portalNameslst);

			// Write the Excel file
			//FileOutputStream fileOut = new FileOutputStream("c:/reports/myReport2.xls"); wb.write(fileOut);
			String timeStamp = new SimpleDateFormat("MMddyyyy_HHmmss").format(Calendar.getInstance().getTime());
			String fileName = "attachment;filename=PortalResumesReport"+timeStamp+".xls";
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

	private int insertDetailInfoDM(HSSFSheet sheet, int index, Map<String, Map<String, Integer>> portalNameslst) {
		HSSFRow row = null;
		HSSFCell c = null;
		String month=null;
		SimpleDateFormat df = new SimpleDateFormat("MM-dd-yyyy");

		for (String user : portalNameslst.keySet())
		{
			String q="",q1="";
			Map<String,Integer> map = portalNameslst.get(user);
			row = sheet.createRow(index);	
			String[] user1=user!=null?user.split("|"):null;
			for(int i=0;i<user1.length;i++){
				if(user1[i]!=null && !user1[i].trim().equals("|")){
					q=user1[i].trim();
					q1=q1+q;
				}else
					break;
			}
			c = row.createCell(0);		
			c.setCellValue(q1);
			c.setCellStyle(cs);
			List<String>maps = new ArrayList<String>(map.keySet());
			if(map!=null && map.size()>0){

				for (Map.Entry<String, Integer> entry : map.entrySet())
				{		
					month = entry.getKey();
					Integer count = entry.getValue()!=null && !entry.getValue().equals("")?entry.getValue():0;
					c = row.createCell(1);		
					c.setCellValue(month);
					c.setCellStyle(cs);
					c = row.createCell(2);		
					c.setCellValue(count);
					c.setCellStyle(cs);
				}
			}
			index++;
		}
		return index;
	}

	public void setColWidthAndMargin(HSSFSheet sheet) {
		// Set Column Widths
		sheet.setColumnWidth(0, 5000);
		sheet.setColumnWidth(1, 4000);
		sheet.setColumnWidth(2, 7000);

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
		c.setCellValue("Portal Name");
		c.setCellStyle(csBold);

		c = row.createCell(1);
		c.setCellValue("Month");
		c.setCellStyle(csBold);

		c = row.createCell(2);
		c.setCellValue("No. Of Resumes Submitted");
		c.setCellStyle(csBold);		

		rowIndex++;
		return rowIndex;
	}
	//All Portal Resumes

	public void setColWidthAndMarginAllPortal(HSSFSheet sheet) {
		// Set Column Widths
		sheet.setColumnWidth(0, 5000);
		sheet.setColumnWidth(1, 4000);
		sheet.setColumnWidth(2, 9000);

		//		sheet.setColumnWidth(5, 3500);
		// Setup the Page margins - Left, Right, Top and Bottom
		sheet.setMargin(HSSFSheet.LeftMargin, 0.25);
		sheet.setMargin(HSSFSheet.RightMargin, 0.25);
		sheet.setMargin(HSSFSheet.TopMargin, 0.75);
		sheet.setMargin(HSSFSheet.BottomMargin, 0.75);
	}
	public byte[] createExcelAllPortal(Map<String, Map<String, Integer>> portalNameslst,String status) {

		byte[] output = null;
		try {

			HSSFWorkbook wb = new HSSFWorkbook();
			HSSFSheet sheet = wb.createSheet("All Portal Resumes Report");

			// Setup some styles that we need for the Cells
			setCellStyles(wb);

			setColWidthAndMarginAllPortal(sheet);

			int rowIndex = 0;
			rowIndex = insertHeaderInfoAllPortal(sheet, rowIndex,status);
			rowIndex = insertDetailInfoAllPortal(sheet, rowIndex, portalNameslst);

			// Write the Excel file
			//FileOutputStream fileOut = new FileOutputStream("c:/reports/myReport2.xls"); wb.write(fileOut);
			String timeStamp = new SimpleDateFormat("MMddyyyy_HHmmss").format(Calendar.getInstance().getTime());
			String fileName = "attachment;filename=AllPortalResumesReport"+timeStamp+".xls";
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
	private int insertHeaderInfoAllPortal(HSSFSheet sheet, int index,String status) {
		System.out.println("entered insertHeaderInfo");
		int rowIndex = index;
		HSSFRow row = null;
		HSSFCell c = null;

		row = sheet.createRow(rowIndex);
		c = row.createCell(0);
		c.setCellValue("Portal Name");
		c.setCellStyle(csBold);

		c = row.createCell(1);
		c.setCellValue("Month");
		c.setCellStyle(csBold);

		c = row.createCell(2);
		c.setCellValue("Count Of "+status+" Resumes");
		c.setCellStyle(csBold);		

		rowIndex++;
		return rowIndex;
	}

	private int insertDetailInfoAllPortal(HSSFSheet sheet, int index, Map<String, Map<String, Integer>> portalNameslst) {
		HSSFRow row = null;
		HSSFCell c = null;
		String month=null;
		SimpleDateFormat df = new SimpleDateFormat("MM-dd-yyyy");

		for (String user : portalNameslst.keySet())
		{
			String q="",q1="";
			Map<String,Integer> map = portalNameslst.get(user);
			row = sheet.createRow(index);	
			String[] user1=user!=null?user.split("|"):null;
			for(int i=0;i<user1.length;i++){
				if(user1[i]!=null && !user1[i].trim().equals("|")){
					q=user1[i].trim();
					q1=q1+q;
				}else
					break;
			}
			c = row.createCell(0);		
			c.setCellValue(q1);
			c.setCellStyle(cs);
			List<String>maps = new ArrayList<String>(map.keySet());
			if(map!=null && map.size()>0){

				for (Map.Entry<String, Integer> entry : map.entrySet())
				{		
					month = entry.getKey();
					Integer count = entry.getValue()!=null && !entry.getValue().equals("")?entry.getValue():0;
					c = row.createCell(1);		
					c.setCellValue(month);
					c.setCellStyle(cs);
					c = row.createCell(2);		
					c.setCellValue(count);
					c.setCellStyle(cs);
				}
			}
			index++;
		}
		return index;
	}
}
