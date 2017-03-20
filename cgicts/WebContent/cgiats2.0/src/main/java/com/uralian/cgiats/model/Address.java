/*
 * Address.java Feb 29, 2012
 *
 * Copyright 2012 Uralian, LLC. All rights reserved.
 */
package com.uralian.cgiats.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import org.hibernate.annotations.Index;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.Store;

import com.uralian.cgiats.util.Utils;

/**
 * This class encapsulates address information.
 * 
 * @author Vlad Orzhekhovskiy
 */
@Embeddable
@Indexed
public class Address implements Serializable, Cloneable {
	private static final long serialVersionUID = 8034333751074894345L;

	@Column(name = "street1")
	private String street1;

	@Column(name = "street2")
	private String street2;

	@Column(name = "street3")
	private String street3;

	@Column(name = "street4")
	private String street4;

	@Column(name = "city")
	@Field(store = Store.YES)
	private String city;

	@Column(name = "state")
	@Field(store = Store.YES)
	private String state;

	@Column(name = "zipcode")
	private String zipcode;

	@Column(name = "country")
	private String country;

	/**
	 * Creates a new address.
	 */
	public Address() {
	}

	/**
	 * Creates a new address.
	 * 
	 * @param street1
	 * @param city
	 * @param state
	 * @param zipcode
	 * @param country
	 */
	public Address(String street1, String city, String state, String zipcode, String country) {
		this(street1, null, null, null, city, state, zipcode, country);
	}

	/**
	 * Creates a new address.
	 * 
	 * @param city
	 * @param state
	 * @param zipcode
	 */
	public Address(String city, String state, String zipcode) {
		this(null, null, null, null, city, state, zipcode, null);
	}

	/**
	 * Creates a new address.
	 * 
	 * @param street1
	 * @param street2
	 * @param street3
	 * @param street4
	 * @param city
	 * @param state
	 * @param zipcode
	 * @param country
	 */
	public Address(String street1, String street2, String street3, String street4, String city, String state,
			String zipcode, String country) {
		this.street1 = street1;
		this.street2 = street2;
		this.street3 = street3;
		this.street4 = street4;
		this.city = city;
		this.state = state;
		this.zipcode = zipcode;
		this.country = country;
	}

	/**
	 * @return Returns the street1.
	 */
	public String getStreet1() {
		return street1;
	}

	/**
	 * @param street1
	 *            The street1 to set.
	 */
	public void setStreet1(String street1) {
		this.street1 = street1;
	}

	/**
	 * @return Returns the street2.
	 */
	public String getStreet2() {
		return street2;
	}

	/**
	 * @param street2
	 *            The street2 to set.
	 */
	public void setStreet2(String street2) {
		this.street2 = street2;
	}

	/**
	 * @return Returns the street3.
	 */
	public String getStreet3() {
		return street3;
	}

	/**
	 * @param street3
	 *            The street3 to set.
	 */
	public void setStreet3(String street3) {
		this.street3 = street3;
	}

	/**
	 * @return Returns the street4.
	 */
	public String getStreet4() {
		return street4;
	}

	/**
	 * @param street4
	 *            The street4 to set.
	 */
	public void setStreet4(String street4) {
		this.street4 = street4;
	}

	/**
	 * @return Returns the city.
	 */
	public String getCity() {
		return city;
	}

	/**
	 * @param city
	 *            The city to set.
	 */
	public void setCity(String city) {
		this.city = city;
	}

	/**
	 * @return Returns the state.
	 */
	public String getState() {
		return state;
	}

	/**
	 * @param state
	 *            The state to set.
	 */
	public void setState(String state) {
		this.state = state;
	}

	/**
	 * @return Returns the zipcode.
	 */
	public String getZipcode() {
		return zipcode;
	}

	/**
	 * @param zipcode
	 *            The zipcode to set.
	 */
	public void setZipcode(String zipcode) {
		this.zipcode = zipcode;
	}

	/**
	 * @return Returns the country.
	 */
	public String getCountry() {
		return country;
	}

	/**
	 * @param country
	 *            The country to set.
	 */
	public void setCountry(String country) {
		this.country = country;
	}

	/**
	 * Returns the street part of the address, something like "123 Main St.".
	 * 
	 * @return the building/suite/appartment number and street of the address.
	 */
	public String getStreet() {
		StringBuffer sb = new StringBuffer();
		if (!Utils.isEmpty(street1))
			sb.append(street1);
		if (!Utils.isEmpty(street2))
			sb.append(" ").append(street2);
		if (!Utils.isEmpty(street3))
			sb.append(" ").append(street3);
		if (!Utils.isEmpty(street4))
			sb.append(" ").append(street4);

		return sb.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#clone()
	 */
	@Override
	public Address clone() {
		try {
			return (Address) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new IllegalStateException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();

		if (!Utils.isBlank(street1))
			sb.append(street1);
		if (!Utils.isBlank(street2))
			sb.append(" ").append(street2);
		if (!Utils.isBlank(street3))
			sb.append(" ").append(street3);
		if (!Utils.isBlank(street4))
			sb.append(" ").append(street4);

		if (!Utils.isBlank(city))
			sb.append(", ").append(city);
		if (!Utils.isBlank(state))
			sb.append(", ").append(state);
		if (!Utils.isBlank(zipcode))
			sb.append(" ").append(zipcode);
		if (!Utils.isBlank(country))
			sb.append(", ").append(country);

		if (sb.length() > 0 && sb.charAt(0) == ',')
			sb.deleteCharAt(0);

		return sb.toString();
	}
}