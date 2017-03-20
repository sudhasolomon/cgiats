package com.uralian.cgiats.dao;



import java.util.List;

import com.uralian.cgiats.model.ActiveAccounts;
import com.uralian.cgiats.model.User;
import com.uralian.cgiats.model.UserRole;

public interface ActiveAccountsDao extends GenericDao<ActiveAccounts, Integer>{

	List<User> loadUsers(UserRole userRole);

	List<ActiveAccounts> loadActAccounts();

}
