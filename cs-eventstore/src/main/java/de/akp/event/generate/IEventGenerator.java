package de.akp.event.generate;

public interface IEventGenerator<T extends Event<?>> {
	
	public T newEvent();
	
	public T newCorrelatedEvent(T event);
	
	public EventPool<T> getEmitter();
	

}
