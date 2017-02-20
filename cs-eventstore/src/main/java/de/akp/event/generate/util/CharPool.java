package de.akp.event.generate.util;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class CharPool extends Pool<String> {
	
	private static final String pool = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZÄÜÖabcdefghijklmnopqrstuvwxyzäöüß";
	private Random random = new Random();
	private int max;

	public CharPool(int max) {
		super(Collections.emptyList());
		this.max = max;
		
	}
	

	@Override
	public String getNext() {
		int length = random.nextInt(max);
		if(length == 0) {
			length = 1;
		}
		StringBuilder result = new StringBuilder(length);
		IntStream.range(0, length).forEach(i->result.append(pool.charAt(random.nextInt(pool.length() - 1))));
		return result.toString();
	}

	@Override
	public List<String> sampleList(int n) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Stream<String> sample(int n) {
		// TODO Auto-generated method stub
		return null;
	}

}
