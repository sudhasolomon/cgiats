package com.uralian.cgiats.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uralian.cgiats.dao.ActiveAccountsDao;
import com.uralian.cgiats.model.ActiveAccounts;
import com.uralian.cgiats.model.User;
import com.uralian.cgiats.model.UserRole;
import com.uralian.cgiats.service.ActiveAccountsService;

/**
 * @author rajashekhar
 */

@Service
@Transactional
public class ActiveAccountsServiceImpl implements ActiveAccountsService {
	protected final Logger log = LoggerFactory.getLogger(getClass());
	@Autowired
	ActiveAccountsDao activeAccountsDao;

	@Override
	public List<User> loadUsers(UserRole userRole) {
		try {
			List<User> dmsList = null;
			dmsList = activeAccountsDao.loadUsers(userRole);
			return dmsList;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	public List<ActiveAccounts> getActivityAccounts() {
		try {
			List<ActiveAccounts> list = new ArrayList<ActiveAccounts>();
			list = activeAccountsDao.loadActAccounts();
			return list;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	public void updateActAccount(ActiveAccounts selectedActAccount) {
		try {
			activeAccountsDao.update(selectedActAccount);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
	}

	@Override
	public void saveActAccount(ActiveAccounts accounts) {
		try {
			activeAccountsDao.save(accounts);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
	}

	@Override
	public ActiveAccounts loadActAccount(Integer actAccountId) {
		try {
			return activeAccountsDao.findById(actAccountId);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

}
