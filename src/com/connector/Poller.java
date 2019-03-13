package com.connector;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class Poller extends Conector {


	private Selector selector;
	private Acceptor acceptor;
	private ExecutorService executorService;
	private int state = -1;
	private ConcurrentLinkedQueue queue;
	private AtomicInteger wakeupCount;
	private Endpoint endpoint;
	private ByteBuffer byf;
	private ByteBuffer bigBf;
	private static Charset charset = Charset.forName("UTF-8");
	private static CharsetDecoder decoder = charset.newDecoder();

	//-----------------------------------------------------------------------------Constructor


	public Poller(Endpoint endpoint) {
		this.endpoint = endpoint;
	}


	//-------------------------------------------------------------------------------function

	@Override
	public void initInternal(){

		state = 1;
		acceptor = new Acceptor(endpoint, this);
		queue = new ConcurrentLinkedQueue();
		wakeupCount = new AtomicInteger(0);
		byf = ByteBuffer.allocateDirect(1024);
		bigBf = null;

		try {
			//打开选择器
			selector = Selector.open();
			//新建线程池
			executorService = Executors.newFixedThreadPool(5);

		}catch (Exception e){
			state = -1;
			e.printStackTrace();
		}

		System.out.println("Poller inited");
	}

	@Override
	public void initStartInternal() {
		state = 0;
		executorService.execute(new ExecutorPoller());
//		new Thread(new ExecutorPoller()).start();
		System.out.println("Poller started");
	}

	@Override
	public void initStopInternal() {
	}

	void registerChannel(SocketChannel socketChannel) throws ClosedChannelException {
		try {
			//设置为非阻塞
			socketChannel.configureBlocking(false);
			//设置为读就绪
			socketChannel.register(selector, SelectionKey.OP_READ);
			//如果之前没有添加，就唤醒
			if(wakeupCount.incrementAndGet() == 0)selector.wakeup();
			//添加到队列
			queue.add(socketChannel);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void setSuperState(int val){
		super.state = val;
	}

	//--------------------------------------------------------------------------------get/set



	//--------------------------------------------------------------------------------internal class
	public class ExecutorPoller implements Runnable{

		private int count;

		public ExecutorPoller(){
			count = 0;
		}

		@Override
		public void run() {

			while (true){
				try {
					if (0 == state) {

						if (0 != wakeupCount.get() && 0 != queue.size()) {
							count = selector.selectNow();
						} else {
							count = selector.select(1000);
						}
						wakeupCount.set(0);

					}

					if(count > 0) {

						Set<SelectionKey> keys = selector.selectedKeys();
						Iterator<SelectionKey> iterator = keys.iterator();

						while (iterator.hasNext()) {
							SelectionKey key = iterator.next();
							//这里不用做判断是否连接上了，因为我们分成了两步走，连接的在acceptor里边，这里边的是读就绪的
							processKey(key);
							key.channel();
						}

						iterator.remove();
					}
				}catch (Exception e){
					state = -1;
				}
			}

		}

		protected void processKey(SelectionKey key){
			//将数据读出来
			SocketChannel s = (SocketChannel)key.channel();

			int num = 0;
			try {
				bigBf = ByteBuffer.allocateDirect(2048);

				s.read(bigBf);

//				while ((s.read(byf)) != -1){
//					num++;
//
//					ByteBuffer temp = ByteBuffer.allocateDirect(1024 * (num + 1));
//
//					if(null != bigBf){
//						bigBf.flip();
//						temp.put(bigBf);
//					}
//
//					bigBf = temp;
//
//					byf.flip();
//					bigBf.put(byf);
//					byf.clear();
//				}



				if(bigBf != null){
					bigBf.flip();
					System.out.println(decoder.decode(bigBf).toString());
				}

			}catch (Exception e){
				e.printStackTrace();
				state = -1;
			}

		}

	}


}
