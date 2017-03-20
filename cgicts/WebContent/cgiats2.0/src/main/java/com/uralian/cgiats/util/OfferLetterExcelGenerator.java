package com.uralian.cgiats.util;

import java.io.Serializable;
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
import org.apache.poi.ss.usermodel.CellStyle;

import com.uralian.cgiats.model.OfferLetter;

public class OfferLetterExcelGenerator implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3283883567905690649L;
	
	private HSSFCellStyle	cs		= null;
	private HSSFCellStyle	csBold	= null;
	private boolean deleteFlag;


	public byte[] createExcel(List<OfferLetter> offers, boolean deletedOffersFlag) {

		byte[] output = null;
		setDeleteFlag(deletedOffersFlag);
		try {

			HSSFWorkbook wb = new HSSFWorkbook();
			HSSFSheet sheet = wb.createSheet("Offer Letter List");

			// Setup some styles that we need for the Cells
			setCellStyles(wb);

			setColWidthAndMargin(sheet);

			int rowIndex = 0;
			rowIndex = insertHeaderInfo(sheet, rowIndex);
			rowIndex = insertOfferLetter(sheet, rowIndex, offers);

			// Write the Excel file
			//FileOutputStream fileOut = new FileOutputStream("c:/reports/myReport2.xls"); wb.write(fileOut);
			String timeStamp = new SimpleDateFormat("MMddyyyy_HHmmss").format(Calendar.getInstance().getTime());
			String fileName = "attachment;filename=OfferLetter_"+timeStamp+".xls";
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
		sheet.setColumnWidth(0, 4000);
		sheet.setColumnWidth(1, 4000);
		sheet.setColumnWidth(2, 8000);	
		sheet.setColumnWidth(3, 10000);
		sheet.setColumnWidth(4, 8000);
		sheet.setColumnWidth(5, 7000);
		sheet.setColumnWidth(6, 5000);
		sheet.setColumnWidth(7, 5000);
		sheet.setColumnWidth(8, 10000);
		sheet.setColumnWidth(9, 5000);
		sheet.setColumnWidth(10, 5000);
		if(deleteFlag==true){
			sheet.setColumnWidth(11,15000);
		}
		
		
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
		cs.setAlignment(CellStyle.ALIGN_LEFT);
		
	}

	private int insertHeaderInfo(HSSFSheet sheet, int index) {

		int rowIndex = index;
		HSSFRow row = null;
		HSSFCell c = null;
		
		row = sheet.createRow(rowIndex);		
		
		c = row.createCell(0);
		c.setCellValue("JobOrder Id");
		c.setCellStyle(csBold);

		c = row.createCell(1);
		c.setCellValue("Candidate Id");
		c.setCellStyle(csBold);
		
		c = row.createCell(2);
		c.setCellValue("Candidate Name");
		c.setCellStyle(csBold);	
		
		c = row.createCell(3);
		c.setCellValue("Candidate Email");
		c.setCellStyle(csBold);	

		
		c = row.createCell(4);
		c.setCellValue("Location");
		c.setCellStyle(csBold);
		
		c = row.createCell(5);
		c.setCellValue("Company Name");
		c.setCellStyle(csBold);
		
		c = row.createCell(6);
		c.setCellValue("DM");
		c.setCellStyle(csBold);
		
		c = row.createCell(7);
		c.setCellValue("Recruiter");
		c.setCellStyle(csBold);
		
		c = row.createCell(8);
		c.setCellValue("Status");
		c.setCellStyle(csBold);
		
		c = row.createCell(9);
		if(!deleteFlag)
		c.setCellValue("Updated Date");
		else
		c.setCellValue("Deleted Date");
		c.setCellStyle(csBold);
		

		c = row.createCell(10);
		if(!deleteFlag)
		c.setCellValue("Updated By");
		else
		c.setCellValue("Deleted By");
		c.setCellStyle(csBold);
		

		if(deleteFlag==true){
			c = row.createCell(11);
			c.setCellValue("Reason");
			c.setCellStyle(csBold);
			
		}
		
		c.setCellStyle(csBold);
		
		rowIndex++;
		return rowIndex;
	}

	private int insertOfferLetterInfo(HSSFSheet sheet, int index, OfferLetter offerLetter) {

		HSSFRow row = null;
		HSSFCell c = null;
		SimpleDateFormat df = new SimpleDateFormat("MM-dd-yyyy MM:hh a");
		row = sheet.createRow(index);		
		
		
		c = row.createCell(0);		
		c.setCellValue(offerLetter.getJobOrderId());
		c.setCellStyle(cs);
		
		c = row.createCell(1);		
		c.setCellValue(offerLetter.getCandidate().getId());
		c.setCellStyle(cs);
		
		
		
		c = row.createCell(2);		
		c.setCellValue(offerLetter.getFullName());
		c.setCellStyle(cs);
		

		c = row.createCell(3);		
		c.setCellValue(offerLetter.getEmail());
		c.setCellStyle(cs);
		
		c = row.createCell(4);		
		c.setCellValue(offerLetter.getCandidate().getCityState());
		c.setCellStyle(cs);
		
		c = row.createCell(5);		
		c.setCellValue(offerLetter.getCompanyName());
		c.setCellStyle(cs);

		c = row.createCell(6);		
		c.setCellValue(offerLetter.getBdmName());
		c.setCellStyle(cs);
		
		c = row.createCell(7);		
		c.setCellValue(offerLetter.getRecruiterName());
		c.setCellStyle(cs);

		c = row.createCell(8);		
		c.setCellValue(offerLetter.getStatus().toString());
		c.setCellStyle(cs);

		
		c = row.createCell(9);	
		if(offerLetter.getUpdatedOn()!= null)
			c.setCellValue(df.format(offerLetter.getUpdatedOn()));	
		else
			c.setCellValue(df.format(offerLetter.getCreatedOn()));
		c.setCellStyle(cs);
		
		c=row.createCell(10);
		if(offerLetter.getUpdatedBy()!= null)
			c.setCellValue(offerLetter.getUpdatedBy());	
		else
			c.setCellValue(offerLetter.getCreatedBy());
		c.setCellStyle(cs);
		
		if(deleteFlag==true)
		{
			c = row.createCell(11);		
			c.setCellValue(offerLetter.getReason());
			c.setCellStyle(cs);
		}
		
		return index;
	}
	
	private int insertOfferLetter(HSSFSheet sheet, int index, List<OfferLetter> offers) {
		int rowIndex = 0;

		try {
			for (int i = 0; i < offers.size(); i++) {
				rowIndex = index + i;
				insertOfferLetterInfo(sheet, rowIndex, offers.get(i));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rowIndex;
	}

	public boolean isDeleteFlag() {
		return deleteFlag;
	}

	public void setDeleteFlag(boolean deleteFlag) {
		this.deleteFlag = deleteFlag;
	}
	
}

