package Game;

import io.netty.channel.Channel;
import lombok.Getter;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class MultiplayerGame {
	public boolean started = false;
	private HashMap<Channel, String> players = new HashMap<>();
	private LinkedHashMap<Channel, Long> finished = new LinkedHashMap<>();
	@Getter
	private long startTime;
	private String setupMessage;
	private Timer countdown;

	public MultiplayerGame() {
		setupMessage = createSetupMessage(10, 10, 20);
	}

	private String createSetupMessage(int rowCount, int columnCount, int mineCount) {
		List<Integer> map = Arrays.stream(MapGenerator.randMinesGen(rowCount * columnCount, mineCount)).boxed().collect(Collectors.toList());
		setupMessage = JsonGenerator.createGamePreparationMesssage(rowCount, columnCount, map).toString();
		return setupMessage;
	}

	public void addPlayer(Channel playerChannel, String username) {
		players.put(playerChannel, username);
		System.out.println(setupMessage);
		playerChannel.writeAndFlush(setupMessage + "\n");
		System.out.println(players.size());
		System.out.println(players.toString());
		if (players.size() >= 4) {
			started = true;
			ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
			exec.schedule(this::startGame, 5, TimeUnit.SECONDS);
		}
	}

	public void deletePlayer(Channel playerChannel) {
		String user = players.get(playerChannel);
		players.remove(playerChannel);
		finished.remove(playerChannel);
		players.forEach((playerChan, username) -> {
			playerChan.writeAndFlush(JsonGenerator.createGameMessage(GameMessageTypes.n, user + " sa odpojil!\n"));
		});
	}

	private void startGame() {
		startTime = System.currentTimeMillis();
		players.forEach((player, username) -> {
			player.writeAndFlush(JsonGenerator.createGameMessage(GameMessageTypes.s, startTime) + "\n");
		});
	}

	public void playerFinished(Channel playerChannel) {
		finished.put(playerChannel, System.currentTimeMillis() - startTime);
		if (players.size() == 1) {
			players.forEach((player, username) -> {
				if (!finished.containsKey(player)) {
					player.writeAndFlush(JsonGenerator.createGameMessage(GameMessageTypes.t, "Bol si eliminovaný\n"));
				} else {
					playerChannel.writeAndFlush(JsonGenerator.createGameMessage(GameMessageTypes.w, "Vyhral si!\n"));
				}
			});
		}
		if (countdown == null) startCountdown();
		players.forEach((player, username) -> player.writeAndFlush(
				JsonGenerator.createGameMessage(GameMessageTypes.f, List.of(30, players.get(playerChannel) + " finished!\n"))
		));
	}

	private void startCountdown() {
		countdown = new Timer();
		countdown.schedule(new TimerTask() {
			@Override
			public void run() {
				setupMessage = createSetupMessage(10, 10, 20);
				players.forEach((player, username) -> {
					if (!finished.containsKey(player)) {
						player.writeAndFlush(JsonGenerator.createGameMessage(GameMessageTypes.t, "Bol si eliminovaný\n"));
					} else {
						player.writeAndFlush(setupMessage + "\n");
					}
					startGame();
				});
			}
		}, 30000);
	}

}
