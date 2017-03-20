package com.uralian.cgiats.model;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "pages_header")
@SequenceGenerator(name = "pages_header_seq", sequenceName = "pages_header_seq")
public class PagesHeader implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6620440347140992309L;
	@Id
	@Column(name = "pages_header_id")
	@GeneratedValue(generator = "pages_header_seq", strategy = GenerationType.AUTO)
	private int pageHeaderId;
	@Column(name = "header_name")
	private String headerName;
	@OneToMany(mappedBy = "pagesHeader" ,fetch=FetchType.EAGER)
	private Set<Pages> pages;
	
	@OneToMany(mappedBy="pageHeaders",fetch=FetchType.EAGER)
	private Set<GroupMenu> groupMenu;

	public int getPageHeaderId() {
		return pageHeaderId;
	}

	public void setPageHeaderId(int pageHeaderId) {
		this.pageHeaderId = pageHeaderId;
	}

	public Set<Pages> getPages() {
		return pages;
	}

	public void setPages(Set<Pages> pages) {
		this.pages = pages;
	}

	public String getHeaderName() {
		return headerName;
	}

	public void setHeaderName(String headerName) {
		this.headerName = headerName;
	}

	public Set<GroupMenu> getGroupMenu() {
		return groupMenu;
	}

	public void setGroupMenu(Set<GroupMenu> groupMenu) {
		this.groupMenu = groupMenu;
	}

}
