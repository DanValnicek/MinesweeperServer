package com.company;

import Game.MultiplayerGame;
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
		for (MultiplayerGame game : games) {
			if (game.getUuid().equals(gameUUID)) return game;
		}
		return null;
	}
}
