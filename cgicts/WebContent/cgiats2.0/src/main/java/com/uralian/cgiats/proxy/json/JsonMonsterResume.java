package com.uralian.cgiats.proxy.json;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.json.simple.JSONArray;
import org.json.simple.JSONAware;
import org.json.simple.JSONObject;
import org.testng.log4testng.Logger;

/**
 * @author Jorge
 */
public class JsonMonsterResume implements JSONAware {
	// private static Pattern lineBreakPattern =
	// Pattern.compile("<[b,r,B,R \\t\\r\\n\\v\\f]*/>");
	private static Pattern htmlPattern = Pattern.compile("<[A-Za-z0-9 \\t\\r\\n\\v\\f\\]\\[!\"#$%&'()*+,.:;=?@\\^_`{|}/~-]*>");

	private static final Logger LOG = Logger.getLogger(JsonMonsterResume.class);

	private static final String YES = "Yes";

	private String resumeBody;
	private List<MonsterSkill> skills = new ArrayList<MonsterSkill>();
	private List<MonsterLanguage> languages = new ArrayList<MonsterLanguage>();
	private String resumeTitle;
	private String resumeKey;
	private String dateModified;
	private String name;
	private String company;
	private String mostRecentJobTitle;
	private String address;
	private String homePhone;
	private String mobilePhone;
	private String email;
	private String gender;
	private String experience;
	private String visaType;
	private String city;
	private String state;
	private String zipcode;
	private Boolean securityClearance;

	@Override
	public String toJSONString() {
		return null;
	}

