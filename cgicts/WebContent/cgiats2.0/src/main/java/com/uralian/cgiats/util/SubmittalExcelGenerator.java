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

import com.uralian.cgiats.model.SubmittalStatus;

/**
 * @author Parameshwar
 * 
 */
public class SubmittalExcelGenerator {

	private HSSFCellStyle cs = null;
	private HSSFCellStyle csBold = null;

	public byte[] createExcel(
			Map<String, Map<SubmittalStatus, Integer>> submittalStatusByUser,
			Map<String, String> assignedBdms, Map<String, String> bdmLocations) {

		byte[] output = null;
		try {

			HSSFWorkbook wb = new HSSFWorkbook();
			HSSFSheet sheet = wb.createSheet("Submittals Stats");

			// Setup some styles that we need for the Cells
			setCellStyles(wb);
			setColWidthAndMargin(sheet);

			int rowIndex = 0;
			rowIndex = insertHeaderInfo(sheet, rowIndex, "RECRUITER");
			System.out.println("rowIndex:::" + rowIndex);
			rowIndex = insertSubmittalsInfo(sheet, rowIndex,
					submittalStatusByUser, assignedBdms, bdmLocations);
			String timeStamp = new SimpleDateFormat("MMddyyyy_HHmmss")
					.format(Calendar.getInstance().getTime());
			String fileName = "attachment;filename=Submittal_" + timeStamp
					+ ".xls";
			output = wb.getBytes();
			System.out.println("Workbook length in bytes: " + output.length);
			FacesContext context = FacesContext.getCurrentInstance();
			HttpServletResponse res = (HttpServletResponse) context
					.getExternalContext().getResponse();
			res.setContentType("application/vnd.ms-excel");
			res.setHeader("Content-disposition", fileName);
			ServletOutputStream out = res.getOutputStream();
			wb.write(out);
			out.flush();
			out.close();
			FacesContext.getCurrentInstance().responseComplete();

		} catch (Exception e) {
			System.out
					.println("Exception in exporting all records to excel :: "
							+ e);
			e.printStackTrace();
		}
		return output;
	}

	public byte[] createDMActivityExcel(
			Map<String, Map<SubmittalStatus, Integer>> submittalStatusByUser,
			Map<String, String> assignedBdms, Map<String, String> bdmLocations,
			Map<String, Integer> approvedPercentage,
			Map<String, Integer> interviewingPercentage,
			Map<String, Integer> weeklyGoalPercentage) {

		byte[] output = null;
		try {

			HSSFWorkbook wb = new HSSFWorkbook();
			HSSFSheet sheet = wb.createSheet("DM Activity Report");

			// Setup some styles that we need for the Cells
			setCellStyles(wb);
			setColWidthAndMargin(sheet);

			int rowIndex = 0;
			rowIndex = insertHeaderInfoSapeareReport(sheet, rowIndex,
					"RECRUITER");
			System.out.println("rowIndex:::" + rowIndex);
			rowIndex = insertSubmittalsInfoSapereReport(sheet, rowIndex,
					submittalStatusByUser, assignedBdms, bdmLocations,
					approvedPercentage, interviewingPercentage,
					weeklyGoalPercentage);
			String timeStamp = new SimpleDateFormat("MMddyyyy_HHmmss")
					.format(Calendar.getInstance().getTime());
			String fileName = "attachment;filename=DM_Activity_Report_"
					+ timeStamp + ".xls";
			output = wb.getBytes();
			System.out.println("Workbook length in bytes: " + output.length);
			FacesContext context = FacesContext.getCurrentInstance();
			HttpServletResponse res = (HttpServletResponse) context
					.getExternalContext().getResponse();
			res.setContentType("application/vnd.ms-excel");
			res.setHeader("Content-disposition", fileName);
			ServletOutputStream out = res.getOutputStream();
			wb.write(out);
			out.flush();
			out.close();
			FacesContext.getCurrentInstance().responseComplete();

		} catch (Exception e) {
			System.out
					.println("Exception in exporting all records to excel :: "
							+ e);
			e.printStackTrace();
		}
		return output;
	}

