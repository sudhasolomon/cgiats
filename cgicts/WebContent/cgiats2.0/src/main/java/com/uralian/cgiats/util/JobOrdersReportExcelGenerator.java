package com.uralian.cgiats.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
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

import com.uralian.cgiats.proxy.CGIATSConstants;


public class JobOrdersReportExcelGenerator {
	
	private HSSFCellStyle	cs		= null;
	private HSSFCellStyle	csBold	= null;

	public byte[] createExcel(Map<String, Object> totalOrders,
			Map<String, Object> periodOrders,
			Map<String, Map<String, Object>> userSubmittals,
			Map<String, Object> totals) {
		
		
		
		byte[] output = null;
		
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet("Job Orders Report");
		
		setCellStyles(wb);

		setColWidthAndMargin(sheet);
		
		
		int rowIndex = 0;
		rowIndex = insertHeader(sheet, rowIndex);
		rowIndex = insertReportsDetail(sheet,rowIndex,totalOrders,periodOrders,userSubmittals);
		rowIndex=insertFooter(sheet,rowIndex,totals);
		
		try {
		output = wb.getBytes();
		 FacesContext context = FacesContext.getCurrentInstance();
		 
	        HttpServletResponse res = (HttpServletResponse) context.getExternalContext().getResponse();
	        
	        String timeStamp = new SimpleDateFormat("MMddyyyy_HHmmss").format(Calendar.getInstance().getTime());
			String fileName = "attachment;filename=JobOrdersReport_"+timeStamp+".xls";
			
	        res.setContentType("application/vnd.ms-excel");
	        res.setHeader("Content-disposition", fileName);
	        ServletOutputStream out = res.getOutputStream();
	        wb.write(out);
	        out.flush();
	        out.close();
	        FacesContext.getCurrentInstance().responseComplete();
		 } 
		 catch (Exception e) {
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
		sheet.setColumnWidth(0, 5000);
		sheet.setColumnWidth(1, 4000);
		sheet.setColumnWidth(2, 5000);
		sheet.setColumnWidth(3, 4000);
		sheet.setColumnWidth(4, 3000);
		sheet.setColumnWidth(5, 3500);
		sheet.setColumnWidth(6, 4200);
		sheet.setColumnWidth(7, 4000);
		sheet.setColumnWidth(8, 3400);
		sheet.setColumnWidth(9, 3200);
		sheet.setColumnWidth(10,3400);
		sheet.setColumnWidth(11,4000);

		// Setup the Page margins - Left, Right, Top and Bottom
		sheet.setMargin(HSSFSheet.LeftMargin, 0.25);
		sheet.setMargin(HSSFSheet.RightMargin, 0.25);
		sheet.setMargin(HSSFSheet.TopMargin, 0.75);
		sheet.setMargin(HSSFSheet.BottomMargin, 0.75);
	}
	
	private int insertHeader(HSSFSheet sheet, int index) {

		int rowIndex = index;
		HSSFRow row = null;
		HSSFCell c = null;
		
		row = sheet.createRow(rowIndex);
		c = row.createCell(0);
		c.setCellValue("DM/ADM");
		c.setCellStyle(csBold);

		c = row.createCell(1);
		c.setCellValue("# Active JO's");
		c.setCellStyle(csBold);
		
		c = row.createCell(2);
		c.setCellValue("# Total Active JO's");
		c.setCellStyle(csBold);
		
		c = row.createCell(3);
		c.setCellValue("# SUBMITTED");
		c.setCellStyle(csBold);
		
		c = row.createCell(4);
		c.setCellValue("# DMREJ");
		c.setCellStyle(csBold);
		
		c = row.createCell(5);
		c.setCellValue("# ACCEPTED");
		c.setCellStyle(csBold);
		
		c = row.createCell(6);
		c.setCellValue("# INTERVIEWING");
		c.setCellStyle(csBold);
		
		c = row.createCell(7);
		c.setCellValue("# CONFIRMED");
		c.setCellStyle(csBold);
		
		c = row.createCell(8);
		c.setCellValue("# REJECTED");
		c.setCellStyle(csBold);
		
		c = row.createCell(9);
		c.setCellValue("STARTED");
		c.setCellStyle(csBold);
		
		c = row.createCell(10);
		c.setCellValue("# BACKOUT");
		c.setCellStyle(csBold);
		
		c = row.createCell(11);
		c.setCellValue("# OUTOFPROJ");
		c.setCellStyle(csBold);
		
		rowIndex++;
		return rowIndex;
	}
	
	private int insertReportsDetail(HSSFSheet sheet, int rowIndex, Map<String, Object> totalOrders,
			Map<String, Object> periodOrders,
			Map<String, Map<String, Object>> userSubmittals) {
		
		int index=rowIndex;
		try {
			
			for(String user:CGIATSConstants.DM_AND_ADM_NAMES){
				
				Map<String,Object> record=new HashMap<String, Object>();
		        record.put("user",user);
                record.put("totalActiveJO",(totalOrders.get(user)==null?0:totalOrders.get(user)));
                record.put("activeJO",(periodOrders.get(user)==null?0:periodOrders.get(user)));
		        
                Map<String,Object> subRecord=userSubmittals.get(user);
                
                if(subRecord!=null){
                	
                	for(String countName:subRecord.keySet()){
                         record.put(countName,subRecord.get(countName));                		
                	}
                	
                }
                else{
                	record.put("sub_count",0 );
    				record.put("dmrej_count",0 );
    				record.put("accepted_count",0 );
    				record.put("inter_count",0 );
    				record.put("confirm_count", 0);
    				record.put("rej_count",0 );
    				record.put("started_count",0 );
    				record.put("backout_count",0 );
    				record.put("outofproj_count",0 );
                }
              insertRow(sheet,index,record);
              index++;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		index++;
		return index;
	}

	private void insertRow(HSSFSheet sheet, int rowIndex, Map<String, Object> record) {
		
		HSSFRow row = null;
		HSSFCell c = null;
		
		row = sheet.createRow(rowIndex);
		
		c = row.createCell(0);
		c.setCellValue(record.get("user").toString());
		c.setCellStyle(cs);

		c = row.createCell(1);
		c.setCellValue(record.get("activeJO").toString());
		c.setCellStyle(cs);
		
		c = row.createCell(2);
		c.setCellValue(record.get("totalActiveJO").toString());
		c.setCellStyle(cs);
		
		c = row.createCell(3);
		c.setCellValue(record.get("sub_count").toString());
		c.setCellStyle(cs);
		
		c = row.createCell(4);
		c.setCellValue(record.get("dmrej_count").toString());
		c.setCellStyle(cs);

		c = row.createCell(5);
		c.setCellValue(record.get("accepted_count").toString());
		c.setCellStyle(cs);

		c = row.createCell(6);
		c.setCellValue(record.get("inter_count").toString());
		c.setCellStyle(cs);
		
		c = row.createCell(7);
		c.setCellValue(record.get("confirm_count").toString());
		c.setCellStyle(cs);
		c = row.createCell(8);
		c.setCellValue(record.get("rej_count").toString());
		c.setCellStyle(cs);
		
		c = row.createCell(9);
		c.setCellValue(record.get("started_count").toString());
		c.setCellStyle(cs);

		c = row.createCell(10);
		c.setCellValue(record.get("backout_count").toString());
		c.setCellStyle(cs);
		
		c = row.createCell(11);
		c.setCellValue(record.get("outofproj_count").toString());
		c.setCellStyle(cs);
	}
	
	
	private int insertFooter(HSSFSheet sheet, int rowIndex,	Map<String, Object> totals) {
		
		HSSFRow row = null;
		HSSFCell c = null;
		
		row = sheet.createRow(rowIndex);
		c = row.createCell(0);
		c.setCellValue("Total");
		c.setCellStyle(csBold);

		c = row.createCell(1);
		c.setCellValue(totals.get("activeJobs").toString());
		c.setCellStyle(cs);
		
		c = row.createCell(2);
		c.setCellValue(totals.get("totalActiveJobs").toString());
		c.setCellStyle(cs);
		
		c = row.createCell(3);
		c.setCellValue(totals.get("submittedTotal").toString());
		c.setCellStyle(cs);
		
		c = row.createCell(4);
		c.setCellValue(totals.get("dmrejTotal").toString());
		c.setCellStyle(cs);

		c = row.createCell(5);
		c.setCellValue(totals.get("acceptedTotal").toString());
		c.setCellStyle(cs);

		c = row.createCell(6);
		c.setCellValue(totals.get("interTotal").toString());
		c.setCellStyle(cs);
		
		c = row.createCell(7);
		c.setCellValue(totals.get("confTotal").toString());
		c.setCellStyle(cs);
		c = row.createCell(8);
		c.setCellValue(totals.get("rejectedTotal").toString());
		c.setCellStyle(cs);
		
		c = row.createCell(9);
		c.setCellValue(totals.get("startedTotal").toString());
		c.setCellStyle(cs);

		c = row.createCell(10);
		c.setCellValue(totals.get("backoutTotal").toString());
		c.setCellStyle(cs);
		
		c = row.createCell(11);
		c.setCellValue(totals.get("outOfProjTotal").toString());
		c.setCellStyle(cs);
		
		return rowIndex++;
	}

}