	public static JsonMonsterResume fromJSONString(JSONObject parsedObject) {

		LOG.info("fromJson" + parsedObject);
		JsonMonsterResume resume = null;
		try {

			JSONObject generalObject = (JSONObject) parsedObject.get("d");
			if (generalObject != null) {
				JSONObject resumeObject = (JSONObject) generalObject.get("Resume");
				if (resumeObject != null) {
					resume = new JsonMonsterResume();
					if (generalObject != null) {
						JSONObject skillsObject = (JSONObject) generalObject.get("Skills");
						if (skillsObject != null) {
							JSONArray skillsArray = (JSONArray) skillsObject.get("Skills");
							if (skillsArray != null) {
								for (Object o : skillsArray) {
									JSONObject skillObject = (JSONObject) o;
									// remove HTML on all fields except the
									// resume
									// body,
									// monster adds
									// html tags to highlight the words that
									// march
									// your
									// search criteria
									// not all have null pointer checks because
									// some
									// of
									// them
									// cant be
									// null.
									MonsterSkill skill = new MonsterSkill();
									skill.setName(htmlPattern.matcher((String) skillObject.get("SkillName")).replaceAll(""));
									skill.setProficiency(htmlPattern.matcher((String) skillObject.get("Proficiency")).replaceAll(""));
									resume.skills.add(skill);
								}
							}
							JSONArray languagesArray = (JSONArray) skillsObject.get("Languages");
							if (languagesArray != null) {
								for (Object o : languagesArray) {
									JSONObject languageObject = (JSONObject) o;
									MonsterLanguage language = new MonsterLanguage();
									language.setName(htmlPattern.matcher((String) languageObject.get("LanguageName")).replaceAll(""));
									language.setProficiency(htmlPattern.matcher((String) languageObject.get("Proficiency")).replaceAll(""));
									resume.languages.add(language);
								}
							}
						}

						if (resumeObject != null) {
							resume.resumeBody = (String) resumeObject.get("ResumeBody");
							resume.resumeTitle = (String) resumeObject.get("ResumeTitle");
							resume.resumeTitle = resume.resumeTitle == null ? "" : htmlPattern.matcher(resume.resumeTitle).replaceAll("");
							resume.resumeKey = (String) resumeObject.get("ResumeValue");
							resume.dateModified = (String) resumeObject.get("DateModified");
							resume.dateModified = resume.dateModified == null ? "" : htmlPattern.matcher(resume.dateModified).replaceAll("");
						}
						JSONObject headerObject = (JSONObject) generalObject.get("Heading");
						if (headerObject != null) {
							resume.name = (String) headerObject.get("Name");
							resume.name = resume.name == null ? "" : htmlPattern.matcher(resume.name).replaceAll("");
							resume.company = (String) headerObject.get("Company");
							resume.company = resume.company == null ? "" : htmlPattern.matcher(resume.company).replaceAll("");
							resume.resumeTitle = (String) headerObject.get("MostRecentJobTitle");
							LOG.info("(String) headerObject.get(MostRecentJobTitle)" + (String) headerObject.get("MostRecentJobTitle"));
							resume.mostRecentJobTitle = resume.mostRecentJobTitle == null ? "" : htmlPattern.matcher(resume.mostRecentJobTitle).replaceAll("");
							resume.address = (String) headerObject.get("Address");
							resume.address = resume.address == null ? "" : htmlPattern.matcher(resume.address).replaceAll("");
							if (resume.address != null && resume.address.trim().length() > 0) {
								String[] locations = resume.address.split(",");
								if (locations != null && locations.length > 0) {
									resume.setCity(locations[0]);
									if (locations[1].trim().split(" ") != null && locations[1].trim().split(" ").length > 0) {
										Pattern p1 = Pattern.compile("[0-9]*");
										Pattern p2 = Pattern.compile("[A-Z]*");
										String zip1 = locations[1].trim().split(" ")[1];
										String zip2 = locations[1].trim().split(" ")[0];
										if (p1.matcher(zip1).matches() || zip1.matches("\\d{3,10}[-]\\d{3,10}")) {
											resume.setZipcode(zip1);
										}
										if (p1.matcher(zip2).matches() || zip2.matches("\\d{3,10}[-]\\d{3,10}")) {
											resume.setZipcode(zip2);
										}
										if (p2.matcher(zip2).matches()) {
											resume.setState(zip2);
										}
									}
								}
							}

							resume.homePhone = (String) headerObject.get("Home");
							resume.homePhone = resume.homePhone == null ? "" : htmlPattern.matcher(resume.homePhone).replaceAll("");
							resume.mobilePhone = (String) headerObject.get("Mobile");
							resume.mobilePhone = resume.mobilePhone == null ? "" : htmlPattern.matcher(resume.mobilePhone).replaceAll("");
							resume.email = (String) headerObject.get("Email");
							resume.email = resume.email == null ? "" : htmlPattern.matcher(resume.email).replaceAll("");
							resume.gender = (String) headerObject.get("Gender");
							resume.gender = resume.gender == null ? "" : htmlPattern.matcher(resume.gender).replaceAll("");
						}
						JSONObject careerInfo = (JSONObject) generalObject.get("CareerInfo");

						if (careerInfo != null) {
							String exp = (String) careerInfo.get("RelevantWorkExperience");
							if (exp != null)
								resume.setExperience(exp.toString());
							String securityClearance = (String) careerInfo.get("SecurityClearance");
							if (securityClearance != null && securityClearance.trim().length() > 0) {
								if (securityClearance.trim().equalsIgnoreCase(YES)) {
									resume.setSecurityClearance(true);
								} else {
									resume.setSecurityClearance(false);
								}

							}
							/*
							 * JSONArray visaTypeArray=(JSONArray)
							 * careerInfo.get("WorkAuthorization");
							 * 
							 * 
							 * StringBuffer visaType=new StringBuffer();
							 * for(Object visa:visaTypeArray){
							 * visaType.append(visa.toString()); }
							 * 
							 * if(visaType!=null)
							 * resume.setVisaType(visaType.toString());
							 */
						}

					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			LOG.error(e.getMessage(), e);
		}
		return resume;
	}

	public String getResumeBody() {
		return resumeBody;
	}

	public void setResumeBody(String resumeBody) {
		this.resumeBody = resumeBody;
	}

	public static Pattern getHtmlPattern() {
		return htmlPattern;
	}

	public static void setHtmlPattern(Pattern htmlPattern) {
		JsonMonsterResume.htmlPattern = htmlPattern;
	}

	public List<MonsterSkill> getSkills() {
		return skills;
	}

	public void setSkills(List<MonsterSkill> skills) {
		this.skills = skills;
	}

	public List<MonsterLanguage> getLanguages() {
		return languages;
	}

	public void setLanguages(List<MonsterLanguage> languages) {
		this.languages = languages;
	}

	public String getResumeTitle() {
		return resumeTitle;
	}

	public void setResumeTitle(String resumeTitle) {
		this.resumeTitle = resumeTitle;
	}

	public String getResumeKey() {
		return resumeKey;
	}

	public void setResumeKey(String resumeKey) {
		this.resumeKey = resumeKey;
	}

	public String getDateModified() {
		return dateModified;
	}

	public void setDateModified(String dateModified) {
		this.dateModified = dateModified;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public String getMostRecentJobTitle() {
		return mostRecentJobTitle;
	}

	public void setMostRecentJobTitle(String mostRecentJobTitle) {
		this.mostRecentJobTitle = mostRecentJobTitle;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getHomePhone() {
		return homePhone;
	}

	public void setHomePhone(String homePhone) {
		this.homePhone = homePhone;
	}

	public String getMobilePhone() {
		return mobilePhone;
	}

	public void setMobilePhone(String mobilePhone) {
		this.mobilePhone = mobilePhone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getExperience() {
		return experience;
	}

	public void setExperience(String experience) {
		this.experience = experience;
	}

	public String getVisaType() {
		return visaType;
	}

	public void setVisaType(String visaType) {
		this.visaType = visaType;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getZipcode() {
		return zipcode;
	}

	public void setZipcode(String zipcode) {
		this.zipcode = zipcode;
	}

	/**
	 * @return the securityClearance
	 */
	public Boolean getSecurityClearance() {
		return securityClearance;
	}

	/**
	 * @param securityClearance
	 *            the securityClearance to set
	 */
	public void setSecurityClearance(Boolean securityClearance) {
		this.securityClearance = securityClearance;
	}
}
