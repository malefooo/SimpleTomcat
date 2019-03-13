package com;

import java.io.IOException;

public interface Lifecycle {

	int START = 0;

	int STOP = 1;

	int INIT = 2;

	//------------------------------------------------------------------------function

	void start();

	void init() throws IOException;

	void stop();
}
