package de.akp.event.generate;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

public class SingleOpenGenerator<T extends Event<?>> implements EventPool<T> {
	
	protected List<T> openList = new ArrayList<>();
	protected Queue<T> sendQueue = new ConcurrentLinkedQueue<>();
	
	private EventProducer producer = new EventProducer();
	private Thread producerThread;
	
	private volatile boolean started = false;
	protected long waitTime = 200;
	protected IEventGenerator<T> generator;
	
	public SingleOpenGenerator(IEventGenerator<T> generator) {
		this.generator = generator;
	}
	

	@Override
	public T getNext() {
		if(! started) {
			throw new RuntimeException("Generator not started yet!");
		}
		return sendQueue.poll();
	}

	@Override
	public void start() {
		producerThread = new Thread(producer);
		producerThread.start();
		started = true;
	}

	@Override
	public void stop() {
		producerThread.interrupt();
	}
	
	protected T toBeSent() {
		T toBeSent = null;
		toBeSent = generator.newEvent();
		openList.add(toBeSent);
		return toBeSent;
	}
	
	
	private class EventProducer implements Runnable {
		
		@Override
		public void run() {
			init();
			while(!Thread.currentThread().isInterrupted()) {
				T beSent = toBeSent();
				if(beSent != null) {
					sendQueue.offer(beSent);
				}
				waitSomeTime();
			}
		}

		private void waitSomeTime() {
			if(waitTime == 0) {
				return;
			}
			try {
				TimeUnit.MILLISECONDS.sleep(waitTime);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
			
		}

		private void init() {
			openList.add(generator.newEvent());
		}
		
		
	}

}
