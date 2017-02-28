package de.akp.event.generate;

public interface EmitterFactory {
	public IEventGenerator<? extends Event<?>> createEmitter();
}
