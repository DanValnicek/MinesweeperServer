package com.company;

import Game.JsonGenerator;
import io.netty.channel.Channel;
import org.json.simple.JSONObject;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.ArrayList;

import static com.company.MessageTypes.e;

public class InternalRequestHandler {
	Channel incomingChannel;
	DBHandler dbHandler = new DBHandler();

	public InternalRequestHandler(Channel incomingChannel) {
		this.incomingChannel = incomingChannel;
	}

	public void joinToGame() {
//		MultiplayerGame.addPlayer(incomingChannel,);
	}

	public void Connect(ArrayList args) throws SQLException {
		dbHandler.executeQuery(args, incomingChannel.remoteAddress().toString());
	}

	public JSONObject execute(String operation, ArrayList args) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
		Method method = this.getClass().getDeclaredMethod(operation.substring(1), ArrayList.class);
		return JsonGenerator.createCallback(e, method.invoke(this, args));
	}
	//TODO: leaveGame method
	//TODO:

}
