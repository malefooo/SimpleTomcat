package com.connector;

import java.io.IOException;

public class Endpoint extends Conector {


	private Poller poller;
	private Acceptor acceptor;
	private String className = "Endpoint";


	//----------------------------------------------------------------------------------Constructor


	public Endpoint() {
		poller = new Poller(this);
		acceptor = new Acceptor(this, poller);
	}

	//--------------------------------------------------------------------------------------function

	@Override
	public void initInternal() {
		try {
			poller.init();
			acceptor.init();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Endpoint inited");
	}

	@Override
	public void initStartInternal() {
		poller.start();
		acceptor.start();
		System.out.println("Endpoint started");
	}

	@Override
	public void initStopInternal() {
	}

	//-----------------------------------------------------------------------------------get/set


}
