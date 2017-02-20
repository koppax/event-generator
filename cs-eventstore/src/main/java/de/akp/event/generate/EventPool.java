package de.akp.event.generate;

import de.akp.event.generate.util.ValuePool;

public interface EventPool<T extends Event<?>> extends ValuePool<T> {
	public void start();
	public void stop();
}
