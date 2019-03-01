package com.zsoft.signala.transport.longpolling;

import org.json.JSONException;
import org.json.JSONObject;

public class JSONHelper {

	public static JSONObject ToJSONObject(String text)
	{
		// https://bugly.qq.com/v2/crash-reporting/crashes/99e030d080/10502?pid=1
		if (text == null) {
			return null;
		}
		JSONObject json;
		try {
			json = new JSONObject(text);
		} catch (JSONException e) {
			json = null;
		}
		return json;
	}
}
