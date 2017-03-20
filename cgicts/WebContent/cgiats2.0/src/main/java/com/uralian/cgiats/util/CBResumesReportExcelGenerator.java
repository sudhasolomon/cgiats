/**
 * 
 */
package com.uralian.cgiats.util;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
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

import com.uralian.cgiats.model.CBUserViews;


/**
 * @author Chaitanya
 * 
 * For Generating Report for CB Portal Download Resumes Report
 *
 */
public class CBResumesReportExcelGenerator {	

	private HSSFCellStyle	cs		= null;
	private HSSFCellStyle	csBold	= null;
	public byte[] createExcel(List<CBUserViews> cbUserViewsLst) {

		byte[] output = null;
		try {

			HSSFWorkbook wb = new HSSFWorkbook();
			HSSFSheet sheet = wb.createSheet("CB Resumes Report");

			// Setup some styles that we need for the Cells
			setCellStyles(wb);

			setColWidthAndMargin(sheet);

			int rowIndex = 0;

			rowIndex = insertHeaderInfo(sheet, rowIndex);
			rowIndex = insertCbDetailInfoDM(sheet, rowIndex, cbUserViewsLst);

			// Write the Excel file
			//FileOutputStream fileOut = new FileOutputStream("c:/reports/myReport2.xls"); wb.write(fileOut);
			String timeStamp = new SimpleDateFormat("MMddyyyy_HHmmss").format(Calendar.getInstance().getTime());
			String fileName = "attachment;filename=CBResumesReport"+timeStamp+".xls";
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

	private int insertCbDetailInfoDM(HSSFSheet sheet, int index,List<CBUserViews> cbUserViewsLst) {
		int rowIndex = 0;
		int i=0;
		int cbTotalLimitViews=0;
		int cbTotalDownloadViews=0;
		int cbTotalRemainingViews=0;
		try {
			Iterator<CBUserViews> ie =cbUserViewsLst.iterator();
			while(ie.hasNext()){
				rowIndex=index+i;
				CBUserViews uv=(CBUserViews) ie.next();
				int totalViews=uv.getTotalViews()!=null && !uv.getTotalViews().trim().equals("")?Integer.parseInt(uv.getTotalViews()):0;
				int completedViews=uv.getCompletedViews()!=null && !uv.getCompletedViews().trim().equals("")?Integer.parseInt(uv.getCompletedViews()):0;
				int remainingViews=totalViews-completedViews;
				cbTotalLimitViews=cbTotalLimitViews+totalViews;
				cbTotalDownloadViews=cbTotalDownloadViews+completedViews;
				cbTotalRemainingViews=cbTotalRemainingViews+remainingViews;
				insertCbDetailInfo(sheet, rowIndex,uv.getPortalUserId(),uv.getViewDate(),totalViews,completedViews,remainingViews);
				i++;
			}

			insertFooterInfo(sheet, i+1,cbTotalLimitViews,cbTotalDownloadViews,cbTotalRemainingViews);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return rowIndex;
	}
	private int insertFooterInfo(HSSFSheet sheet, int index,int cbTotalLimitViews,int cbTotalDownloadViews,int cbTotalRemainingViews) {
		int rowIndex = index;
		HSSFRow row = null;
		HSSFCell c = null;

		row = sheet.createRow(rowIndex);
		c = row.createCell(0);
		c.setCellValue("");
		c.setCellStyle(csBold);

		c = row.createCell(1);
		c.setCellValue("Total:");
		c.setCellStyle(csBold);

		c = row.createCell(2);
		c.setCellValue(cbTotalLimitViews);
		c.setCellStyle(csBold);		

		c = row.createCell(3);
		c.setCellValue(cbTotalDownloadViews);
		c.setCellStyle(csBold);

		c = row.createCell(4);
		c.setCellValue(cbTotalRemainingViews);
		c.setCellStyle(csBold);

		return rowIndex;
	}
	private int insertCbDetailInfo(HSSFSheet sheet, int index, String userId,Date viewDate,int totalViews,int completedViews,int remainingViews) {

		HSSFRow row = null;
		HSSFCell c = null;
		SimpleDateFormat df = new SimpleDateFormat("MM-dd-yyyy");
		row = sheet.createRow(index);	

		c = row.createCell(0);		
		c.setCellValue(userId);
		c.setCellStyle(cs);

		c = row.createCell(1);		
		c.setCellValue(df.format(viewDate));
		c.setCellStyle(cs);

		c = row.createCell(2);		
		c.setCellValue(totalViews);
		c.setCellStyle(cs);

		c = row.createCell(3);		
		c.setCellValue(completedViews);
		c.setCellStyle(cs);

		c = row.createCell(4);		
		c.setCellValue(remainingViews);
		c.setCellStyle(cs);

		return index;
	}

	public void setColWidthAndMargin(HSSFSheet sheet) {
		// Set Column Widths
		sheet.setColumnWidth(0, 8000);
		sheet.setColumnWidth(1, 4000);
		sheet.setColumnWidth(2, 5000);
		sheet.setColumnWidth(3, 5000);
		sheet.setColumnWidth(4, 5000);
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
		int rowIndex = index;
		HSSFRow row = null;
		HSSFCell c = null;

		row = sheet.createRow(rowIndex);
		c = row.createCell(0);
		c.setCellValue("UserId");
		c.setCellStyle(csBold);

		c = row.createCell(1);
		c.setCellValue("Viewed Date");
		c.setCellStyle(csBold);

		c = row.createCell(2);
		c.setCellValue("Total Views");
		c.setCellStyle(csBold);		

		c = row.createCell(3);
		c.setCellValue("Completed Views");
		c.setCellStyle(csBold);

		c = row.createCell(4);
		c.setCellValue("Remaining Views");
		c.setCellStyle(csBold);

		rowIndex++;
		return rowIndex;
	}

	
	
	
	public byte[] createExcel(Map<String, Integer> resumeStatsByUser,List<String> portalEmails) {
		
		byte[] output = null;
		try {

			HSSFWorkbook wb = new HSSFWorkbook();
			HSSFSheet sheet = wb.createSheet("Monster Resumes Report");

			// Setup some styles that we need for the Cells
			setCellStyles(wb);

			setColWidthAndMargin(sheet);

			int rowIndex = 0;

			rowIndex = insertHeaderInfo1(sheet, rowIndex);
			rowIndex = insertCBDetailInfo1(sheet, rowIndex,resumeStatsByUser,portalEmails);

			// Write the Excel file
			//FileOutputStream fileOut = new FileOutputStream("c:/reports/myReport2.xls"); wb.write(fileOut);
			String timeStamp = new SimpleDateFormat("MMddyyyy_HHmmss").format(Calendar.getInstance().getTime());
			String fileName = "attachment;filename=CBIdResumesReport"+timeStamp+".xls";
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
	
	private int insertHeaderInfo1(HSSFSheet sheet, int index) {
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
	
	
	private int insertCBDetailInfo1(HSSFSheet sheet, int index,Map<String, Integer> resumeStatsByUser,List<String> portalEmails) {
		int rowIndex = 0;
		int i=0;
		try {
			int resumeTotal = 0;
			Iterator<String> ie =portalEmails.iterator();
			String name=null;
			while(ie.hasNext()){
				for (Integer count : resumeStatsByUser.values()){
					rowIndex=index+i;
					name=(String)ie.next();
					resumeTotal += count;
					insertCBDetailInfo1(sheet, rowIndex,name,count);
					i++;
				}
			}
				
			insertFooterInfo1(sheet, i+1,resumeTotal);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return rowIndex;
	}
	private int insertCBDetailInfo1(HSSFSheet sheet, int index, String userId,int count) {

		HSSFRow row = null;
		HSSFCell c = null;
		row = sheet.createRow(index);	

		c = row.createCell(0);		
		c.setCellValue(userId);
		c.setCellStyle(cs);
		
		c = row.createCell(1);		
		c.setCellValue(count);
		c.setCellStyle(cs);

		return index;
	}
	private int insertFooterInfo1(HSSFSheet sheet,int index, int resumeTotal) {
		int rowIndex = index;
		HSSFRow row = null;
		HSSFCell c = null;

		row = sheet.createRow(rowIndex);
		c = row.createCell(0);
		c.setCellValue("Total:");
		c.setCellStyle(csBold);
		
		c = row.createCell(1);
		c.setCellValue(resumeTotal);
		c.setCellStyle(csBold);

		return rowIndex;
	}

}
