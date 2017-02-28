package de.akp.event.generate;

public interface IEventGenerator<T extends Event<?>> {
	
	public T nextEvent();
	
	public T newCorrelatedEvent(T event);
	
	public void start();
	
	public void stop();

}
