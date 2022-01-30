package Game;

import lombok.Getter;

import java.util.HashMap;
import java.util.UUID;

public class GamesHandler {

	@Getter
	HashMap<UUID, MultiplayerGame> games = new HashMap<>();
	MultiplayerGame openGame;

	public MultiplayerGame getLastGame() {
		if (games.size() == 0) {
			newGame();
		}
		return openGame;
	}

	public void newGame() {
		UUID uuid = UUID.randomUUID();
		openGame = new MultiplayerGame(uuid);
		games.put(uuid, openGame);
	}

	public MultiplayerGame getGame(UUID gameUUID) {
		return games.get(gameUUID);
	}

	protected void deleteGame(UUID uuid) {
		games.remove(uuid);
	}
}

