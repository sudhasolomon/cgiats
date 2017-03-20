/**
 * 
 */
package com.uralian.cgiats.util;

/**
 * @author skurapati
 *
 */
public enum SubmittalColors {
	SUBMITTED("SUBMITTED", "#00afab"), DMREJ("DMREJ", "#ffb1e3"), ACCEPTED("ACCEPTED", "#62a0ca"), INTERVIEWING("INTERVIEWING", "#ffa556"), CONFIRMED(
			"CONFIRMED",
			"#b7e8ad"), REJECTED("REJECTED", "#e26868"), STARTED("STARTED", "#6bbc6b"), BACKOUT("BACKOUT", "#000000"), OUTOFPROJ("OUTOFPROJ", "#c6d8ef");
	private String name, color;

	SubmittalColors(String name, String color) {
		this.name = name;
		this.color = color;
	}

	public String getColor() {
		return color;
	}

	public String getName() {
		return name;
	}
}
