package com.company;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.sql.*;
import java.util.concurrent.ExecutionException;

/**
 * @author Steven Ou
 */

public class Server {

	private final int port;

	public Server(int port) throws SQLException {
		this.port = port;
	}

	public Server(String bindAddr, int bindPort, int port) throws IOException, ExecutionException, InterruptedException, SQLException {
		this.port = port;
		InetSocketAddress sockAddr = new InetSocketAddress(bindAddr, bindPort);

		//create a socket channel and bind to local bind address
		AsynchronousServerSocketChannel serverSock = AsynchronousServerSocketChannel.open().bind(sockAddr);

		//start to accept the connection from client
		serverSock.accept(serverSock, new CompletionHandler<AsynchronousSocketChannel, AsynchronousServerSocketChannel>() {

			@Override
			public void completed(AsynchronousSocketChannel sockChannel, AsynchronousServerSocketChannel serverSock) {
				//a connection is accepted, start to accept next connection
				serverSock.accept(serverSock, this);
				//start to read message from the client
				try {
					startRead(sockChannel);
				} catch (SQLException e) {
					e.printStackTrace();
				}

			}

			@Override
			public void failed(Throwable exc, AsynchronousServerSocketChannel serverSock) {
				System.out.println("fail to accept a connection from: " + serverSock);
			}

		});

	}

		Connection sqlConn = DriverManager.getConnection("jdbc:mysql://165.22.76.230:3306/minesweeperDatabase", "workServ", "EnterTheDB1.");
		Statement stmt = sqlConn.createStatement();
	public void run() throws SQLException {

		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		try {
			ServerBootstrap bootstrap = new ServerBootstrap()
					.group(bossGroup)
					.channel(NioServerSocketChannel.class)
					.localAddress(port);
			bootstrap.childHandler(new ChatServerInitializer());
			ChannelFuture future = bootstrap.bind(port).sync().channel().closeFuture().sync();

		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}

	private void startRead(AsynchronousSocketChannel sockChannel) throws SQLException {
		final ByteBuffer buf = ByteBuffer.allocate(2048);

		//read message from client
		sockChannel.read(buf, sockChannel, new CompletionHandler<Integer, AsynchronousSocketChannel>() {

			/**
			 * some message is read from client, this callback will be called
			 */
			ResultSet resultSet = stmt.executeQuery("select * from Users");
			@Override
			public void completed(Integer result, AsynchronousSocketChannel channel) {
				buf.flip();
while (true){
	try {
		if (!resultSet.next()) break;
	} catch (SQLException e) {
		e.printStackTrace();
	}
	try {
		System.out.println(resultSet.getInt(1)+"  "+resultSet.getString(2)+"  "+resultSet.getString(3));
	} catch (SQLException e) {
		e.printStackTrace();
	}
}
				// echo the message
				System.out.println("writing: " + buf);
				startWrite(channel, buf);

				//start to read next message again
				try {
					startRead(channel);
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void failed(Throwable exc, AsynchronousSocketChannel channel) {
				System.err.println(exc);
				System.out.println("fail to read message from client");
			}
		});
	}

	private void startWrite(AsynchronousSocketChannel sockChannel, final ByteBuffer buf) {
		sockChannel.write(buf, sockChannel, new CompletionHandler<Integer, AsynchronousSocketChannel>() {

			@Override
			public void completed(Integer result, AsynchronousSocketChannel channel) {
				//finish to write message to client, nothing to do

			}

			@Override
			public void failed(Throwable exc, AsynchronousSocketChannel channel) {
				//fail to write message to client
				System.out.println("Fail to write message to client");
			}

		});
	}
}



