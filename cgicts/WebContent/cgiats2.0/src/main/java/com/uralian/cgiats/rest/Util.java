package com.uralian.cgiats.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.uralian.cgiats.model.User;
import com.uralian.cgiats.service.UserService;

@Component
public class Util {

	@Autowired
	private UserService userService;

	public UserRoleVo checkingRoleByUser(String userId) {
		UserRoleVo userRoleDto = new UserRoleVo();

		try {
			if (userId != null && userId.length() > 0) {
				User user = userService.loadUser(userId);

				if (user.isAdmin()) {
					return null;
				} else if (user.isDM()) {
					userRoleDto.setUserName(user.getUserId());
					userRoleDto.setUserRole(user.getUserRole().toString());
					return userRoleDto;
				} else if (user.isADM()) {

					userRoleDto.setUserName(user.getAssignedBdm());
					userRoleDto.setUserRole(user.getUserRole().toString());
					return userRoleDto;
				} else if (user.isRecruiter()) {
					userRoleDto.setUserName(user.getAssignedBdm());
					userRoleDto.setUserRole(user.getUserRole().toString());
					return userRoleDto;

				}

			} else {
				return null;
			}
			return userRoleDto;

		} catch (Exception e) {
			e.printStackTrace();
			return null;

		}
	}
}
