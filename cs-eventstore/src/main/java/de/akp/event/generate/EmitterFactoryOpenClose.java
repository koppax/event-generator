package de.akp.event.generate;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import de.akp.event.generate.util.CharPool;

public class EmitterFactoryOpenClose implements EmitterFactory {
	
	public IEventGenerator<OpenCloseEvent> createEmitter() {
		return new EventGenerator();
	}
	
	public static class OpenCloseEvent extends EmitterFactoryBasic.BasicEvent {
		protected long timestamp;
		protected String message;
		
		protected boolean come;

		public boolean isCome() {
			return come;
		}

		public void setCome(boolean come) {
			this.come = come;
		}

		protected OpenCloseEvent(String id, String m) {
			super(id, m);
			come = true;
		}

	}

	public static class EventGenerator implements IEventGenerator<OpenCloseEvent> {
		
		private boolean active = true;
		private boolean firstCall = true;
		private Random random = new Random();

		private CharPool cp = new CharPool(35);
		
		private List<OpenCloseEvent> history = new ArrayList<>();

		@Override
		public OpenCloseEvent nextEvent() {
			if(!active) {
				return null;
			}
			if(firstCall) {
				firstCall = false;
				return history.get(0);
			}
			boolean open = random.nextBoolean();
			OpenCloseEvent toBeSent = null;
			if(open) {
				toBeSent = new OpenCloseEvent(UUID.randomUUID().toString(), cp.getNext());
				history.add(toBeSent);
			} else if(history.size() > 0) {
				toBeSent = history.remove(random.nextInt(history.size()));
				toBeSent.setCome(false);
			}
			return toBeSent;
		}

		@Override
		public OpenCloseEvent newCorrelatedEvent(OpenCloseEvent event) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void start() {
			history.add(new OpenCloseEvent(UUID.randomUUID().toString(), cp.getNext()));
			active = true;
		}

		@Override
		public void stop() {
			active = false;
		}

		@Override
		public Event<?> getNext() {
			return nextEvent();
		}
	}
}
