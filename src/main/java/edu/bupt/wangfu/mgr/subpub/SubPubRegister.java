package edu.bupt.wangfu.mgr.subpub;

import edu.bupt.wangfu.info.msg.SPRegister;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by lenovo on 2016-11-4.
 */
public class SubPubRegister implements Runnable {
	private ServerSocket ss;
	private ExecutorService executorService;

	SubPubRegister(int port) {
		try {
			this.ss = new ServerSocket(port);
			int POOL_SIZE = 4;
			this.executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * POOL_SIZE);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		Socket s = null;
		while (true) {
			try {
				s = ss.accept();
				executorService.execute(new RegisterHandler(s));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private class RegisterHandler implements Runnable {
		private Socket s;

		RegisterHandler(Socket s) {
			this.s = s;
		}

		@Override
		public void run() {
			String msg;
			try {
				ObjectInputStream ois = SocketUtil.getObjReader(s);
				ObjectOutputStream oos = SocketUtil.getObjWriter(s);
				Object obj = ois.readObject();
				while (obj instanceof SPRegister){} /*{
					System.out.println(msg);
					pw.println(echo(msg));

					if (msg.equals("bye")) {
						break;
					}
				}*/
			} catch (IOException | ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	}
}
