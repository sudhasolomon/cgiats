package com.uralian.cgiats.model;

import java.io.Serializable;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

@Entity
@Table(name = "user_role")
@SequenceGenerator(name = "user_roles_seq", sequenceName = "user_roles_seq")
public class UserRoles implements Serializable {

	private static final long serialVersionUID = 7696016974402899419L;

	@Id
	@Column(name = "user_role_id")
	@GeneratedValue(generator = "user_roles_seq", strategy = GenerationType.AUTO)
	private int userRoleId;
	@Column(name = "user_role")
	@Enumerated(EnumType.STRING)
	
	private UserRole userRole;
	@ManyToOne
	@JoinColumn(name = "user_id")
	@Fetch(FetchMode.JOIN)
	@BatchSize(size = 20)
	private User user;
	@Column(name = "status")
	private String status;
	@ManyToOne
	@JoinColumn(name = "page_id")
	@Fetch(FetchMode.JOIN)
	@BatchSize(size = 20)
	private Pages pageName;
	@Column(name = "page_name")
	private String page;

	public int getUserRoleId() {
		return userRoleId;
	}

	public void setUserRoleId(int userRoleId) {
		this.userRoleId = userRoleId;
	}

	public UserRole getUserRole() {
		return userRole;
	}

	public String getPage() {
		return page;
	}

	public void setPage(String page) {
		this.page = page;
	}

	public void setUserRole(UserRole userRole) {
		this.userRole = userRole;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Pages getPageName() {
		return pageName;
	}

	public void setPageName(Pages pageName) {
		this.pageName = pageName;
	}

}
