/**
 * 
 */
package com.uralian.cgiats.config;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.uralian.cgiats.dto.UserDto;
import com.uralian.cgiats.model.LoginAttempts;
import com.uralian.cgiats.service.LoginDetailsService;
import com.uralian.cgiats.util.Constants;
import com.uralian.cgiats.util.TransformDtoToEntity;

/**
 * @author skurapati
 *
 */
public class HttpSessionCollector implements HttpSessionListener {
	private static final Map<String, HttpSession> sessions = new HashMap<String, HttpSession>();
	/*@Autowired
	private LoginDetailsService loginDetailService;
*/
	@Override
	public void sessionCreated(HttpSessionEvent event) {
		HttpSession session = event.getSession();
		sessions.put(session.getId(), session);
	}

	@Override
	public void sessionDestroyed(HttpSessionEvent event) {
		UserDto userDto = null;
		LoginAttempts attempts = null;
		HttpSession session = event.getSession();
		try {
			userDto = (UserDto) session.getAttribute(Constants.LOGIN_USER);
			if (userDto != null && userDto.getLoginAttemptId() != null) {
				ApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(session.getServletContext());
				 LoginDetailsService loginDetailService = (LoginDetailsService) ctx.getBean("loginDetailsServiceImpl");
				attempts = loginDetailService.loadLoginAttempts(Integer.parseInt(userDto.getLoginAttemptId()));
				TransformDtoToEntity.getuserloginAttempt(userDto, attempts);
				loginDetailService.updateLoginDetails(attempts);
			}
			sessions.remove(event.getSession().getId());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public static HttpSession find(String sessionId) {
		return sessions.get(sessionId);
	}

	public static Map<String, HttpSession> getSessions() {
		return sessions;
	}
}