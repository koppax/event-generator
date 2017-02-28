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

import de.akp.event.generate.EmitterFactory;
import de.akp.event.generate.EmitterFactoryBasic;
import de.akp.event.generate.EmitterFactoryBasic.BasicEvent;
import de.akp.event.generate.EmitterFactoryOpenClose;
import de.akp.event.generate.Event;
import de.akp.event.generate.IEventGenerator;

public class SimpleServer {

	private int port;
	private int waitMillis;
	
	private volatile boolean doRun = true;

	ExecutorService executor = Executors.newFixedThreadPool(10);
	private EmitterFactory factory;

//	private IEventGenerator<?> generator;
//	
//	private Event<?> event;

//	public SimpleServer(int port, IEventGenerator<?> eventGenerator) {
//		this(port, eventGenerator, 0);
//	}

//	public SimpleServer(int port, IEventGenerator<?> eventGenerator, int waitMillis) {
//		this.port = port;
//		generator = eventGenerator;
//		this.waitMillis = waitMillis;
//	}
	
	public SimpleServer(int port, EmitterFactory factory, int waitMillis) {
		this.port = port;
//		generator = eventGenerator;
		this.factory = factory;
		
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
		SimpleServer simpleServer = new SimpleServer(8082, new EmitterFactoryOpenClose(), 100);
		simpleServer.centralLoop();
		try {
			TimeUnit.SECONDS.sleep(25);
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
					System.out.println("Start...");
					try {
						Socket connection = server.accept();
						System.out.println("accepted");
						task = new Loop(connection, factory.createEmitter());
						executor.submit(task);
					} catch (IOException e) {
					}
				}
				executor.shutdown();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		
	}
	
	private class Loop implements Callable<String> {

		private Socket connection;
		private IEventGenerator<? extends Event<?>> iEventGenerator;

		public Loop(Socket connection, IEventGenerator<? extends Event<?>> iEventGenerator) {
			this.connection = connection;
			this.iEventGenerator = iEventGenerator;
		}

		@Override
		public String call() {
			iEventGenerator.start();
				try (Writer w = new BufferedWriter(
						new OutputStreamWriter(connection.getOutputStream(), StandardCharsets.UTF_8))) {
					Event<?> event = null;
					while (!Thread.currentThread().isInterrupted()) {
						event = iEventGenerator.nextEvent();
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
					iEventGenerator.stop();
					while ((event = iEventGenerator.nextEvent()) != null) {
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
