package com.connector;

import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class Acceptor extends Conector implements Runnable{

	private Endpoint endpoint;
	private Poller poller;
	private ServerSocketChannel serverSocketChannel;
	private int state = -1;


	//------------------------------------------Constructor

	public Acceptor(Endpoint endpoint, Poller poller) {
		this.endpoint = endpoint;
		this.poller = poller;
	}

	//------------------------------------------function

	@Override
	public void initInternal() {
		try {
			//打开通道
			serverSocketChannel = ServerSocketChannel.open();
			//绑定端口
			serverSocketChannel.bind(new InetSocketAddress(8080));
			//模拟tomcat，接收线程时是阻塞
			serverSocketChannel.configureBlocking(true);
			//不用注册，注册是等到接收到请求新建一个channel那个注册

		}catch (Exception e){
			super.state = -1;
			e.printStackTrace();
		}

		state = 1;
		System.out.println("Acceptor inited");
	}

	@Override
	public void initStartInternal() {
		state = 0;
		new Thread(this).start();
		System.out.println("Acceptor started");
	}

	@Override
	public void initStopInternal() {
	}

	@Override
	public void run() {

		while (true){
			try {

				SocketChannel channel = null;

				if (super.state == 0) {
					channel = serverSocketChannel.accept();
				} else {
					break;
				}

				//注册
				poller.registerChannel(channel);

			}catch (Exception e){
				super.state = -1;
			}
		}

	}
}
