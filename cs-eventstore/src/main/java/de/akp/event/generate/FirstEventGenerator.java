package de.akp.event.generate;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.glassfish.jersey.client.ClientConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.Meter;
import com.codahale.metrics.Metric;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.annotation.Timed;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yammer.metrics.core.MetricsRegistry;

import de.akp.event.generate.util.Pool;

@Service
public class FirstEventGenerator {

	public enum Clients {
		EINS(UriBuilder.fromUri("http://localhost:8090").build()),
		ZWEI(UriBuilder.fromUri("http://localhost:8090").build());


		private final URI uri;

		private Clients(URI uri) {
			this.uri = uri;
		}

	}

	private static final String PATH = "events";

	private Random random = new Random();

	private Pool<Long> priority = new Pool<Long>(Arrays.asList(1L, 2L, 3L));

	private Pool<Long> nodes = new Pool<Long>(Arrays.asList(101L, 102L, 103L));

	private Pool<Long> gateways = new Pool<Long>(Arrays.asList(201L, 202L, 303L));

	private Map<String, PanamaEventDto> history = new HashMap<>();

	Queue<PanamaEventDto> createQueue = new ConcurrentLinkedQueue<>();
	Queue<PanamaEventDto> sendQueue = new ConcurrentLinkedQueue<>();


	private WebTarget service1;

	private WebTarget service2;

	private Thread filler;

	private Thread consumer;
	
	@Autowired
	MetricRegistry metrics;
	

	public FirstEventGenerator() {
		new FillQueue(createQueue);
		new ConsumeQueue(createQueue);

		ClientConfig config = new ClientConfig();
		Client client = ClientBuilder.newClient(config);
		service1 = client.target(Clients.EINS.uri);
		service2 = client.target(Clients.ZWEI.uri);
		

	}

	
	public void start() {
		metrics.register(MetricRegistry.name(FirstEventGenerator.class, "send"), new Gauge<Integer>() {
			
			@Override
			public Integer getValue() {
				return sendQueue.size();
			}
			
		});
		FillQueue fillQueue = new FillQueue(createQueue);
		filler = new Thread(fillQueue);
		filler.start();
		try {
			TimeUnit.SECONDS.sleep(4);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		ConsumeQueue consumeQueue = new ConsumeQueue(createQueue);
		consumer = new Thread(consumeQueue);
		consumer.start();
	}

	public void stop() {
		if (filler != null)
			filler.interrupt();
		if (consumer != null)
			consumer.interrupt();
	}
	
//	public PanamaEventDto getNext() {
//		metrics.register(MetricRegistry.name(FirstEventGenerator.class, "versuch"), new Gauge<Integer>() {
//
//			@Override
//			public Integer getValue() {
//				return sendQueue.size();
//			}
//			
//		});
//		PanamaEventDto e = sendQueue.poll();
//		return e;
//	}

	private PanamaEventDto createNextNext() {
		String id = Integer.toString(random.nextInt());
		PanamaEventDto event = new PanamaEventDto(id, nodes.getNext(), gateways.getNext(), priority.getNext(), true, "test");
		history.put(id, event);
		return event;
	}

	@Timed
	private void send(PanamaEventDto event) {
		sendQueue.offer(event);
	}

	private String sendToClient(PanamaEventDto event, WebTarget service) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			String jsonInString = mapper.writeValueAsString(event);

			service.path(PATH).request()
					.post(Entity.entity(jsonInString, MediaType.APPLICATION_JSON), Response.class);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return "asdf";

	}

	private class FillQueue implements Runnable {

		private Queue<PanamaEventDto> q;

		public FillQueue(Queue<PanamaEventDto> q) {
			this.q = q;
		}

		@Override
		public void run() {
			try {
				while (!Thread.currentThread().isInterrupted()) {
					PanamaEventDto event = createNextNext();
					q.add(event);
					System.out.println(event.toString());
					send(event);
					TimeUnit.SECONDS.sleep(1);
				}
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				e.printStackTrace();
			}
		}


	}

	private class ConsumeQueue implements Runnable {

		private static final int MAXLOAD = 3;

		private Queue<PanamaEventDto> q;

		private List<PanamaEventDto> list = new ArrayList<>();

		public ConsumeQueue(Queue<PanamaEventDto> q) {
			this.q = q;
		}

		@Override
		public void run() {
			try {
				while (!Thread.currentThread().isInterrupted() && (list.size() < MAXLOAD)) {
					TimeUnit.MILLISECONDS.sleep(100);
					getAndSetNextEvent();
				}
				while (!Thread.currentThread().isInterrupted() || !list.isEmpty()) {
					PanamaEventDto event = getAndSetNextEvent();
					TimeUnit.MILLISECONDS.sleep(800);
					PanamaEventDto e = list.remove(random.nextInt(list.size()));
					if (e != null) {
						e.setIsCome(false);
						System.out.println(e.toString());
						send(e);
					}
				}
			} catch (InterruptedException e) {
				System.out.println("Remained: " + list.size());
				Thread.currentThread().interrupt();
				e.printStackTrace();
			}
		}

		private PanamaEventDto getAndSetNextEvent() {
			PanamaEventDto event = q.poll();
			if (event != null)
				list.add(event);
			return event;
		}

	}


}
