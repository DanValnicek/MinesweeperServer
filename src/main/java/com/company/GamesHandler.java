package com.company;

import Game.MultiplayerGame;
import lombok.Getter;

import java.nio.channels.Channel;
import java.util.ArrayList;

public class GamesHandler {

	@Getter
	ArrayList<MultiplayerGame> games = new ArrayList<>();

	public MultiplayerGame getLastGame() {

		if (games.size() == 0 || games.get(games.size() - 1).started) {
			games.add(new MultiplayerGame());
		}
		return games.get(games.size() - 1);
	}
	public MultiplayerGame getGame(long startTime) {
		for (MultiplayerGame game : games) {
			if (game.getStartTime() == startTime) return game;
		}
		return null;
	}
}
