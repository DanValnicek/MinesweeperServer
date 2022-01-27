package Game;

import lombok.Getter;

import java.util.ArrayList;
import java.util.UUID;

public class GamesHandler {

	@Getter
	ArrayList<MultiplayerGame> games = new ArrayList<>();

	public MultiplayerGame getLastGame() {
		if (games.size() == 0 || games.get(games.size() - 1).started) {
			games.add(new MultiplayerGame());
		}
		return games.get(games.size() - 1);
	}

	public MultiplayerGame getGame(UUID gameUUID) {
		if (games.size() > 0) {
			for (MultiplayerGame game : games) {
				if (game.getUuid().equals(gameUUID)) return game;
			}
		}
		return null;
	}

	protected void deleteGame(UUID uuid) {
		for (int i = 0; i < games.size(); i++) {
			if (games.get(i).getUuid().equals(uuid)) {
				games.set(i, null);
				System.gc();
			}
		}
	}
}
