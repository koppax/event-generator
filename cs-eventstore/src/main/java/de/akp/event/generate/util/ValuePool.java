package de.akp.event.generate.util;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;


public interface ValuePool<T> {

	T getNext();

	public default List<T> sampleList(int n) {
		return sample(n).collect(Collectors.toList());
	}

	public default Stream<T> sample(int n) {
		return IntStream.range(0, n).mapToObj(i->getNext());
		
	}
	
}
