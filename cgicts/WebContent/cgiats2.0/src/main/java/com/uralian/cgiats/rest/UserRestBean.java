package com.uralian.cgiats.rest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;
import com.uralian.cgiats.model.User;
import com.uralian.cgiats.service.UserService;

@Component
@Path("/userProfile")
public class UserRestBean extends SpringBeanAutowiringSupport {

	@Autowired
	public UserService userService;
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String getUserProfile(@Context HttpServletRequest request)
	{
		User user=null;
		HttpSession session=request.getSession();
		
		String userId=(String)session.getAttribute("restUser");
		
		/*if(userId==null)
		{
			return "login agian! session Expired";
		}*/
		
        String registerId=(String)session.getAttribute("registerId");
		
		if(registerId==null)
		{
			return "login agian! session Expired";
		}
		
		try{
		user=userService.loadUser(userId);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		JSONObject jsonObj=new JSONObject();
		
		try {
			if (user != null) {
				jsonObj.put("userId", user.getId());
				jsonObj.put("name", user.getFullName());
				jsonObj.put("userType", user.getUserRole());
				jsonObj.put("email", user.getEmail());
				jsonObj.put("officeLocation", user.getOfficeLocation());
				jsonObj.put("city", user.getCity());
				jsonObj.put("assignedTo", user.getAssignedBdm());
				jsonObj.put("phone", user.getPhone());
			}
			
		} catch (JSONException je) {
			// TODO Auto-generated catch block
			je.printStackTrace();
			return "Internal problem! Try after some time";
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return "Internal problem! Try after some time";
		}
		return jsonObj.toString();
	}

	public UserService getUserService() {
		return userService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}
}
