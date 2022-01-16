package Game;

import io.netty.channel.Channel;
import lombok.Getter;

import java.util.*;

public class MultiplayerGame {
	public boolean started = false;
	private HashMap<Channel, String> players;
	private LinkedHashMap<Channel, Long> finished;
	@Getter
	private long startTime;
	private int[] map;
	private Timer countdown;

	public MultiplayerGame() {
		map = MapGenerator.randMinesGen(100, 20);
	}

	public void addPlayer(Channel playerChannel, String username) {
		players.put(playerChannel, username);
		playerChannel.writeAndFlush(JsonGenerator.createGameMessage(GameMessageTypes.p, map));
		if (players.size() > 4) {
			started = true;
			startGame();
		}
	}

	private void startGame() {
		startTime = System.currentTimeMillis();
		players.forEach((player, time) -> {
			player.writeAndFlush(JsonGenerator.createGameMessage(GameMessageTypes.s, startTime));
		});
	}

	public void playerFinished(Channel playerChannel) {
		finished.put(playerChannel, System.currentTimeMillis() - startTime);
		if (players.size() == 2) {
			players.forEach((player, username) -> {
				if (!finished.containsKey(player)) {
					player.writeAndFlush(JsonGenerator.createGameMessage(GameMessageTypes.t, "Bol si eliminovaný"));
				} else {
					playerChannel.writeAndFlush(JsonGenerator.createGameMessage(GameMessageTypes.w, "Vyhral si!"));
				}
			});
		}
		if (countdown == null) startCountdown();
		players.forEach((player, username) -> player.writeAndFlush(
				JsonGenerator.createGameMessage(GameMessageTypes.f, List.of(finished.get(playerChannel)))
		));
	}

	private void startCountdown() {
		countdown = new Timer();
		countdown.schedule(new TimerTask() {
			@Override
			public void run() {
				map = MapGenerator.randMinesGen(100, 20);
				players.forEach((player, username) -> {
					if (!finished.containsKey(player)) {
						player.writeAndFlush(JsonGenerator.createGameMessage(GameMessageTypes.t, "Bol si eliminovaný"));
					} else {
						player.writeAndFlush(JsonGenerator.createGameMessage(GameMessageTypes.p, map));
					}
					startGame();
				});
			}
		}, 30000);
	}

}
