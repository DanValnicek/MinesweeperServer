package Game;

import com.company.MessageTypes;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.List;
import java.util.UUID;

import static Game.GameMessageTypes.p;

public class JsonGenerator {


	//e = error
	//n = notification
	//q = query
	//i = internal
	public static JSONObject createCallback(MessageTypes type, Object message) {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("messageType", type.toString());
		if (message != null) jsonObject.put("message", message);
		return jsonObject;
	}

	public static JSONObject createGameMessage(GameMessageTypes type, Object message) {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("gameMessageType", type.toString());
		if (message != null) jsonObject.put("message", message);
		return jsonObject;
	}

	public static JSONObject createGamePreparationMesssage(int rowCount, int columnCount, List<Integer> minePositions, UUID uuid) {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("gameMessageType", p.toString());
		jsonObject.put("rowCount", rowCount);
		jsonObject.put("columnCount", columnCount);
		jsonObject.put("uuid", uuid.toString());
		JSONArray jsonArray = new JSONArray();
		jsonArray.addAll(minePositions);
		jsonObject.put("minePositions", jsonArray);
		return jsonObject;
	}
}
