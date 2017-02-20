package de.akp.event.generate;

import java.lang.reflect.Field;
import java.util.stream.Stream;

public abstract class Event<T> {
	
	protected T id;
	
	public T getId() {
		return id;
	}
	
	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append("[id=").append(id);
		Field[] fields = this.getClass().getDeclaredFields();
		Stream.of(fields)
			.forEach(f->{try {
				result.append(", ").append(f.getName()).append("=").append(f.get(this));
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}});
		result.append(']');
		return result.toString();
	}
	
}
