package net.centersight.eventstore.generate;

import net.centersight.eventstore.generate.util.Pool;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.glassfish.jersey.client.ClientConfig;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

@Service
public class EventGenerator {

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

	Queue<PanamaEventDto> queue = new ArrayBlockingQueue<>(100);


	private WebTarget service1;

	private WebTarget service2;

	public EventGenerator(RestTemplateBuilder restTemplateBuilder) {
		new FillQueue(queue);
		new ConsumeQueue(queue);

		ClientConfig config = new ClientConfig();
		Client client = ClientBuilder.newClient(config);
		service1 = client.target(Clients.EINS.uri);
		service2 = client.target(Clients.ZWEI.uri);

	}

	public void start() {
		FillQueue fillQueue = new FillQueue(queue);
		new Thread(fillQueue).start();
		try {
			TimeUnit.SECONDS.sleep(4);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		ConsumeQueue consumeQueue = new ConsumeQueue(queue);
		new Thread(consumeQueue).start();
	}

	public PanamaEventDto createNextNext() {
		String id = Integer.toString(random.nextInt());
		PanamaEventDto event = new PanamaEventDto(id, nodes.getNext(), gateways.getNext(), priority.getNext(), true, "test");
		history.put(id, event);
		return event;
	}

	private void send(PanamaEventDto event) {
		if (event.getParentId() % (long) 2 == 0) {
			sendToClient(event, service1);
		} else {
			sendToClient(event, service2);
		}
	}

	public String sendToClient(PanamaEventDto event, WebTarget service) {
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


	// private URI getBaseURI() {
	// return UriBuilder.fromUri("http://localhost:8090").build();
	// }


	private class FillQueue implements Runnable {

		private Queue<PanamaEventDto> q;

		public FillQueue(Queue<PanamaEventDto> q) {
			this.q = q;
		}

		@Override
		public void run() {
			try {
				while (true) {
					PanamaEventDto event = createNextNext();
					q.add(event);
					System.out.println(event.toString());
					send(event);
					TimeUnit.SECONDS.sleep(1);
				}
			} catch (InterruptedException e) {
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
				while ((list.size() < MAXLOAD)) {
					TimeUnit.MILLISECONDS.sleep(100);
					getAndSetNextEvent();
				}
				while (true) {
					PanamaEventDto event = getAndSetNextEvent();
					TimeUnit.SECONDS.sleep(1);
					PanamaEventDto e = list.remove(random.nextInt(list.size()));
					event.setIsCome(false);
					System.out.println(e.toString());
					send(e);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		private PanamaEventDto getAndSetNextEvent() {
			PanamaEventDto event = q.poll();
			list.add(event);
			return event;
		}

	}


}
