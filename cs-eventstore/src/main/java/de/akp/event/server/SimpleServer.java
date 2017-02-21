package de.akp.event.server;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import de.akp.event.generate.Event;
import de.akp.event.generate.EventPool;
import de.akp.event.generate.IEventGenerator;
import de.akp.event.generate.OpenCloseEvent;
import de.akp.event.generate.SingleOpenGenerator;

public class SimpleServer {

	private int port;
	private int waitMillis;
	
	private volatile boolean doRun = true;

	ExecutorService executor = Executors.newFixedThreadPool(10);

	private IEventGenerator<?> generator;

	public SimpleServer(int port, IEventGenerator<?> eventGenerator) {
		this(port, eventGenerator, 0);
	}

	public SimpleServer(int port, IEventGenerator<?> eventGenerator, int waitMillis) {
		this.port = port;
		generator = eventGenerator;
		this.waitMillis = waitMillis;
	}

	public void centralLoop() {
		Server server = new Server();
		new Thread(server).start();
	}
	
	public void shutDown() {
		this.doRun = false;
	}

	// curl localhost:8082
	public static void main(String[] args) {
		SimpleServer simpleServer = new SimpleServer(8082, new OpenCloseEvent.EventGenerator(), 100);
		simpleServer.centralLoop();
		try {
			TimeUnit.SECONDS.sleep(15);
		} catch (InterruptedException e) {
		}
		simpleServer.shutDown();
		System.out.println("Shutting Down...");
	}
	
	private class Server implements Runnable {

		@Override
		public void run() {
			Loop task = null;
			try (ServerSocket server = new ServerSocket(port)) {
				while (doRun) {
					try {
						Socket connection = server.accept();
						task = new Loop(connection);
						executor.submit(task);
					} catch (IOException e) {
					}
				}
				executor.shutdown();
				executor.shutdownNow();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		
	}
	
	private class Loop implements Callable<String> {

		private Socket connection;

		public Loop(Socket connection) {
			this.connection = connection;
		}

		@Override
		public String call() {
			EventPool<?> emitter = generator.getEmitter();
			emitter.start();
				try (Writer w = new BufferedWriter(
						new OutputStreamWriter(connection.getOutputStream(), StandardCharsets.UTF_8))) {
					Event<?> event = null;
					while (!Thread.currentThread().isInterrupted()) {
						event = emitter.getNext();
						if (event != null) {
							w.write(event.toString());
							w.write('\n');
							w.flush();
							if(!doRun) break;
						}
						if (waitMillis > 0) {
							try {
								TimeUnit.MILLISECONDS.sleep(waitMillis);
							} catch (InterruptedException e) {
								Thread.currentThread().interrupt();
							}
						}
					}
					emitter.stop();
					while ((event = emitter.getNext()) != null) {
						w.write(event.toString());
						w.write('\n');
						w.flush();
					}
			} catch (IOException e) {
				e.printStackTrace();
			}
			return "finished";
		}

	}

}