	public byte[] createDMActivityExcel(
			Map<String, Map<SubmittalStatus, Integer>> submittalStatusByUser,
			Map<String, String> assignedBdms, Map<String, String> bdmLocations) {

		byte[] output = null;
		try {

			HSSFWorkbook wb = new HSSFWorkbook();
			HSSFSheet sheet = wb.createSheet("DM Activity Report");

			// Setup some styles that we need for the Cells
			setCellStyles(wb);
			setColWidthAndMargin(sheet);

			int rowIndex = 0;
			rowIndex = insertHeaderInfoActivityReport(sheet, rowIndex,
					"RECRUITER");
			System.out.println("rowIndex:::" + rowIndex);
			rowIndex = insertSubmittalsInfoActivityReport(sheet, rowIndex,
					submittalStatusByUser, assignedBdms, bdmLocations);
			String timeStamp = new SimpleDateFormat("MMddyyyy_HHmmss")
					.format(Calendar.getInstance().getTime());
			String fileName = "attachment;filename=DM_Activity_Report_"
					+ timeStamp + ".xls";
			output = wb.getBytes();
			System.out.println("Workbook length in bytes: " + output.length);
			FacesContext context = FacesContext.getCurrentInstance();
			HttpServletResponse res = (HttpServletResponse) context
					.getExternalContext().getResponse();
			res.setContentType("application/vnd.ms-excel");
			res.setHeader("Content-disposition", fileName);
			ServletOutputStream out = res.getOutputStream();
			wb.write(out);
			out.flush();
			out.close();
			FacesContext.getCurrentInstance().responseComplete();

		} catch (Exception e) {
			System.out
					.println("Exception in exporting all records to excel :: "
							+ e);
			e.printStackTrace();
		}
		return output;
	}

