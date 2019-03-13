package com.client;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.Scanner;

public class client {
	public static void main(String[] args) {

		Charset charset = Charset.forName("UTF-8");
		OutputStream outputStream = null;
		try {
			Socket socket = new Socket("localhost",8080);
			outputStream = socket.getOutputStream();

		} catch (IOException e) {
			e.printStackTrace();
		}

//		Scanner scanner = new Scanner(System.in);
//		System.out.println("请输入：");
//		String str = scanner.nextLine();
		String str = "111111111111111111111";

		try {
			outputStream.write(str.getBytes(charset));
		} catch (IOException e) {
			e.printStackTrace();
		}
		while (true){}

	}
}
