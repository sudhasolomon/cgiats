package com.uralian.cgiats.proxy.json;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.uralian.cgiats.util.Utils;

/**
 * @author Jorge
 */
public class JsonHelper {
	private static Logger log = LoggerFactory.getLogger(JsonHelper.class);

	/**
	 * @param chain
	 * @return
	 */
	public static JsonMonsterResume createMonsterResume(String chain) {
		if (Utils.isEmpty(chain))
			return null;

		JSONParser parser = new JSONParser();
		try {
			JSONObject parsedMap = (JSONObject) parser.parse(chain);
			return JsonMonsterResume.fromJSONString(parsedMap);
		} catch (ParseException e) {
			log.error("Error parsing the JSON chain", e.getMessage(), e);
			return null;
		}
	}
}
