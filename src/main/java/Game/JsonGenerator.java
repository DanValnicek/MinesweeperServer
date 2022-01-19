package Game;

import com.company.MessageTypes;
import org.json.simple.JSONObject;

public class JsonGenerator {


	//e = error
	//n = notification
	//q = query
	//i = internal
	public static JSONObject createCallback(MessageTypes type, Object message) {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("messageType", type.toString());
		jsonObject.put("message", message);
		return jsonObject;
	}

	public static JSONObject createGameMessage(GameMessageTypes type, Object message) {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("gameMessageType", type);
		jsonObject.put("message", message);
		return jsonObject;
	}
}
