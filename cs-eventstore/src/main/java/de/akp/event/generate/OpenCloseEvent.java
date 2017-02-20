package de.akp.event.generate;

import java.time.Instant;
import java.util.UUID;

import de.akp.event.generate.util.CharPool;

public class OpenCloseEvent extends BasicEvent {
	
	protected boolean come;

	protected OpenCloseEvent(String s, String message) {
		super(s, message);
		come = true;
	}
	
	public boolean isCome() {
		return come;
	}

	public void setCome(boolean come) {
		this.come = come;
	}
	
	public static class EventGenerator implements IEventGenerator<OpenCloseEvent> {
		
		private CharPool cp = new CharPool(35);

		@Override
		public OpenCloseEvent newEvent() {
			return new OpenCloseEvent(UUID.randomUUID().toString(), cp.getNext());
		}

		@Override
		public OpenCloseEvent newCorrelatedEvent(OpenCloseEvent event) {
			OpenCloseEvent result = new OpenCloseEvent(event.getId(), event.getMessage());
			event.setCome(false);
			event.setTimestamp(Instant.now().toEpochMilli());
			return result;
		}
		
		@Override
		public EventPool<OpenCloseEvent> getEmitter() {
			return new OpenCloseEmitter<OpenCloseEvent>(new EventGenerator());
		}
		
	}

}