	public byte[] createRecActivityExcel(
			Map<String, Map<SubmittalStatus, Integer>> submittalStatusByUser,
			Map<String, String> assignedBdms, Map<String, String> bdmLocations) {

		byte[] output = null;
		try {

			HSSFWorkbook wb = new HSSFWorkbook();
			HSSFSheet sheet = wb.createSheet("Recruiter Activity Report");

			// Setup some styles that we need for the Cells
			setCellStyles(wb);
			setColWidthAndMargin(sheet);

			int rowIndex = 0;
			rowIndex = insertHeaderInfo(sheet, rowIndex, "RECRUITER");
			System.out.println("rowIndex:::" + rowIndex);
			rowIndex = insertSubmittalsInfo(sheet, rowIndex,
					submittalStatusByUser, assignedBdms, bdmLocations);
			String timeStamp = new SimpleDateFormat("MMddyyyy_HHmmss")
					.format(Calendar.getInstance().getTime());
			String fileName = "attachment;filename=Recruiter_Activity_Report_"
					+ timeStamp + ".xls";
			output = wb.getBytes();
			System.out.println("Workbook length in bytes: " + output.length);
			FacesContext context = FacesContext.getCurrentInstance();
			HttpServletResponse res = (HttpServletResponse) context
					.getExternalContext().getResponse();
			res.setContentType("application/vnd.ms-excel");
			res.setHeader("Content-disposition", fileName);
			ServletOutputStream out = res.getOutputStream();
			wb.write(out);
			out.flush();
			out.close();
			FacesContext.getCurrentInstance().responseComplete();

		} catch (Exception e) {
			System.out
					.println("Exception in exporting all records to excel :: "
							+ e);
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
		sheet.setColumnWidth(6, 4000);
		sheet.setColumnWidth(7, 3000);
		sheet.setColumnWidth(8, 3000);
		sheet.setColumnWidth(9, 3000);
		sheet.setColumnWidth(10, 3000);
		sheet.setColumnWidth(11, 4000);

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

	private int insertHeaderInfo(HSSFSheet sheet, int index, String role) {

		int rowIndex = index;
		HSSFRow row = null;
		HSSFCell c = null;

		row = sheet.createRow(rowIndex);
		c = row.createCell(0);
		c.setCellValue(role);
		c.setCellStyle(csBold);

		c = row.createCell(1);
		c.setCellValue("DM");
		c.setCellStyle(csBold);

		c = row.createCell(2);
		c.setCellValue("LOC");
		c.setCellStyle(csBold);

		c = row.createCell(3);
		c.setCellValue("SUBMITTED");
		c.setCellStyle(csBold);

		c = row.createCell(4);
		c.setCellValue("DMREJ");
		c.setCellStyle(csBold);

		c = row.createCell(5);
		c.setCellValue("ACCEPTED");
		c.setCellStyle(csBold);

		c = row.createCell(6);
		c.setCellValue("INTERVIEWING");
		c.setCellStyle(csBold);

		c = row.createCell(7);
		c.setCellValue("CONFIRMED");
		c.setCellStyle(csBold);

		c = row.createCell(8);
		c.setCellValue("REJECTED");
		c.setCellStyle(csBold);

		c = row.createCell(9);
		c.setCellValue("STARTED");
		c.setCellStyle(csBold);

		c = row.createCell(10);
		c.setCellValue("BACKOUT");
		c.setCellStyle(csBold);

		c = row.createCell(11);
		c.setCellValue("OUTOFPROJ");
		c.setCellStyle(csBold);

		c = row.createCell(12);
		c.setCellValue("STATUS NOT UPDATED");
		c.setCellStyle(csBold);

		rowIndex++;
		return rowIndex;
	}

	private int insertHeaderInfoSapeareReport(HSSFSheet sheet, int index,
			String role) {

		int rowIndex = index;
		HSSFRow row = null;
		HSSFCell c = null;

		row = sheet.createRow(rowIndex);

		c = row.createCell(0);
		c.setCellValue("RECRUITER/ADM");
		c.setCellStyle(csBold);

		c = row.createCell(1);
		c.setCellValue("SUBMITTED");
		c.setCellStyle(csBold);

		c = row.createCell(2);
		c.setCellValue("DMREJ");
		c.setCellStyle(csBold);

		c = row.createCell(3);
		c.setCellValue("ACCEPTED");
		c.setCellStyle(csBold);

		c = row.createCell(4);
		c.setCellValue("INTERVIEWING");
		c.setCellStyle(csBold);

	/*	c = row.createCell(5);
		c.setCellValue("CONFIRMED");
		c.setCellStyle(csBold);*/

		c = row.createCell(5);
		c.setCellValue("REJECTED");
		c.setCellStyle(csBold);

		c = row.createCell(6);
		c.setCellValue("STARTED");
		c.setCellStyle(csBold);

		/*c = row.createCell(8);
		c.setCellValue("BACKOUT");
		c.setCellStyle(csBold);

		c = row.createCell(9);
		c.setCellValue("OUTOFPROJ");
		c.setCellStyle(csBold);*/

		c = row.createCell(7);
		c.setCellValue("ACCEPTED % SUBMITTED");
		c.setCellStyle(csBold);

		c = row.createCell(8);
		c.setCellValue("INTERVIEWING %  SUBMITTED");
		c.setCellStyle(csBold);

		c = row.createCell(9);
		c.setCellValue("Weekly Goal of 4 Approved %");
		c.setCellStyle(csBold);

		rowIndex++;
		return rowIndex;
	}

	private int insertHeaderInfoActivityReport(HSSFSheet sheet, int index,
			String role) {

		int rowIndex = index;
		HSSFRow row = null;
		HSSFCell c = null;

		row = sheet.createRow(rowIndex);

		c = row.createCell(0);
		c.setCellValue("RECRUITER/ADM");
		c.setCellStyle(csBold);

		c = row.createCell(1);
		c.setCellValue("SUBMITTED");
		c.setCellStyle(csBold);

		c = row.createCell(2);
		c.setCellValue("DMREJ");
		c.setCellStyle(csBold);

		c = row.createCell(3);
		c.setCellValue("ACCEPTED");
		c.setCellStyle(csBold);

		c = row.createCell(4);
		c.setCellValue("INTERVIEWING");
		c.setCellStyle(csBold);

		c = row.createCell(5);
		c.setCellValue("CONFIRMED");
		c.setCellStyle(csBold);

		c = row.createCell(6);
		c.setCellValue("REJECTED");
		c.setCellStyle(csBold);

		c = row.createCell(7);
		c.setCellValue("STARTED");
		c.setCellStyle(csBold);

		c = row.createCell(8);
		c.setCellValue("BACKOUT");
		c.setCellStyle(csBold);

		c = row.createCell(9);
		c.setCellValue("OUTOFPROJ");
		c.setCellStyle(csBold);

		rowIndex++;
		return rowIndex;
	}

	private int insertDetailInfo(HSSFSheet sheet, int index, String recId,
			String bdmName, String bdmLocation, String strRow) {

		HSSFRow row = null;
		HSSFCell c = null;
		row = sheet.createRow(index);

		c = row.createCell(0);
		c.setCellValue(recId);
		c.setCellStyle(cs);

		c = row.createCell(1);
		c.setCellValue(bdmName);
		c.setCellStyle(cs);

		c = row.createCell(2);
		c.setCellValue(bdmLocation);
		c.setCellStyle(cs);
		StringTokenizer st = new StringTokenizer(strRow, ",");

		int i = 3;
		while (st.hasMoreTokens()) {
			c = row.createCell(i);
			c.setCellValue(st.nextToken());
			c.setCellStyle(cs);
			i = i + 1;
		}
		return index;
	}

	private int insertDetailInfoActivityReport(HSSFSheet sheet, int index,
			String recId, String bdmName, String bdmLocation, String strRow,
			Integer total) {

		HSSFRow row = null;
		HSSFCell c = null;
		row = sheet.createRow(index);

		c = row.createCell(0);
		c.setCellValue(recId);
		c.setCellStyle(cs);

		/*
		 * c = row.createCell(1); c.setCellValue(bdmName); c.setCellStyle(cs);
		 * 
		 * c = row.createCell(2); c.setCellValue(bdmLocation);
		 * c.setCellStyle(cs);
		 * 
		 * c = row.createCell(3); c.setCellValue(total); c.setCellStyle(cs);
		 */
		StringTokenizer st = new StringTokenizer(strRow, ",");

		int i = 1;
		while (st.hasMoreTokens()) {
			c = row.createCell(i);
			c.setCellValue(st.nextToken());
			c.setCellStyle(cs);
			i = i + 1;
		}
		return index;
	}

	private int insertDetailInfoTotal(HSSFSheet sheet, int index,
			String columnRow) {
		HSSFRow row = null;
		HSSFCell c = null;
		row = sheet.createRow(index + 2);

		c = row.createCell(0);
		c.setCellValue("TOTAL:");
		c.setCellStyle(csBold);

		c = row.createCell(1);
		c.setCellValue("");
		c.setCellStyle(cs);

		c = row.createCell(2);
		c.setCellValue("");
		c.setCellStyle(cs);
		StringTokenizer st1 = new StringTokenizer(columnRow, ",");

		int i = 3;
		while (st1.hasMoreTokens()) {
			c = row.createCell(i);
			c.setCellValue(st1.nextToken());
			c.setCellStyle(cs);
			i = i + 1;
		}
		return index;
	}

	private int insertDetailInfoTotalActivityReport(HSSFSheet sheet, int index,
			String columnRow) {
		HSSFRow row = null;
		HSSFCell c = null;
		row = sheet.createRow(index + 2);

		c = row.createCell(0);
		c.setCellValue("TOTAL:");
		c.setCellStyle(csBold);

		/*
		 * c = row.createCell(1); c.setCellValue(""); c.setCellStyle(cs);
		 * 
		 * c = row.createCell(2); c.setCellValue(""); c.setCellStyle(cs);
		 */
		StringTokenizer st1 = new StringTokenizer(columnRow, ",");

		int i = 1;
		while (st1.hasMoreTokens()) {
			c = row.createCell(i);
			c.setCellValue(st1.nextToken());
			c.setCellStyle(cs);
			i = i + 1;
		}
		return index;
	}

	private int insertSubmittalsInfo(HSSFSheet sheet, int index,
			Map<String, Map<SubmittalStatus, Integer>> submittalStatusByUser,
			Map<String, String> assignedBdms, Map<String, String> bdmLocations) {

		int rowIndex = 0;
		Integer recBDMREJ = 0;
		Integer recOUTOFPROJ = 0;
		Integer recACCEPTED = 0;
		Integer recSUBMITTED = 0;
		Integer recINTERVIEWING = 0;
		Integer recCONFIRMED = 0;
		Integer recSTARTED = 0;
		Integer recBACKOUT = 0;
		Integer recCLOSED = 0;
		Integer recREJECTED = 0;
		Integer recACCEPTEDTotal = 0;
		Integer recSUBMITTEDTotal = 0;
		Integer recINTERVIEWINGTotal = 0;
		Integer recCONFIRMEDTotal = 0;
		Integer recSTARTEDTotal = 0;
		Integer recBACKOUTTotal = 0;
		Integer recCLOSEDTotal = 0;
		Integer recREJECTEDTotal = 0;
		Integer recBDMREJTotal = 0;
		Integer recOUTOFPROJTotal = 0;
		String strRow = null;
		String recCount = null;
		String recColValue = null;
		int total = 0;
		int count = 0;
		String columnRow = null;
		int statusNuTotal = 0;

		try {

			for (Map.Entry<String, Map<SubmittalStatus, Integer>> entry : submittalStatusByUser
					.entrySet()) {
				rowIndex = rowIndex + 1;
				String recName = entry.getKey();
				String bdmName = assignedBdms.get(recName);
				if (bdmName == null) {
					bdmName = "";
				}
				String bdmLocation = bdmLocations.get(recName);
				if (bdmLocation == null) {
					bdmLocation = "";
				}
				for (Map.Entry<SubmittalStatus, Integer> subEntry : entry
						.getValue().entrySet()) {
					if ((subEntry.getKey()).toString().equals("SUBMITTED")) {
						recSUBMITTED = subEntry.getValue();
						recSUBMITTEDTotal = recSUBMITTEDTotal + recSUBMITTED;
					} else if ((subEntry.getKey()).toString().equals("DMREJ")) {
						recBDMREJ = subEntry.getValue();
						recBDMREJTotal = recBDMREJTotal + recBDMREJ;
					} else if ((subEntry.getKey()).toString()
							.equals("ACCEPTED")) {
						recACCEPTED = subEntry.getValue();
						recACCEPTEDTotal = recACCEPTEDTotal + recACCEPTED;
					} else if ((subEntry.getKey()).toString().equals(
							"INTERVIEWING")) {
						recINTERVIEWING = subEntry.getValue();
						recINTERVIEWINGTotal = recINTERVIEWINGTotal
								+ recINTERVIEWING;
					} else if ((subEntry.getKey()).toString().equals(
							"CONFIRMED")) {
						recCONFIRMED = subEntry.getValue();
						recCONFIRMEDTotal = recCONFIRMEDTotal + recCONFIRMED;
					} else if ((subEntry.getKey()).toString().equals("STARTED")) {
						recSTARTED = subEntry.getValue();
						recSTARTEDTotal = recSTARTEDTotal + recSTARTED;
					} else if ((subEntry.getKey()).toString().equals("BACKOUT")) {
						recBACKOUT = subEntry.getValue();
						recBACKOUTTotal = recBACKOUTTotal + recBACKOUT;
					} else if ((subEntry.getKey()).toString()
							.equals("REJECTED")) {
						recREJECTED = subEntry.getValue();
						recREJECTEDTotal = recREJECTEDTotal + recREJECTED;
					} else if ((subEntry.getKey()).toString().equals(
							"OUTOFPROJ")) {
						recOUTOFPROJ = subEntry.getValue();
						recOUTOFPROJTotal = recOUTOFPROJTotal + recOUTOFPROJ;
					}

				}
				total = recSUBMITTED + recBDMREJ + recACCEPTED
						+ recINTERVIEWING + recCONFIRMED + recSTARTED
						+ recBACKOUT + recREJECTED + recOUTOFPROJ;
				count = recSUBMITTEDTotal + recBDMREJTotal + recACCEPTEDTotal
						+ recINTERVIEWINGTotal + recCONFIRMEDTotal
						+ recSTARTEDTotal + recBACKOUTTotal + recOUTOFPROJTotal
						+ recREJECTEDTotal;
				strRow = recSUBMITTED + "," + recBDMREJ + "," + recACCEPTED
						+ "," + recINTERVIEWING + "," + recCONFIRMED + ","
						+ recREJECTED + "," + recSTARTED + "," + recBACKOUT
						+ "," + recOUTOFPROJ + "," + total;
				recCount = total
						+ ","
						+ recBDMREJ
						+ ","
						+ recACCEPTED
						+ ","
						+ recINTERVIEWING
						+ ","
						+ recCONFIRMED
						+ ","
						+ recREJECTED
						+ ","
						+ recSTARTED
						+ ","
						+ recBACKOUT
						+ ","
						+ recOUTOFPROJ
						+ ","
						+ (total - (recBDMREJ + recACCEPTED + recINTERVIEWING
								+ recCONFIRMED + recSTARTED + recBACKOUT
								+ recREJECTED + recOUTOFPROJ)) + "";
				statusNuTotal += (total - (recBDMREJ + recACCEPTED
						+ recINTERVIEWING + recCONFIRMED + recSTARTED
						+ recBACKOUT + recREJECTED + recOUTOFPROJ));
				insertDetailInfo(sheet, rowIndex, entry.getKey(), bdmName,
						bdmLocation, recCount);
			}
			columnRow = recSUBMITTEDTotal + "," + recBDMREJTotal + ","
					+ recACCEPTEDTotal + "," + recINTERVIEWINGTotal + ","
					+ recCONFIRMEDTotal + "," + recREJECTEDTotal + ","
					+ recSTARTEDTotal + "," + recBACKOUTTotal + ","
					+ recOUTOFPROJTotal + "," + count;
			recColValue = count + "," + recBDMREJTotal + "," + recACCEPTEDTotal
					+ "," + recINTERVIEWINGTotal + "," + recCONFIRMEDTotal
					+ "," + recREJECTEDTotal + "," + recSTARTEDTotal + ","
					+ recBACKOUTTotal + "," + recOUTOFPROJTotal + ","
					+ statusNuTotal;
			insertDetailInfoTotal(sheet, rowIndex, recColValue);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return rowIndex;
	}

	private int insertSubmittalsInfoSapereReport(HSSFSheet sheet, int index,
			Map<String, Map<SubmittalStatus, Integer>> submittalStatusByUser,
			Map<String, String> assignedBdms, Map<String, String> bdmLocations,
			Map<String, Integer> approvedPercentage,
			Map<String, Integer> interviewingPercentage,
			Map<String, Integer> weeklyGoalPercentage) {

		int rowIndex = 0;
		Integer recBDMREJ = 0;
		Integer recOUTOFPROJ = 0;
		Integer recACCEPTED = 0;
		Integer recSUBMITTED = 0;
		Integer recINTERVIEWING = 0;
		Integer recCONFIRMED = 0;
		Integer recSTARTED = 0;
		Integer recBACKOUT = 0;
		Integer recCLOSED = 0;
		Integer recREJECTED = 0;
		Integer recACCEPTEDTotal = 0;
		Integer recSUBMITTEDTotal = 0;
		Integer recINTERVIEWINGTotal = 0;
		Integer recCONFIRMEDTotal = 0;
		Integer recSTARTEDTotal = 0;
		Integer recBACKOUTTotal = 0;
		Integer recCLOSEDTotal = 0;
		Integer recREJECTEDTotal = 0;
		Integer recBDMREJTotal = 0;
		Integer recOUTOFPROJTotal = 0;
		String strRow = null;
		String recCount = null;
		String recColValue = null;
		int total = 0;
		int count = 0;
		String columnRow = null;
		int statusNuTotal = 0;

		int approvedPer = 0;
		int inverviewPer = 0;
		int goalPer = 0;

		try {

			for (Map.Entry<String, Map<SubmittalStatus, Integer>> entry : submittalStatusByUser
					.entrySet()) {
				rowIndex = rowIndex + 1;
				String recName = entry.getKey();
				String bdmName = assignedBdms.get(recName);
				if (bdmName == null) {
					bdmName = "";
				}
				String bdmLocation = bdmLocations.get(recName);
				if (bdmLocation == null) {
					bdmLocation = "";
				}
				for (Map.Entry<SubmittalStatus, Integer> subEntry : entry
						.getValue().entrySet()) {
					if ((subEntry.getKey()).toString().equals("SUBMITTED")) {
						recSUBMITTED = subEntry.getValue();
						recSUBMITTEDTotal = recSUBMITTEDTotal + recSUBMITTED;
					} else if ((subEntry.getKey()).toString().equals("DMREJ")) {
						recBDMREJ = subEntry.getValue();
						recBDMREJTotal = recBDMREJTotal + recBDMREJ;
					} else if ((subEntry.getKey()).toString()
							.equals("ACCEPTED")) {
						recACCEPTED = subEntry.getValue();
						recACCEPTEDTotal = recACCEPTEDTotal + recACCEPTED;
					} else if ((subEntry.getKey()).toString().equals(
							"INTERVIEWING")) {
						recINTERVIEWING = subEntry.getValue();
						recINTERVIEWINGTotal = recINTERVIEWINGTotal
								+ recINTERVIEWING;
					} else if ((subEntry.getKey()).toString().equals(
							"CONFIRMED")) {
						recCONFIRMED = subEntry.getValue();
						recCONFIRMEDTotal = recCONFIRMEDTotal + recCONFIRMED;
					} else if ((subEntry.getKey()).toString().equals("STARTED")) {
						recSTARTED = subEntry.getValue();
						recSTARTEDTotal = recSTARTEDTotal + recSTARTED;
					} else if ((subEntry.getKey()).toString().equals("BACKOUT")) {
						recBACKOUT = subEntry.getValue();
						recBACKOUTTotal = recBACKOUTTotal + recBACKOUT;
					} else if ((subEntry.getKey()).toString()
							.equals("REJECTED")) {
						recREJECTED = subEntry.getValue();
						recREJECTEDTotal = recREJECTEDTotal + recREJECTED;
					} else if ((subEntry.getKey()).toString().equals(
							"OUTOFPROJ")) {
						recOUTOFPROJ = subEntry.getValue();
						recOUTOFPROJTotal = recOUTOFPROJTotal + recOUTOFPROJ;
					}

				}
				total = recSUBMITTED + recBDMREJ + recACCEPTED
						+ recINTERVIEWING + recCONFIRMED + recSTARTED
						+ recBACKOUT + recREJECTED + recOUTOFPROJ;
				// count = recSUBMITTEDTotal + recBDMREJTotal + recACCEPTEDTotal
				// + recINTERVIEWINGTotal + recCONFIRMEDTotal
				// + recSTARTEDTotal + recBACKOUTTotal + recOUTOFPROJTotal
				// + recREJECTEDTotal;
				strRow = recSUBMITTED + "," + recBDMREJ + "," + recACCEPTED
						+ "," + recINTERVIEWING + "," + recCONFIRMED + ","
						+ recREJECTED + "," + recSTARTED + "," + recBACKOUT
						+ "," + recOUTOFPROJ + "," + total;

				approvedPer = approvedPercentage.get(entry.getKey());
				inverviewPer = interviewingPercentage.get(entry.getKey());
				goalPer=weeklyGoalPercentage.get(entry.getKey());
				recCount = recSUBMITTED + "," + recBDMREJ + "," + recACCEPTED
						+ "," + recINTERVIEWING + ","
						+ recREJECTED + "," + recSTARTED + "," + approvedPer + ","
						+ inverviewPer + "," + goalPer + "";

				System.out.println("approvedPer"+approvedPer+"inverviewPer"+inverviewPer+"goalPer"+goalPer);
				statusNuTotal += (total - (recBDMREJ + recACCEPTED
						+ recINTERVIEWING + recCONFIRMED + recSTARTED
						+ recBACKOUT + recREJECTED + recOUTOFPROJ));
				insertDetailInfoActivityReport(sheet, rowIndex, entry.getKey(),
						bdmName, bdmLocation, recCount, total);
				count = count + total;
			}
			columnRow = recSUBMITTEDTotal + "," + recBDMREJTotal + ","
					+ recACCEPTEDTotal + "," + recINTERVIEWINGTotal + ","
					+ recCONFIRMEDTotal + "," + recREJECTEDTotal + ","
					+ recSTARTEDTotal + "," + recBACKOUTTotal + ","
					+ recOUTOFPROJTotal + "," + count;

			recColValue = recSUBMITTEDTotal + "," + recBDMREJTotal + ","
					+ recACCEPTEDTotal + "," + recINTERVIEWINGTotal + ","
					+  + recREJECTEDTotal + ","
					+ recSTARTEDTotal ;
			insertDetailInfoTotalActivityReport(sheet, rowIndex, recColValue);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return rowIndex;
	}

	private int insertSubmittalsInfoActivityReport(HSSFSheet sheet, int index,
			Map<String, Map<SubmittalStatus, Integer>> submittalStatusByUser,
			Map<String, String> assignedBdms, Map<String, String> bdmLocations) {

		int rowIndex = 0;
		Integer recBDMREJ = 0;
		Integer recOUTOFPROJ = 0;
		Integer recACCEPTED = 0;
		Integer recSUBMITTED = 0;
		Integer recINTERVIEWING = 0;
		Integer recCONFIRMED = 0;
		Integer recSTARTED = 0;
		Integer recBACKOUT = 0;
		Integer recCLOSED = 0;
		Integer recREJECTED = 0;
		Integer recACCEPTEDTotal = 0;
		Integer recSUBMITTEDTotal = 0;
		Integer recINTERVIEWINGTotal = 0;
		Integer recCONFIRMEDTotal = 0;
		Integer recSTARTEDTotal = 0;
		Integer recBACKOUTTotal = 0;
		Integer recCLOSEDTotal = 0;
		Integer recREJECTEDTotal = 0;
		Integer recBDMREJTotal = 0;
		Integer recOUTOFPROJTotal = 0;
		String strRow = null;
		String recCount = null;
		String recColValue = null;
		int total = 0;
		int count = 0;
		String columnRow = null;
		int statusNuTotal = 0;

		try {

			for (Map.Entry<String, Map<SubmittalStatus, Integer>> entry : submittalStatusByUser
					.entrySet()) {
				rowIndex = rowIndex + 1;
				String recName = entry.getKey();
				String bdmName = assignedBdms.get(recName);
				if (bdmName == null) {
					bdmName = "";
				}
				String bdmLocation = bdmLocations.get(recName);
				if (bdmLocation == null) {
					bdmLocation = "";
				}
				for (Map.Entry<SubmittalStatus, Integer> subEntry : entry
						.getValue().entrySet()) {
					if ((subEntry.getKey()).toString().equals("SUBMITTED")) {
						recSUBMITTED = subEntry.getValue();
						recSUBMITTEDTotal = recSUBMITTEDTotal + recSUBMITTED;
					} else if ((subEntry.getKey()).toString().equals("DMREJ")) {
						recBDMREJ = subEntry.getValue();
						recBDMREJTotal = recBDMREJTotal + recBDMREJ;
					} else if ((subEntry.getKey()).toString()
							.equals("ACCEPTED")) {
						recACCEPTED = subEntry.getValue();
						recACCEPTEDTotal = recACCEPTEDTotal + recACCEPTED;
					} else if ((subEntry.getKey()).toString().equals(
							"INTERVIEWING")) {
						recINTERVIEWING = subEntry.getValue();
						recINTERVIEWINGTotal = recINTERVIEWINGTotal
								+ recINTERVIEWING;
					} else if ((subEntry.getKey()).toString().equals(
							"CONFIRMED")) {
						recCONFIRMED = subEntry.getValue();
						recCONFIRMEDTotal = recCONFIRMEDTotal + recCONFIRMED;
					} else if ((subEntry.getKey()).toString().equals("STARTED")) {
						recSTARTED = subEntry.getValue();
						recSTARTEDTotal = recSTARTEDTotal + recSTARTED;
					} else if ((subEntry.getKey()).toString().equals("BACKOUT")) {
						recBACKOUT = subEntry.getValue();
						recBACKOUTTotal = recBACKOUTTotal + recBACKOUT;
					} else if ((subEntry.getKey()).toString()
							.equals("REJECTED")) {
						recREJECTED = subEntry.getValue();
						recREJECTEDTotal = recREJECTEDTotal + recREJECTED;
					} else if ((subEntry.getKey()).toString().equals(
							"OUTOFPROJ")) {
						recOUTOFPROJ = subEntry.getValue();
						recOUTOFPROJTotal = recOUTOFPROJTotal + recOUTOFPROJ;
					}

				}
				total = recSUBMITTED + recBDMREJ + recACCEPTED
						+ recINTERVIEWING + recCONFIRMED + recSTARTED
						+ recBACKOUT + recREJECTED + recOUTOFPROJ;
				count = recSUBMITTEDTotal + recBDMREJTotal + recACCEPTEDTotal
						+ recINTERVIEWINGTotal + recCONFIRMEDTotal
						+ recSTARTEDTotal + recBACKOUTTotal + recOUTOFPROJTotal
						+ recREJECTEDTotal;
				strRow = recSUBMITTED + "," + recBDMREJ + "," + recACCEPTED
						+ "," + recINTERVIEWING + "," + recCONFIRMED + ","
						+ recREJECTED + "," + recSTARTED + "," + recBACKOUT
						+ "," + recOUTOFPROJ + "," + total;
				recCount = total
						+ ","
						+ recBDMREJ
						+ ","
						+ recACCEPTED
						+ ","
						+ recINTERVIEWING
						+ ","
						+ recCONFIRMED
						+ ","
						+ recREJECTED
						+ ","
						+ recSTARTED
						+ ","
						+ recBACKOUT
						+ ","
						+ recOUTOFPROJ
						+ ","
						+ (total - (recBDMREJ + recACCEPTED + recINTERVIEWING
								+ recCONFIRMED + recSTARTED + recBACKOUT
								+ recREJECTED + recOUTOFPROJ)) + "";
				statusNuTotal += (total - (recBDMREJ + recACCEPTED
						+ recINTERVIEWING + recCONFIRMED + recSTARTED
						+ recBACKOUT + recREJECTED + recOUTOFPROJ));
				insertDetailInfo(sheet, rowIndex, entry.getKey(), bdmName,
						bdmLocation, recCount);
			}
			columnRow = recSUBMITTEDTotal + "," + recBDMREJTotal + ","
					+ recACCEPTEDTotal + "," + recINTERVIEWINGTotal + ","
					+ recCONFIRMEDTotal + "," + recREJECTEDTotal + ","
					+ recSTARTEDTotal + "," + recBACKOUTTotal + ","
					+ recOUTOFPROJTotal + "," + count;
			recColValue = count + "," + recBDMREJTotal + "," + recACCEPTEDTotal
					+ "," + recINTERVIEWINGTotal + "," + recCONFIRMEDTotal
					+ "," + recREJECTEDTotal + "," + recSTARTEDTotal + ","
					+ recBACKOUTTotal + "," + recOUTOFPROJTotal + ","
					+ statusNuTotal;
			insertDetailInfoTotal(sheet, rowIndex, recColValue);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return rowIndex;
	}

	/**
	 * @param submittalStatusByUser
	 * @param assignedBdms
	 * @param bdmLocations
	 * @return
	 * 
	 *         for ADM Activity Report Export
	 */
	public byte[] createAdmActivityExcel(
			Map<String, Map<SubmittalStatus, Integer>> submittalStatusByUser,
			Map<String, String> assignedBdms, Map<String, String> bdmLocations) {

		byte[] output = null;
		try {

			HSSFWorkbook wb = new HSSFWorkbook();
			HSSFSheet sheet = wb.createSheet("ADM Activity Report");

			// Setup some styles that we need for the Cells
			setCellStyles(wb);
			setColWidthAndMargin(sheet);

			int rowIndex = 0;
			rowIndex = insertHeaderInfo(sheet, rowIndex, "ADM");
			System.out.println("rowIndex:::" + rowIndex);
			rowIndex = insertSubmittalsInfo(sheet, rowIndex,
					submittalStatusByUser, assignedBdms, bdmLocations);
			String timeStamp = new SimpleDateFormat("MMddyyyy_HHmmss")
					.format(Calendar.getInstance().getTime());
			String fileName = "attachment;filename=ADM_Activity_Report_"
					+ timeStamp + ".xls";
			output = wb.getBytes();
			System.out.println("Workbook length in bytes: " + output.length);
			FacesContext context = FacesContext.getCurrentInstance();
			HttpServletResponse res = (HttpServletResponse) context
					.getExternalContext().getResponse();
			res.setContentType("application/vnd.ms-excel");
			res.setHeader("Content-disposition", fileName);
			ServletOutputStream out = res.getOutputStream();
			wb.write(out);
			out.flush();
			out.close();
			FacesContext.getCurrentInstance().responseComplete();

		} catch (Exception e) {
			System.out
					.println("Exception in exporting all records to excel :: "
							+ e);
			e.printStackTrace();
		}
		return output;
	}

}
