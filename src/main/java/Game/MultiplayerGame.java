package Game;

import com.company.DBHandler;
import io.netty.channel.Channel;

import java.util.HashMap;
import java.util.LinkedHashMap;

public class MultiplayerGame {
	private static HashMap<Channel, String> players;
	private final DBHandler dbHandler = new DBHandler();
	private LinkedHashMap<Channel, Long> finished;
	private long startTime;
	private int[] map;

	public MultiplayerGame() {

	}

	public static void addPlayer(Channel playerChannel, String username) {
		players.put(playerChannel, username);
	}

	private void prepareGame() {
		map = MapGenerator.randMinesGen(900, 450);
		players.forEach((player, time) -> player.writeAndFlush(JsonGenerator.createGameMessage(GameMessageTypes.p, map)));
	}

	//
	private void startGame() {
		startTime = System.currentTimeMillis();
		players.forEach((player, time) -> {
			player.writeAndFlush(JsonGenerator.createGameMessage(GameMessageTypes.s, null));
		});
	}

	public void playerFinished(Channel playerChannel) {
		finished.put(playerChannel, System.currentTimeMillis() - startTime);
//		players.forEach(player -> {player.writeAndFlush(JsonGenerator.createGameMessage(GameMessageTypes.f, Arrays.asList( finished.get(playerChannel)))});
	}


	//TODO: startGame method
	//TODO: ending algorithm
}
