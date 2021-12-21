package com.company;

import org.json.simple.JSONObject;

public class JsonGenerator {


	//e = error
	//n = notification
	//q = query
	//i = internal
	public static JSONObject createCallback(String type, Object message) {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("messageType", type);
		jsonObject.put("message", message);
		return jsonObject;
	}
}
