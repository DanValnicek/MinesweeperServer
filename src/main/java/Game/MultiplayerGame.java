package Game;

import com.company.Main;
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
	protected @Getter UUID uuid;
	@Getter
	private long startTime;
	private String setupMessage;
	private Timer countdown;

	public MultiplayerGame(UUID uuid) {
		this.uuid = uuid;
		setupMessage = createSetupMessage(10, 10, 20);
	}

	private String createSetupMessage(int rowCount, int columnCount, int mineCount) {
		List<Integer> map = Arrays.stream(MapGenerator.randMinesGen(rowCount * columnCount, mineCount)).boxed().collect(Collectors.toList());
		setupMessage = JsonGenerator.createGamePreparationMesssage(rowCount, columnCount, map, uuid).toString();
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
		if (players.size() >= 3) {
			Main.gamesHandler.newGame();
			startGame();
		}
	}

	public void deletePlayer(Channel playerChannel) {
		String user = players.get(playerChannel);
		players.remove(playerChannel);
		finished.remove(playerChannel);
		players.forEach((playerChan, username) -> {
			playerChan.writeAndFlush(JsonGenerator.createGameMessage(GameMessageTypes.pcc, List.of(user + " sa odpojil!", Integer.toString(players.size()))) + "\n");
		});
		if (players.size() <= 1) {
			players.forEach((player, username) -> {
				playerWon(player);
			});
			Main.gamesHandler.deleteGame(uuid);
			return;
		}
		if (players.size() == finished.size()) {
			countdown.cancel();
			countdown = null;
			players.forEach((player, username) -> {
				player.writeAndFlush(setupMessage + "\n");
			});
			startGame();
		}
	}

	private void playerWon(Channel playerChannel) {
		playerChannel.writeAndFlush(JsonGenerator.createGameMessage(GameMessageTypes.w, "Vyhral si!") + "\n");
		Main.gamesHandler.deleteGame(uuid);
	}

	private void startGame() {
		finished.clear();
		ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
		exec.schedule(() -> {
			players.forEach((player, username) -> {
				player.writeAndFlush(JsonGenerator.createGameMessage(GameMessageTypes.s, null) + "\n");
				startTime = System.currentTimeMillis();
			});
		}, 5, TimeUnit.SECONDS);
		setupMessage = createSetupMessage(10, 10, 20);
	}

	public void playerLost(Channel playerChannel) {
		deletePlayer(playerChannel);
		playerChannel.writeAndFlush(JsonGenerator.createGameMessage(GameMessageTypes.t, "Bol si eliminovaný") + "\n");

	}

	public void playerFinished(Channel playerChannel) {
		finished.put(playerChannel, System.currentTimeMillis() - startTime);
		if (players.size() == 1) {
			playerWon(playerChannel);
			countdown.cancel();
			countdown = null;
		}
		if (players.size() == finished.size()) {
			countdown.cancel();
			countdown = null;
			players.forEach((player, username) -> {
				player.writeAndFlush(setupMessage + "\n");
			});
			startGame();
			return;
		}
		if (countdown == null) {
			startCountdown();
			players.forEach((player, username) -> player.writeAndFlush(
					JsonGenerator.createGameMessage(GameMessageTypes.f, List.of("30",
							players.get(playerChannel) + " finished!")) + "\n"
			));
			return;
		}
		players.forEach((player, username) -> player.writeAndFlush(
				JsonGenerator.createGameMessage(GameMessageTypes.f, List.of("0",
						players.get(playerChannel) + " finished!")) + "\n"
		));
	}

	private synchronized void startCountdown() {
		countdown = new Timer();
		countdown.schedule(new TimerTask() {
			@Override
			public void run() {
				setupMessage = createSetupMessage(10, 10, 20);
				ArrayList<Channel> toRemove = new ArrayList();
				players.forEach((player, username) -> {
					if (!finished.containsKey(player)) {
						player.writeAndFlush(JsonGenerator.createGameMessage(GameMessageTypes.t, "Bol si eliminovaný") + "\n");
						toRemove.add(player);
					} else if (players.size() <= 1) {
						player.writeAndFlush(setupMessage + "\n");
					}
				});
				toRemove.forEach(player -> deletePlayer(player));
				if (players.size() <= 1) startGame();
			}
		}, 30000);

	}

	public void sendPlayerCount(Channel playerChannel) {
		playerChannel.writeAndFlush(JsonGenerator.createGameMessage(GameMessageTypes.pcc, List.of("", Integer.toString(players.size()))) + "\n");
	}
}
