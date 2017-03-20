package com.uralian.cgiats.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * This class encapsulates information on a searcheable resume. It includes some
 * personal candidate's information, the original resume data, and a list of
 * keywords to search for.
 * 
 * @author Christian Rebollar
 */
@Entity
@Table(name = "designation")
public class Designation implements Serializable {
	private static final long serialVersionUID = 124873175298688432L;
	@Id
	@Column(name = "id")
	private Integer id;
	@Column(name = "name")
	private String name;
	@Column(name = "min_start_count")
	private Integer minStartCount;
	@Column(name = "max_start_count")
	private Integer maxStartCount;
	@Column(name = "avg_start_count")
	private Integer avgStartCount;

	
	
	public Integer getAvgStartCount() {
		return avgStartCount;
	}

	public void setAvgStartCount(Integer avgStartCount) {
		this.avgStartCount = avgStartCount;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getMinStartCount() {
		return minStartCount;
	}

	public void setMinStartCount(Integer minStartCount) {
		this.minStartCount = minStartCount;
	}

	public Integer getMaxStartCount() {
		return maxStartCount;
	}

	public void setMaxStartCount(Integer maxStartCount) {
		this.maxStartCount = maxStartCount;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}