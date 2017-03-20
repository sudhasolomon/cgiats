package com.uralian.cgiats.model;

/**
 * @author Christian Rebollar
 */
public enum OrderByColumn {
	ID(" ORDER BY c.id"), FIRSTNAME(" ORDER BY c.firstName"), LASTNAME(" ORDER BY c.lastName"), LOCATION(
			" ORDER BY COALESCE(c.address.city, '') || COALESCE(c.address.state, '')"), TITLE(
					" ORDER BY c.title"), DATE(" ORDER BY COALESCE(c.updatedOn, c.createdOn)"), RELEVANCE(
							" ORDER BY sum(ck)"), FAVORITE(" ORDER BY c.hot"), BLACKLIST(
									" ORDER BY c.block"), UPDATEDON("ORDER BY c.updatedOn"), CREATEDON(
											"ORDER BY c.createdOn"), STATUS("ORDER BY c.status"), VISATYPE(
													"ORDER BY c.visaType");

	private final String orderByType;

	/**
	 * @param value
	 */
	private OrderByColumn(String value) {
		this.orderByType = value;
	}

	/**
	 * @return
	 */
	public String getValue() {
		return orderByType;
	}
}
