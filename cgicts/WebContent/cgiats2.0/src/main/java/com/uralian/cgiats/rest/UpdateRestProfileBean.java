package com.uralian.cgiats.rest;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import com.uralian.cgiats.model.Address;
import com.uralian.cgiats.model.Candidate;
import com.uralian.cgiats.model.ContentType;
import com.uralian.cgiats.service.CandidateService;
import com.uralian.cgiats.service.ServiceException;
import com.uralian.cgiats.util.Utils;

@Component
@Path("/updateProfile")
public class UpdateRestProfileBean extends SpringBeanAutowiringSupport {

	@Autowired
	CandidateService candidateService;
	Candidate candidate = null;

	@POST
	@Consumes("application/zip")
	@Produces(MediaType.APPLICATION_JSON)
	public String updateUserProfile(File file,
			@Context HttpServletRequest request) throws JSONException {

		System.out.println("request>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>"
				+ request.getHeader("profileData"));
		System.out.println("request>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>"
				+ request.getHeader("firstName"));

		JSONObject jsonObj = new JSONObject();
		String firstName = request.getHeader("firstName");
		String lastName = request.getHeader("lastName");
		String email = request.getHeader("email");
		String phone = request.getHeader("phone");
		String visaType = request.getHeader("visa_type");
		String expectedRate = request.getHeader("expected_rate");
		String currentRate = request.getHeader("current_rate");
		String currentLocation = request.getHeader("currentLocation");
		String fileType = request.getHeader("fileType");
		String visaTransfer = request.getHeader("visaTransfer");
		String resumeTitle = request.getHeader("resumeTitle");
		String isResumeUpload = request.getHeader("isResumeUpload");
		candidate = candidateService.getCandidateFromEmail(email, true, false);
		
		if (!Utils.isBlank(firstName)) {
			candidate.setFirstName(firstName);
		}
		if (!Utils.isBlank(lastName)) {
			candidate.setLastName(lastName);
		}
		if (!Utils.isBlank(phone)) {
			candidate.setPhone(phone);
		}
		if (!Utils.isBlank(currentLocation)) {
			Address a = new Address();
			a.setCity(currentLocation);
			candidate.setAddress(a);
		}
		candidate.setExpectedRate(expectedRate);
		candidate.setPresentRate(currentRate);
		candidate.setVisaType(visaType);
		candidate.setVisaTransfer(visaTransfer);
		candidate.setTitle(resumeTitle);

		String INPUT_ZIP_FILE = "D:\\MyFile.zip";
		String OUTPUT_FOLDER = "D:\\outputzip";
		byte[] buffer = new byte[1024];
		try {

			if ("yes".equalsIgnoreCase(isResumeUpload)) {
				String fileName = request.getHeader("fileName");
				FileInputStream fis = new FileInputStream(file);
				INPUT_ZIP_FILE = "D:\\" + firstName + "-" + lastName + "-Input";
				File folder = new File(INPUT_ZIP_FILE);
				if (!folder.exists()) {
					folder.mkdir();
				}
				INPUT_ZIP_FILE = INPUT_ZIP_FILE + "\\" + firstName + "-"
						+ lastName + ".zip";
				OUTPUT_FOLDER = "D:\\" + firstName + "-" + lastName + "-Output";
				FileOutputStream fos = new FileOutputStream(new File(
						INPUT_ZIP_FILE));
				int len;
				while ((len = fis.read(buffer)) > 0) {
					fos.write(buffer, 0, len);
				}
				fis.close();
				fos.close();
				unZipIt(INPUT_ZIP_FILE, OUTPUT_FOLDER);
				// File file1 = new File("D:\\sudheer- Iphone.doc");

				System.out.println("fileName  " + fileName);
				File file1 = new File(OUTPUT_FOLDER);
				FileInputStream fis1 = new FileInputStream(listFilesForFolder(
						file1, fileName));
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				byte[] buf = new byte[1024];
				try {
					for (int readNum; (readNum = fis1.read(buf)) != -1;) {
						bos.write(buf, 0, readNum); // no doubt here is 0
						// Writes len bytes from the specified byte array
						// starting at offset off to this byte array output
						// stream.
					}
				} catch (IOException ex) {
					ex.printStackTrace();
					jsonObj.put("statusMessage", "Error in uploding resume");
					jsonObj.put("statusCode", 21);
				}

				// System.out.println(bos.toByteArray());
				byte[] docData = bos.toByteArray();

				ContentType docType = ContentType.resolveByFileName(fileName);
				candidate.setDocument(docData, docType);
				candidate.parseDocument();
			}

			try {
				candidateService.upDateCandidateFromMobile(candidate);
			} catch (ServiceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				jsonObj.put("statusMessage",
						"Internal Error try after sometime");
				jsonObj.put("statusCode", 21);
			}
			if ("yes".equalsIgnoreCase(isResumeUpload)) {
				deleteFolder(INPUT_ZIP_FILE);
				deleteFolder(OUTPUT_FOLDER);
			}
			jsonObj.put("statusMessage",
					"Candidate profile updated successfully");
			jsonObj.put("statusCode", 1);

		} catch (Exception e) {
			e.printStackTrace();
			jsonObj.put("statusMessage", "Error while uploading file");
			jsonObj.put("statusCode", 21);
		}

		return jsonObj.toString();

	}

