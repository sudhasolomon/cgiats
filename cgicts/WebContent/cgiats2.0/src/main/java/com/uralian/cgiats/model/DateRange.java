package com.uralian.cgiats.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name="date_range")
@SequenceGenerator(sequenceName="datarange_seq", name = "datarange_seq")
public class DateRange implements Serializable {

	private static final long serialVersionUID = 1639535642952087324L;

	@Id
	@Column(name = "id")
	@GeneratedValue(generator="datarange_seq",strategy=GenerationType.AUTO)
	int id;
	@Column(name = "fromDate")
	private Date fromDate;
	@Column(name = "toDate")
	private Date toDate;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public Date getFromDate() {
		return fromDate;
	}
	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}
	public Date getToDate() {
		return toDate;
	}
	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

}
