/**
 * 
 */
package com.uralian.cgiats.config;

import java.util.Date;

import org.apache.lucene.document.Document;
import org.hibernate.search.bridge.LuceneOptions;
import org.hibernate.search.bridge.TwoWayFieldBridge;

import com.uralian.cgiats.util.Utils;

/**
 * @author skurapati
 *
 */
public class DateToStringConvertBridge implements TwoWayFieldBridge {
	/**
	 * Getting string formatted long value to string type in the date formatted
	 * value
	 */
	@Override
	public Object get(String name, Document document) {
		String value = document.get(name);
		if (value != null) {
			Long l = Long.valueOf(value);
			Date date = new Date(l);
			String d = Utils.convertDateToString(date);
			return d;
		} else {
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.hibernate.search.bridge.TwoWayFieldBridge#objectToString(java.lang.
	 * Object)
	 */
	@Override
	public String objectToString(Object object) {
		if (object != null) {
			Date date = (Date) object;
			return Utils.convertDateToString_HH_MM(date);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.hibernate.search.bridge.FieldBridge#set(java.lang.String,
	 * java.lang.Object, org.apache.lucene.document.Document,
	 * org.hibernate.search.bridge.LuceneOptions)
	 */
	@Override
	public void set(String name, Object value, Document document, LuceneOptions luceneOptions) {
		if (value != null) {
			Date date = (Date) value;
			// Converting long value of date to string
			luceneOptions.addFieldToDocument(name, String.valueOf(date.getTime()), document);
		}

	}
}
