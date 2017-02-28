package de.akp.event.generate.old;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.Gauge;
import com.codahale.metrics.MetricRegistry;

import de.akp.event.generate.Event;
import de.akp.event.generate.EventPool;
import de.akp.event.generate.IEventGenerator;

public class SingleOpenGenerator<T extends Event<?>> implements EventPool<T> {
	
	protected List<T> openList = new ArrayList<>();
	protected Queue<T> sendQueue = new ConcurrentLinkedQueue<>();
	
	private EventProducer producer = new EventProducer();
	private Thread producerThread;
	
	private volatile boolean started = false;
	protected long waitTime = 200;
	protected IEventGenerator<T> generator;
	
	static final MetricRegistry metrics = new MetricRegistry();
	
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
		metrics.register(MetricRegistry.name(SingleOpenGenerator.class, "send"), new Gauge<Integer>() {
			
			@Override
			public Integer getValue() {
				return sendQueue.size();
			}
			
		});
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
//		toBeSent = generator.newEvent();
		openList.add(toBeSent);
		return toBeSent;
	}
	
	public static void startReport() {
	      ConsoleReporter reporter = ConsoleReporter.forRegistry(metrics)
	          .convertRatesTo(TimeUnit.SECONDS)
	          .convertDurationsTo(TimeUnit.MILLISECONDS)
	          .build();
	      reporter.start(1, TimeUnit.SECONDS);
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
//			openList.add(generator.newEvent());
		}
		
		
	}

}
