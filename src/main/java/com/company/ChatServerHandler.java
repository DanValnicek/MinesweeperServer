package com.company;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

public class ChatServerHandler extends SimpleChannelInboundHandler<String> {

	private static final ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
	DBHandler dbHandler = new DBHandler();

	@Override
	public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
		System.out.println("idk handlerAdded");
		Channel incoming = ctx.channel();
		System.out.println(incoming.toString());
		System.out.println(channels);
		if (!incoming.isRegistered()) channels.add(incoming);
		for (Channel channel : channels) {
			channel.writeAndFlush("[SERVER] - " + incoming.remoteAddress() + " has joined!\n");
			System.out.println("[SERVER] - " + incoming.remoteAddress() + " has joined!\n");
		}
	}

	@Override
	public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
		System.out.println("idk handlerRemoved");
		Channel incoming = ctx.channel();
		for (Channel channel : channels) {
			channel.writeAndFlush("[SERVER] - " + incoming.remoteAddress() + " has left!\n");
			System.out.println("[SERVER] - " + incoming.remoteAddress() + " has left!\n");
		}
		channels.remove(ctx.channel());
	}

	public void channelRead0(ChannelHandlerContext ctx, String message) throws Exception {
		System.out.println("idk channelRead");
		Channel incoming = ctx.channel();
		for (Channel channel : channels) {
//            if (channel != incoming) {
			channel.writeAndFlush("[" + channel.remoteAddress() + "] " + message + "\n");
			System.out.println("[" + channel.remoteAddress() + "] " + message + "\n");
			dbHandler.executeQuery(message);
		}
//            }
	}
}

