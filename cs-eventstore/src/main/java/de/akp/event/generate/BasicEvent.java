package de.akp.event.generate;

import java.time.Instant;
import java.util.UUID;

import de.akp.event.generate.util.CharPool;

public class BasicEvent extends Event<String> {
	
	protected long timestamp;
	protected String message;
	
	protected BasicEvent(String id) {
		this.id = id;
	}
	
	protected BasicEvent(String id, String m) {
		this.id = id;
		this.message = m;
		timestamp = Instant.now().toEpochMilli();
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	public static class EventGenerator implements IEventGenerator<BasicEvent> {
		
		private CharPool cp = new CharPool(35);

		@Override
		public BasicEvent newEvent() {
			return new BasicEvent(UUID.randomUUID().toString(), cp.getNext());
		}

		@Override
		public BasicEvent newCorrelatedEvent(BasicEvent event) {
			// TODO Auto-generated method stub
			return null;
		}
		
		@Override
		public EventPool<BasicEvent> getEmitter() {
			return new SingleOpenGenerator<>(new EventGenerator());
		}
		
	}
		

}
