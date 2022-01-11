package com.company;

import Game.JsonGenerator;
import com.mysql.cj.xdevapi.JsonParser;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.json.simple.JSONObject;

import java.util.Arrays;
import java.util.List;

import static com.company.MessageTypes.i;

public class ChatServerHandler extends SimpleChannelInboundHandler<String> {

	private static final ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
	DBHandler dbHandler = new DBHandler();

	@Override
	public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
		System.out.println("unregistered " + ctx.channel().remoteAddress());
		ctx.fireChannelUnregistered();
		ctx.channel().closeFuture();
	}

	@Override
	public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
		System.out.println("idk handlerAdded");
		Channel incoming = ctx.channel();
		System.out.println(incoming.remoteAddress());
		System.out.println(channels);

		channels.removeIf(channel -> channel.remoteAddress().equals(incoming.remoteAddress()));
		System.out.println(Arrays.toString(channels.toArray()));
		channels.add(incoming);
		System.out.println(JsonGenerator.createCallback(i, "[SERVER] - " + incoming.remoteAddress() + " has joined!\n"));
		for (Channel channel : channels) {
			channel.writeAndFlush(JsonGenerator.createCallback(i, "[SERVER] - " + incoming.remoteAddress() + " has joined!"));
		}
	}

	@Override
	public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
		System.out.println("idk handlerRemoved");
		Channel incoming = ctx.channel();
		dbHandler.executeQuery(List.of(incoming.remoteAddress().toString()), "uDisconnect");
		for (Channel channel : channels) {
			channel.writeAndFlush(JsonGenerator.createCallback(i, "[SERVER] - " + incoming.remoteAddress() + " has left!"));
			System.out.println("[SERVER] - " + incoming.remoteAddress() + " has left!\n");
		}
		channels.remove(ctx.channel());
	}

	public void channelRead0(ChannelHandlerContext ctx, String message) throws Exception {
		System.out.println("idk channelRead");
//		for (Channel channel : channels) {
//            if (channel != incoming) {
		System.out.println("[" + ctx.channel().remoteAddress() + "] " + message + "\n");
		JSONObject callBack;

		try {
			Input input = new Input(JsonParser.parseDoc(message));
			callBack = input.validate();
//			System.out.println("Validation message: " + callBack);
			if (callBack.get("message") == null) {
				if (input.operation.startsWith("u") || input.operation.startsWith("q")) {
					callBack = dbHandler.executeQuery(input.args, input.operation);
				} else if (input.operation.startsWith("i")) {
					InternalRequestHandler internalRequestHandler = new InternalRequestHandler(ctx.channel());
					internalRequestHandler.execute(input.operation, input.args);
					callBack = null;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			callBack = JsonGenerator.createCallback(MessageTypes.e, e.toString());
		}
		if (callBack != null) {
			System.out.println(callBack);
			ctx.channel().writeAndFlush(callBack + "\n");
		}
	}
//            }
}


