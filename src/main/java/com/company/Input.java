package com.company;

import Game.JsonGenerator;
import com.mysql.cj.xdevapi.DbDoc;
import com.mysql.cj.xdevapi.JsonNumber;
import com.mysql.cj.xdevapi.JsonString;
import org.json.simple.JSONObject;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static com.company.MessageTypes.e;

public class Input extends Validator {
	ArrayList<String> args = new ArrayList<>();
	String operation;
	List<Long> mapData = new ArrayList<>();
	public Input(DbDoc json) {
		if (json.containsKey("args")) {
			List<JsonString> jsonStrings = (List<JsonString>) json.get("args");
			for (JsonString string : jsonStrings) {
				this.args.add(string.getString());
			}
		}
		if (json.containsKey("mapData")) {
			List<JsonNumber> jsonNumbers = (List<JsonNumber>) json.get("mapData");
			for (JsonNumber js : jsonNumbers) {
				this.mapData.add(Long.valueOf(js.getInteger()));
			}
		}
		this.operation = ((JsonString) json.get("operation")).getString();
	}

	public JSONObject validate() throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
		if (operation.equals("iJoinToGame")) return JsonGenerator.createCallback(e, null);
		Validator validator = new Validator();
		Method method = validator.getClass().getDeclaredMethod(operation.substring(1), ArrayList.class);
		return JsonGenerator.createCallback(e, method.invoke(validator, args));
	}
}
