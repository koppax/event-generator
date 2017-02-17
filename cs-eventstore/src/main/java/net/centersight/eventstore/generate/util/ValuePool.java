package net.centersight.eventstore.generate.util;

import java.util.List;
import java.util.stream.Stream;


public interface ValuePool<T> {

	T getNext();

	List<T> sampleList(int n);

	Stream<T> sample(int n);
	
}
