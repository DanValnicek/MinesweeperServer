package com.company;

import Game.JsonGenerator;
import com.mysql.cj.xdevapi.DbDoc;
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

	public Input(DbDoc json) {
		List<JsonString> jsonStrings = (List<JsonString>) json.get("args");
		for (JsonString string : jsonStrings) {
			this.args.add(string.getString());

		}
		this.operation = ((JsonString) json.get("operation")).getString();
	}

	public JSONObject validate() throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
		Validator validator = new Validator();
		Method method = validator.getClass().getDeclaredMethod(operation.substring(1), ArrayList.class);
		return JsonGenerator.createCallback(e, method.invoke(validator, args));
	}
}
