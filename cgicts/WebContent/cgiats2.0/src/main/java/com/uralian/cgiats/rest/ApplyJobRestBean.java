package com.uralian.cgiats.rest;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import com.uralian.cgiats.model.Address;
import com.uralian.cgiats.model.Candidate;
import com.uralian.cgiats.model.ContentType;
import com.uralian.cgiats.model.JobOrder;
import com.uralian.cgiats.model.JobOrderDefaults;
import com.uralian.cgiats.model.MobileCgiCandidates;
import com.uralian.cgiats.model.Submittal;
import com.uralian.cgiats.service.CandidateService;
import com.uralian.cgiats.service.CareerAccountService;
import com.uralian.cgiats.service.JobOrderService;
import com.uralian.cgiats.service.MobileCgiCandidateService;
import com.uralian.cgiats.service.SubmittalService;
import com.uralian.cgiats.util.Utils;

@Component
@Path("/applyJob")
public class ApplyJobRestBean extends SpringBeanAutowiringSupport {

	private static final Logger log=Logger.getLogger(ApplyJobRestBean.class);
	@Autowired
	CandidateService candidateService;
	@Autowired
	private transient SubmittalService submittalService;
	@Autowired
	public CareerAccountService careerAccount;
	@Autowired
	private transient MobileCgiCandidateService mobileCgiCandidateService;
	@Autowired
	private transient JobOrderService service;
	private Submittal submittal;
	private MobileCgiCandidates mobileCgiCandidates;
	Candidate candidate = null;
	private Integer orderId;
	private Integer candidateId;
	private JobOrder order;
	String summary = "Rejected";
	JSONObject jsonObj = new JSONObject();

	@POST
	@Consumes("application/zip")
	@Produces(MediaType.APPLICATION_JSON)
	public String updateUserProfile(File file,
			@Context HttpServletRequest request) throws JSONException {
		log.info(" From ApplyJobRestBean updateUserProfile Start......");
		Candidate result = null;
		System.out.println("request>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>"
				+ request.getHeader("profileData"));
		System.out.println("request>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>"
				+ request.getHeader("firstName"));

		String firstName = request.getHeader("firstName");
		String lastName = request.getHeader("lastName");
		String email = request.getHeader("email");
		String phone = request.getHeader("phone");
		String visaType = request.getHeader("visa_type");
		String expectedRate = request.getHeader("expected_rate");
		String currentRate = request.getHeader("current_rate");
		String currentLocation = request.getHeader("currentLocation");
		String fileType = request.getHeader("fileType");
		String jobId = request.getHeader("job_id");
		String createdUser = request.getHeader("created_by");
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
				// order = service.getJobOrder(orderId, true, true);
				candidateId = candidate.getId();

				mobileCgiCandidates = new MobileCgiCandidates();
				mobileCgiCandidates.setCandidateId(candidate);
				JobOrder jobOrder=new JobOrder();
				jobOrder.setId(Integer.valueOf(jobId));
				mobileCgiCandidates.setOrderId(jobOrder);
				System.out.println("JobId>>>>" + Integer.valueOf(jobId));
				mobileCgiCandidates.setCreatedBy(createdUser);
				mobileCgiCandidates.setResumeStatus("PENDING");
				mobileCgiCandidates.setCreatedOn(new Date());
				mobileCgiCandidateService
						.saveMobileCandidate(mobileCgiCandidates);
				/*
				 * submittal = new Submittal(); submittal.setCreatedDate(new
				 * Date()); submittal.setStatus(SubmittalStatus.SUBMITTED);
				 * submittal.setDeleteFlag(0);
				 * submittal.setCreatedBy(createdUser);
				 * submittal.setJobOrder(order);
				 * submittal.setCandidate(candidate);
				 * service.saveSubmittal(submittal);
				 * //log.info("order2:::::: "+order);
				 * service.updateJobOrder(order);
				 * //log.debug("Submittal saved: " + submittal);
				 */

			} catch (Exception e) {
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
		log.info(" From ApplyJobRestBean updateUserProfile End......");
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

		try{
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
		}catch(Exception e){
			e.printStackTrace();
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

	@GET
	@Path("/checkAppliedJob")
	@Produces("application/zip")
	public Response chkJobApply(@QueryParam("job_id") Integer jobId,
			@QueryParam("email") String email,
			@Context HttpServletResponse response) throws JSONException,
			IOException {
	log.info(" From ApplyJobRestBean chkJobApply Start......");
	
		candidate = candidateService.getCandidateFromEmail(email, true, false);
		candidateId = candidate.getId();
		ResponseBuilder responseBuilder = null;
		Response responseType = null;
		String OUTPUT_ZIP_FILE = null;
		String DB_FILE = "D:\\";
		List<MobileCgiCandidates> canLst = mobileCgiCandidateService
				.getCandidate(jobId, candidateId);
		
		try {

			if (canLst.size() > 0) {
				response.setHeader("statusMessage",
						"Candidate already applied to this job");
				response.setHeader("statusCode", "JobApplied");
				responseBuilder = Response.ok("OK");
				responseType = responseBuilder.build();
			} else {
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

					OUTPUT_ZIP_FILE = ZIP_FOLDER + candidate.getFirstName()
							+ "-" + candidate.getLastName() + "-Output.zip";

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
					FileOutputStream fileOuputStream = new FileOutputStream(
							DB_FILE);
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
					response.setHeader("expected_rate",
							candidate.getExpectedRate());
				} else {
					response.setHeader("expected_rate", " ");
				}
				if (candidate.getPresentRate() != null) {
					response.setHeader("current_rate",
							candidate.getPresentRate());
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
					// response.setHeader("jsonString", jsonObj.toString());
					// deleteFolder(DB_FILE);
					// deleteFolder(ZIP_FOLDER);
					File folder2 = new File(DB_FILE);
					/*delete(file);
					System.out.println("Deleted Folder is--------->"+folder2.getAbsolutePath());
//					delete(folder2);
					boolean status=folder2.delete();
					System.out.println("Deleted Status----->"+status);*/
					// delete(file.);
					// return res;
					delete(folder2);
					responseType = responseBuilder.build();
					// boolean flag = file.delete();
					// System.out.println("delete Flag="+flag);
				} else {
					responseBuilder = Response.ok("No Resume Found");
					responseType = responseBuilder.build();
				}
			}

		} catch (Exception e) {
			e.printStackTrace();

		}/*finally{
			if(file!=null)
			delete(file);
			if(folder2!=null){
			boolean status=folder2.delete();
			System.out.println("Deleted Status----->"+status);
			}
		}*/
		log.info(" From ApplyJobRestBean chkJobApply End......");
		return responseType;
	}

	public void deleteFolder1(String OUTPUT_FOLDER) {

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

	public void delete1(File file) throws IOException {

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
