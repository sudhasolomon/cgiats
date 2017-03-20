package com.uralian.cgiats.model;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "group_menu")
@SequenceGenerator(name = "group_menu_seq", sequenceName = "group_menu_seq")
public class GroupMenu implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3038817318049777635L;

	@Id
	@Column
	@GeneratedValue(generator = "group_menu_seq", strategy = GenerationType.AUTO)
	int groupMenuId;
	@Column(name = "group_menu_name")
	private String groupMenuName;
	@ManyToOne
	@JoinColumn(name = "page_header_id")
	private PagesHeader pageHeaders;

	@OneToMany(mappedBy = "groupMenu",fetch=FetchType.EAGER)
	private Set<Pages> pages;

	public int getGroupMenuId() {
		return groupMenuId;
	}

	public void setGroupMenuId(int groupMenuId) {
		this.groupMenuId = groupMenuId;
	}

	public PagesHeader getPageHeaders() {
		return pageHeaders;
	}

	public void setPageHeaders(PagesHeader pageHeaders) {
		this.pageHeaders = pageHeaders;
	}

	public Set<Pages> getPages() {
		return pages;
	}

	public void setPages(Set<Pages> pages) {
		this.pages = pages;
	}

	public String getGroupMenuName() {
		return groupMenuName;
	}

	public void setGroupMenuName(String groupMenuName) {
		this.groupMenuName = groupMenuName;
	}

}
