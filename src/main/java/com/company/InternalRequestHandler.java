package com.company;

import Game.JsonGenerator;
import io.netty.channel.Channel;
import org.json.simple.JSONObject;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

import static com.company.MessageTypes.e;
import static com.company.MessageTypes.i;

public class InternalRequestHandler {
	Channel incomingChannel;
	DBHandler dbHandler = new DBHandler();

	public InternalRequestHandler(Channel incomingChannel) {
		this.incomingChannel = incomingChannel;
	}

	public void JoinToGame(ArrayList<String> args) throws SQLException {
		String[] ipAndPort = incomingChannel.remoteAddress().toString().substring(1).split(":");
		Main.gamesHandler.getLastGame().addPlayer(incomingChannel, dbHandler.executeQuery(
						Arrays.asList(ipAndPort), "qFindUser")
				.get("message").toString());
	}

	public void LostGame(ArrayList<String> args) {
		Main.gamesHandler.getGame(Long.parseLong(args.get(0))).playerLost(incomingChannel);
	}

	private void LeaveGame(ArrayList<String> args) {
		Main.gamesHandler.getGame(Long.parseLong(args.get(0))).deletePlayer(incomingChannel);
	}

	public void ReportFinishedMap(ArrayList<String> args) {
		Main.gamesHandler.getGame(Long.parseLong(args.get(0))).playerFinished(incomingChannel);
	}

	public JSONObject Connect(ArrayList<String> args) throws SQLException {
		args.add(incomingChannel.remoteAddress().toString());
		System.out.println(Arrays.toString(args.toArray()));
		return dbHandler.executeQuery(args, "qConnect");
	}

	public JSONObject execute(String operation, ArrayList args) throws InvocationTargetException, NoSuchMethodException {
		Method method = this.getClass().getDeclaredMethod(operation.substring(1), ArrayList.class);
		Object callback;
		try {
			callback = method.invoke(this, args);
			if (!(callback instanceof JSONObject) && callback != null) {
				System.out.println("not jsonobject");
				callback = JsonGenerator.createCallback(i, callback);
			}
		} catch (IllegalAccessException ex) {
			callback = JsonGenerator.createCallback(e, ex.getMessage());
		}
		return (JSONObject) callback;
	}

}
