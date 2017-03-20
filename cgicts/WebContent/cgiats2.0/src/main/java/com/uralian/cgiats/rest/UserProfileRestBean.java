package com.uralian.cgiats.rest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import com.uralian.cgiats.model.Candidate;
import com.uralian.cgiats.service.CandidateService;

@Component
@Path("/restProfile")
public class UserProfileRestBean extends SpringBeanAutowiringSupport {

	@Autowired
	CandidateService candidateService;
	Candidate candidate = null;

	@GET
	@Produces("application/zip")
	public Response userProfile(@QueryParam("email") String email,
			@Context HttpServletRequest request,
			@Context HttpServletResponse response) throws JSONException,
			IOException {
		JSONObject jsonObj = new JSONObject();
		String DB_FILE = "D:\\";
		String OUTPUT_ZIP_FILE = null;
		ResponseBuilder responseBuilder = null;
		Response responseType = null;
		candidate = candidateService.getCandidateFromEmail(email, true, false);

		response.setHeader("first_name", candidate.getFirstName());
		response.setHeader("last_name", candidate.getLastName());
		response.setHeader("email", candidate.getEmail());
		response.setHeader("location", candidate.getCityState());
		response.setHeader("mobile", candidate.getPhone());
		response.setHeader("visa_type", candidate.getVisaType());
		response.setHeader("visa_transfer", candidate.getVisaTransfer());
		response.setHeader("resumeTitle", candidate.getTitle());
	
		
		if (candidate.getDocumentType() != null) {
			byte[] resume = candidate.getDocument();

			String ext = null;

			String ZIP_FOLDER = "D:\\";
			File folder = new File(DB_FILE);
			if (!folder.exists()) {
				folder.mkdir();
			}
			File folder1 = new File(ZIP_FOLDER);
			if (!folder1.exists()) {
				folder1.mkdir();
			}

			OUTPUT_ZIP_FILE = ZIP_FOLDER + candidate.getFirstName() + "-"
					+ candidate.getLastName() + "-Output.zip";

			if (candidate.getDocumentType() != null
					&& candidate.getDocumentType().toString()
							.equalsIgnoreCase("MS_WORD")) {
				ext = "DOC";
			} else {
				ext = candidate.getDocumentType().toString();
			}
			response.setHeader("fileType", ext);
			String fileName = candidate.getFirstName() + "-"
					+ candidate.getLastName() + "." + ext;
			DB_FILE = DB_FILE + fileName;
			response.setHeader("fileName", fileName);
			// convert array of bytes into file
			FileOutputStream fileOuputStream = new FileOutputStream(DB_FILE);
			fileOuputStream.write(resume);

			byte[] buffer = new byte[1024];

			FileOutputStream fos = new FileOutputStream(OUTPUT_ZIP_FILE);
			ZipOutputStream zos = new ZipOutputStream(fos);
			ZipEntry ze = new ZipEntry(DB_FILE);
			zos.putNextEntry(ze);
			FileInputStream in = new FileInputStream(DB_FILE);

			int len;

			while ((len = in.read(buffer)) > 0) {
				zos.write(buffer, 0, len);
			}
			fileOuputStream.close();
			in.close();
			zos.closeEntry();

			// remember close it
			zos.close();
			fos.close();
			System.out.println("Done");
		}

		if (candidate.getExpectedRate() != null) {
			response.setHeader("expected_rate", candidate.getExpectedRate());
		} else {
			response.setHeader("expected_rate", " ");
		}
		if (candidate.getPresentRate() != null) {
			response.setHeader("current_rate", candidate.getPresentRate());
		} else {
			response.setHeader("current_rate", " ");
		}

		// }
		response.setHeader("statusMessage", "User Profile");
		response.setHeader("statusCode", "Success");
		if (candidate.getDocumentType() != null) {
			File file = new File(OUTPUT_ZIP_FILE);
			responseBuilder = Response.ok(file);
			// Response res = response1.build();
			// response.setContentType("application/zip");
			// deleteFolder(DB_FILE);
			// deleteFolder(ZIP_FOLDER);
			File folder2 = new File(DB_FILE);
			delete(folder2);
			responseType = responseBuilder.build();
			// boolean flag = file.delete();
			// System.out.println("delete Flag="+flag);
		} else {
			responseBuilder = Response.ok("No Resume Found");
			responseType = responseBuilder.build();
		}
		return responseType;
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
}
