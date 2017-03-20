package com.uralian.cgiats.model;

import java.io.Serializable;

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
@Table(name = "pages")
@SequenceGenerator(name = "pages_seq", sequenceName = "pages_seq")
public class Pages implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8574039080978394376L;

	@Id
	@Column(name = "pages_id")
	@GeneratedValue(generator = "pages_seq", strategy = GenerationType.AUTO)
	private int pagesId;

	@Column(name = "page_name")
	private String pageName;

	@ManyToOne
	@JoinColumn(name = "id")
	private PagesHeader pagesHeader;
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="group_menu_id" )
	private GroupMenu groupMenu;

		

	public int getPagesId() {
		return pagesId;
	}

	public void setPagesId(int pagesId) {
		this.pagesId = pagesId;
	}

	public String getPageName() {
		return pageName;
	}

	public void setPageName(String pageName) {
		this.pageName = pageName;
	}

	public PagesHeader getPagesHeader() {
		return pagesHeader;
	}

	public void setPagesHeader(PagesHeader pagesHeader) {
		this.pagesHeader = pagesHeader;
	}

	public GroupMenu getGroupMenu() {
		return groupMenu;
	}

	public void setGroupMenu(GroupMenu groupMenu) {
		this.groupMenu = groupMenu;
	}

}
