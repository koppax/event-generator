package de.akp.event.generate;

import java.time.Instant;
import java.util.UUID;

import de.akp.event.generate.util.CharPool;

public class EmitterFactoryBasic implements EmitterFactory {
	
	public IEventGenerator<BasicEvent> createEmitter() {
		return new EventGenerator();
	}
	
	public static class BasicEvent extends Event<String> {
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

	}

	public static class EventGenerator implements IEventGenerator<BasicEvent> {
		
		private boolean active = true;

		private CharPool cp = new CharPool(35);

		@Override
		public BasicEvent nextEvent() {
			if(!active) {
				return null;
			}
			return new BasicEvent(UUID.randomUUID().toString(), "urururururururu");
		}

		@Override
		public BasicEvent newCorrelatedEvent(BasicEvent event) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void start() {
			active = true;
		}

		@Override
		public void stop() {
			active = false;
		}
	}

}
