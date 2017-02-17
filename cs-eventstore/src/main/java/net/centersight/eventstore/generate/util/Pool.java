package net.centersight.eventstore.generate.util;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Pool<T> implements ValuePool<T> {

	private List<T> pool;

	private int len;

	private Random random;

	public Pool(List<T> values) {
		pool = Collections.unmodifiableList(values);
		len = pool.size();
		random = new Random();
	}

	@Override
	public T getNext() {
		return pool.get(random.nextInt(len));
	}

	@Override
	public List<T> sampleList(int n) {
		return sample(n).collect(Collectors.toList());
	}

	@Override
	public Stream<T> sample(int n) {
		return IntStream.range(0, n)
				.mapToObj(i -> getNext());
	}

}
