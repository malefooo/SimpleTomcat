package com.connector;

import com.Lifecycle;

import java.io.IOException;

public abstract class Conector implements Lifecycle{

	protected int state = -1;

	//---------------------------------------------------------------------------function
	@Override
	public void start() {
		state = Lifecycle.START;
		initStartInternal();
	}

	@Override
	public void init() throws IOException {
		state = Lifecycle.INIT;
		initInternal();
	}

	@Override
	public void stop() {
		state = Lifecycle.STOP;
		initStopInternal();
	}

	public abstract void initInternal() throws IOException;

	public abstract void initStartInternal();

	public abstract void initStopInternal();
}
