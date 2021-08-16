package com.company;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 *
 * @author Steven Ou
 */

public class Server {

//    public static void main(String[] args) throws IOException {
////        ServerSocket serverSocket = new ServerSocket(4999);
////        Socket socket = serverSocket.accept();
////        System.out.println("connected");
////        InputStreamReader inputStreamReader = new InputStreamReader(socket.getInputStream());
////        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
////        String response = bufferedReader.readLine();
////        System.out.println("response: " + response);
//
//        try (AsynchronousServerSocketChannel serverSocketChannel = AsynchronousServerSocketChannel.open()) {
//            serverSocketChannel.bind(new InetSocketAddress("localhost",4999));
//            while (true) {
//            Future<AsynchronousSocketChannel> listener = serverSocketChannel.accept();
//
//                AsynchronousSocketChannel client = listener.get();
//                if ((client != null) && (client.isOpen())) {
////                    PrintWriter out = new PrintWriter();
//                    ByteBuffer buffer = ByteBuffer.allocate(1024);
//                    Future<Integer> readVal = client.read(buffer);
//                    System.out.println("Response from client: " + new String(buffer.array()).trim());
//                    System.out.println(Arrays.toString(buffer.array()));
//                    readVal.get();
////                    System.out.println(readVal);
//                    buffer.flip();
//                    String str = "Back to you!";
//                    Future<Integer> writeVal = client.write(
//                            ByteBuffer.wrap(str.getBytes())
//                    );
//                    System.out.println("Client response: " + buffer);
//                    writeVal.get();
//                    buffer.clear();
//
//                }
//            }
////            Objects.requireNonNull(client).close();
//        } catch (ExecutionException | InterruptedException e) {
//            e.printStackTrace();
//            System.err.println();
//        }
//    }

    public Server( String bindAddr, int bindPort ) throws IOException {
        InetSocketAddress sockAddr = new InetSocketAddress(bindAddr, bindPort);

        //create a socket channel and bind to local bind address
        AsynchronousServerSocketChannel serverSock =  AsynchronousServerSocketChannel.open().bind(sockAddr);

        //start to accept the connection from client
        serverSock.accept(serverSock, new CompletionHandler<AsynchronousSocketChannel,AsynchronousServerSocketChannel >() {

            @Override
            public void completed(AsynchronousSocketChannel sockChannel, AsynchronousServerSocketChannel serverSock ) {
                //a connection is accepted, start to accept next connection
                serverSock.accept( serverSock, this );
                //start to read message from the client
                startRead( sockChannel );

            }

            @Override
            public void failed(Throwable exc, AsynchronousServerSocketChannel serverSock) {
                System.out.println( "fail to accept a connection");
            }

        } );

    }

    private void startRead( AsynchronousSocketChannel sockChannel ) {
        final ByteBuffer buf = ByteBuffer.allocate(2048);

        //read message from client
        sockChannel.read( buf, sockChannel, new CompletionHandler<Integer, AsynchronousSocketChannel >() {

            /**
             * some message is read from client, this callback will be called
             */

            @Override
            public void completed(Integer result, AsynchronousSocketChannel channel  ) {
                buf.flip();

                // echo the message
                startWrite( channel, buf );

                //start to read next message again
                startRead( channel );
            }

            @Override
            public void failed(Throwable exc, AsynchronousSocketChannel channel ) {
                System.err.println(exc);
                System.out.println( "fail to read message from client");
            }
        });
    }

    private void startWrite( AsynchronousSocketChannel sockChannel, final ByteBuffer buf) {
        sockChannel.write(buf, sockChannel, new CompletionHandler<Integer, AsynchronousSocketChannel >() {

            @Override
            public void completed(Integer result, AsynchronousSocketChannel channel) {
                //finish to write message to client, nothing to do
            }

            @Override
            public void failed(Throwable exc, AsynchronousSocketChannel channel) {
                //fail to write message to client
                System.out.println( "Fail to write message to client");
            }

        });
    }

    public static void main( String[] args ) {
        try {
            new Server( "127.0.0.1", 3575 );
            for( ; ; ) {
                Thread.sleep(10*1000);
            }
        } catch (Exception ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
