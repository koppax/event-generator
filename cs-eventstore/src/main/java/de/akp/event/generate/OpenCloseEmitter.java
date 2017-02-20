package de.akp.event.generate;

import java.util.Random;

public class OpenCloseEmitter<T extends OpenCloseEvent> extends SingleOpenGenerator<T> {
	
	
	private Random random = new Random();
	private boolean firstCall = true;

	public OpenCloseEmitter(IEventGenerator<T> generator) {
		super(generator);
		this.waitTime = 300;
	}
	
	@Override
	public void stop() {
		super.stop();
		while(!openList.isEmpty()) {
			T event = (T) openList.remove(0);
			event.setCome(false);
			sendQueue.offer((T) event);
		}
	}
	
	@Override
	protected T toBeSent() {
		T toBeSent = null;
		boolean open = random.nextBoolean();
		if(firstCall) {
			open = true;
			firstCall = false;
		}
		if(open) {
			toBeSent = generator.newEvent();
			openList.add(toBeSent);
		} else if(openList.size() > 0) {
			toBeSent = openList.remove(random.nextInt(openList.size()));
			toBeSent.setCome(false);
		}
		return toBeSent;
	}
	

}
