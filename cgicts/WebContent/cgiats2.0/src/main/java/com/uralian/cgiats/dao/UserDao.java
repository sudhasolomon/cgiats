/*
 * UserDao.java May 16, 2012
 *
 * Copyright 2012 Uralian, LLC. All rights reserved.
 */
package com.uralian.cgiats.dao;

import java.util.List;
import java.util.Map;

import com.uralian.cgiats.dto.UserDto;
import com.uralian.cgiats.model.MobileRegistration;
import com.uralian.cgiats.model.Pages;
import com.uralian.cgiats.model.PagesHeader;
import com.uralian.cgiats.model.User;
import com.uralian.cgiats.model.UserRoles;

/**
 * @author Vlad Orzhekhovskiy
 */
public interface UserDao extends GenericDao<User, String> {

	void saveRegistration(MobileRegistration register);

	User getUserByCredentials(String username,String password,Boolean isActive);

	List<MobileRegistration> listRegistrations();

	List<PagesHeader> loadMenuCfgData();

	void saveCfg(UserRoles ur);

	List<UserRoles> loadUserRole(User user);

	UserRoles loadPage(String s, User user);

	Pages loadPages(String s);

	//void passwordRecovery(User user);

	User findByEmail(String email);
}
