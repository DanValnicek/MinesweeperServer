package Game;

import io.netty.channel.Channel;
import lombok.Getter;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class MultiplayerGame {
	private final HashMap<Channel, String> players = new HashMap<>();
	private final LinkedHashMap<Channel, Long> finished = new LinkedHashMap<>();
	public boolean started = false;
	@Getter
//	private long startTime;
	protected UUID uuid = UUID.randomUUID();
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
		System.out.println(players);
		players.forEach((player, user) -> {
			player.writeAndFlush(JsonGenerator.createGameMessage(GameMessageTypes.pcc, List.of(username + " sa pripojil/a", Integer.toString(players.size()))) + "\n");
		});
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
			playerChan.writeAndFlush(JsonGenerator.createGameMessage(GameMessageTypes.pcc, List.of(user + " sa odpojil!", Integer.toString(players.size()))) + "\n");
		});
	}

	private void startGame() {
		startTime = System.currentTimeMillis();
		players.forEach((player, username) -> {
			player.writeAndFlush(JsonGenerator.createGameMessage(GameMessageTypes.s, startTime) + "\n");
		});
	}

	public void playerLost(Channel playerChannel) {
		deletePlayer(playerChannel);
		playerChannel.writeAndFlush(JsonGenerator.createGameMessage(GameMessageTypes.t, List.of("Bol si eliminovaný", null) + "\n"));
	}

	public void playerFinished(Channel playerChannel) {
		finished.put(playerChannel, System.currentTimeMillis() - startTime);
		if (players.size() == 1) {
			players.forEach((player, username) -> {
				if (!finished.containsKey(player)) {
					deletePlayer(player);
					player.writeAndFlush(JsonGenerator.createGameMessage(GameMessageTypes.t, List.of("Bol si eliminovaný", null) + "\n"));
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
						deletePlayer(player);
						player.writeAndFlush(JsonGenerator.createGameMessage(GameMessageTypes.t, List.of("Bol si eliminovaný", null) + "\n"));
					} else {
						player.writeAndFlush(setupMessage + "\n");
					}
					startGame();
				});
			}
		}, 30000);
	}

	public void sendPlayerCount(Channel playerChannel) {
		playerChannel.writeAndFlush(JsonGenerator.createGameMessage(GameMessageTypes.pcc, List.of("", Integer.toString(players.size())) + "\n"));
	}
}
