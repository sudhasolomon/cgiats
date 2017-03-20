package com.uralian.cgiats.util;

import org.hibernate.search.bridge.TwoWayStringBridge;

public class ByteFieldBridge implements TwoWayStringBridge{

	@Override
	public String objectToString(Object obj) {
		String stringObj = null;
		byte[] reason = (byte[]) obj;
		if(reason != null){
			//System.out.println("yes");
			stringObj = new String (reason);
		}
		return stringObj;
	}

	@Override
	public Object stringToObject(String stringValue) {
		byte[] reason = null;
		if(stringValue != null){
			//System.out.println(stringValue+" yes");
			  reason = stringValue.toString().getBytes();
		}
		return reason;
	}

}
