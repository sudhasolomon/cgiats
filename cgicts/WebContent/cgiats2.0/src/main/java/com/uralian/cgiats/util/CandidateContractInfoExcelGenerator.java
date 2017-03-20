/**
 * 
 */
package com.uralian.cgiats.util;


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
import com.uralian.cgiats.model.ClientInfo;


/**
 * @author Radhika
 * ATS-254
 *
 */
public class CandidateContractInfoExcelGenerator {
	
	private HSSFCellStyle	cs		= null;
	private HSSFCellStyle	csBold	= null;
	
	public byte[] createExcel(List<ClientInfo> consultantInfo) {

		byte[] output = null;
		try {

			HSSFWorkbook wb = new HSSFWorkbook();
			HSSFSheet sheet = wb.createSheet("Consultant Status List");

			// Setup some styles that we need for the Cells
			setCellStyles(wb);

			setColWidthAndMargin(sheet);

			int rowIndex = 0;
			rowIndex = insertHeaderInfo(sheet, rowIndex);
			rowIndex = insertJODetailInfo(sheet, rowIndex, consultantInfo);

			// Write the Excel file
			//FileOutputStream fileOut = new FileOutputStream("c:/reports/myReport2.xls"); wb.write(fileOut);
			String timeStamp = new SimpleDateFormat("MMddyyyy_HHmmss").format(Calendar.getInstance().getTime());
			String fileName = "attachment;filename=ConsultantStatus_"+timeStamp+".xls";
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
		sheet.setColumnWidth(0, 2000);
		sheet.setColumnWidth(1, 3000);
		sheet.setColumnWidth(2, 3000);	
		sheet.setColumnWidth(3, 6000);
		sheet.setColumnWidth(4, 6000);
		sheet.setColumnWidth(5, 6000);
		sheet.setColumnWidth(6, 6000);
		sheet.setColumnWidth(7, 4000);
		sheet.setColumnWidth(8, 4000);
		sheet.setColumnWidth(9, 4000);

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
		c.setCellValue("Id");
		c.setCellStyle(csBold);
		
		c = row.createCell(1);
		c.setCellValue("Candidate Id");
		c.setCellStyle(csBold);
		
		c = row.createCell(2);
		c.setCellValue("JobOrder Id");
		c.setCellStyle(csBold);
		
		c = row.createCell(3);
		c.setCellValue("Consultant Name");
		c.setCellStyle(csBold);

		c = row.createCell(4);
		c.setCellValue("Email");
		c.setCellStyle(csBold);			

		c = row.createCell(5);
		c.setCellValue("StartDate");
		c.setCellStyle(csBold);
		
		c = row.createCell(6);
		c.setCellValue("EndDate");
		c.setCellStyle(csBold);
		
		c = row.createCell(7);
		c.setCellValue("Recruiter Name");
		c.setCellStyle(csBold);
		
		c = row.createCell(8);
		c.setCellValue("DM Name");
		c.setCellStyle(csBold);
		
		c = row.createCell(9);
		c.setCellValue("Client Name");
		c.setCellStyle(csBold);
		
		rowIndex++;
		return rowIndex;
	}

	private int insertDetailInfo(HSSFSheet sheet, int index, ClientInfo consultantInfo) {

		HSSFRow row = null;
		HSSFCell c = null;
		SimpleDateFormat df = new SimpleDateFormat("MM-dd-yyyy");
		row = sheet.createRow(index);	
		
		c = row.createCell(0);		
		c.setCellValue(consultantInfo.getCandidateinfo().getId());
		c.setCellStyle(cs);
		
		c = row.createCell(1);		
		c.setCellValue(consultantInfo.getCandidateinfo().getCandidate().getId());
		c.setCellStyle(cs);
		
		c = row.createCell(2);		
		c.setCellValue(consultantInfo.getCandidateinfo().getJobOrderId());
		c.setCellStyle(cs);
		
		c = row.createCell(3);		
		c.setCellValue(consultantInfo.getCandidateinfo().getFullName());
		c.setCellStyle(cs);

		c = row.createCell(4);		
		c.setCellValue(consultantInfo.getCandidateinfo().getEmail());
		c.setCellStyle(cs);

		c = row.createCell(5);	
		if(consultantInfo.getCandidateinfo().getStartDate() != null)
		c.setCellValue(df.format(consultantInfo.getCandidateinfo().getStartDate()));	
		else
		c.setCellValue("");
		c.setCellStyle(cs);
		
		c = row.createCell(6);	
		if(consultantInfo.getCandidateinfo().getEndDate() != null)
			c.setCellValue(df.format(consultantInfo.getCandidateinfo().getEndDate()));
		else
			c.setCellValue("");
		c.setCellStyle(cs);
		
		c = row.createCell(7);		
		c.setCellValue(consultantInfo.getCandidateinfo().getRecruiterName());
		c.setCellStyle(cs);	
		
		c = row.createCell(8);		
		c.setCellValue(consultantInfo.getCandidateinfo().getBdmName());
		c.setCellStyle(cs);	
		
		c = row.createCell(9);		
		c.setCellValue(consultantInfo.getClientName());
		c.setCellStyle(cs);	
		
		return index;
	}
	
	private int insertJODetailInfo(HSSFSheet sheet, int index, List<ClientInfo> consultantInfo) {
		int rowIndex = 0;

		try {
			for (int i = 0; i < consultantInfo.size(); i++) {
				rowIndex = index + i;
				insertDetailInfo(sheet, rowIndex, consultantInfo.get(i));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rowIndex;
	}
	
}
