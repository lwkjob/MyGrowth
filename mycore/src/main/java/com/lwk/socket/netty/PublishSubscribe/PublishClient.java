package com.lwk.socket.netty.PublishSubscribe;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class PublishClient {

	public static void main(String[] args) throws IOException {

		Socket socket = null;
		OutputStream out = null;

		try {

			socket = new Socket("localhost", 8080);
			out = socket.getOutputStream();
			out.write("Hello\r\n".getBytes()); // 发布信息到服务器
			out.flush();

		} finally {
			// 关闭连接
			out.close();
			socket.close();
		}
	}
}