	public void unZipIt(String zipFile, String outputFolder) {

		byte[] buffer = new byte[1024];

		try {

			// create output directory is not exists
			File folder = new File(outputFolder);
			if (!folder.exists()) {
				folder.mkdir();
			}

			// get the zip file content
			ZipInputStream zis = new ZipInputStream(
					new FileInputStream(zipFile));
			// get the zipped file list entry
			ZipEntry ze = zis.getNextEntry();

			while (ze != null) {

				String fileName = ze.getName();
				File newFile = new File(outputFolder + File.separator
						+ fileName);

				System.out.println("file unzip : " + newFile.getAbsoluteFile());

				// create all non exists folders
				// else you will hit FileNotFoundException for compressed folder
				new File(newFile.getParent()).mkdirs();

				FileOutputStream fos = new FileOutputStream(newFile);

				int len;
				while ((len = zis.read(buffer)) > 0) {
					fos.write(buffer, 0, len);
				}

				fos.close();
				ze = zis.getNextEntry();
			}

			zis.closeEntry();
			zis.close();

			System.out.println("Done");

		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public void deleteFolder(String OUTPUT_FOLDER) {

		File directory = new File(OUTPUT_FOLDER);

		// make sure directory exists
		if (!directory.exists()) {

			System.out.println("Directory does not exist.");
			System.exit(0);

		} else {

			try {

				delete(directory);

			} catch (IOException e) {
				e.printStackTrace();
				System.exit(0);
			}
		}

		System.out.println("Done");
	}

	public void delete(File file) throws IOException {

		if (file.isDirectory()) {

			// directory is empty, then delete it
			if (file.list().length == 0) {

				file.delete();
				System.out.println("Directory is deleted : "
						+ file.getAbsolutePath());

			} else {

				// list all the directory contents
				String files[] = file.list();

				for (String temp : files) {
					// construct the file structure
					File fileDelete = new File(file, temp);

					// recursive delete
					delete(fileDelete);
				}

				// check the directory again, if empty then delete it
				if (file.list().length == 0) {
					file.delete();
					System.out.println("Directory is deleted : "
							+ file.getAbsolutePath());
				}
			}

		} else {
			// if file, then delete it
			file.delete();
			System.out.println("File is deleted : " + file.getAbsolutePath());
		}
	}

	public File listFilesForFolder(File folder, String fileName) {
		File fileEntry1 = null;
		for (final File fileEntry : folder.listFiles()) {
			if (fileEntry.isDirectory()) {
				listFilesForFolder(fileEntry, fileName);

			} else {
				if (fileEntry.getName() != null
						&& fileEntry.getName().toString()
								.equalsIgnoreCase(fileName)) {
					fileEntry1 = fileEntry;
					break;
				}
				System.out.println("file Name :" + fileEntry.getName());
			}
		}
		return fileEntry1;
	}
}