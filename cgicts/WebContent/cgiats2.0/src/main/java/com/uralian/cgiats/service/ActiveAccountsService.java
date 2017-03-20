package com.uralian.cgiats.service;

import java.util.List;

import com.uralian.cgiats.model.ActiveAccounts;
import com.uralian.cgiats.model.User;
import com.uralian.cgiats.model.UserRole;

public interface ActiveAccountsService {

	List<User> loadUsers(UserRole userRole);

	List<ActiveAccounts> getActivityAccounts();

	void updateActAccount(ActiveAccounts selectedActAccount);

	void saveActAccount(ActiveAccounts accounts);

	ActiveAccounts loadActAccount(Integer actAccountId);

}
