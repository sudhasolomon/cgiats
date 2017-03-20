package com.uralian.cgiats.rest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;

@Path("/restLogout")
public class LogoutRestBean {
        private static final Logger log=Logger.getLogger(LogoutRestBean.class);
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String logoutUser(@Context HttpServletRequest request) {
		HttpSession session = request.getSession();
		log.info("From LogoutRestBean Start...");
		if (session.getAttribute("userId") == null) {
			return "session expired";
		} else {
			session.removeAttribute("userId");
		}
		log.info("From LogoutRestBean End...");
		return "success";
	}

}
