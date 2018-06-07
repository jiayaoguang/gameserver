package com.jyg.gameserver.queue;

import java.util.AbstractCollection;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.CountDownLatch;

/**
 * Hello world!
 *
 */
public class FixArrayQueue extends AbstractCollection<Object> implements Queue<Object> {

	private static final int MIN_INITIAL_CAPACITY = 128;
	
	

	private final Data[] elements;

	int headIndex = 0;
	int tailIndex = 0;
	int size = 0;

	public FixArrayQueue() {
		this(MIN_INITIAL_CAPACITY);
	}

	public FixArrayQueue(int numElements) {
		int realNumElements = calculateSize(numElements);
		elements = new Data[realNumElements];
		initElements();
	}

	private void initElements() {
		for (int i = 0; i < elements.length; i++) {
			elements[i] = new Data();
		}
	}

	private static int calculateSize(int numElements) {
		int initialCapacity = MIN_INITIAL_CAPACITY;
		if (numElements >= initialCapacity) {
			initialCapacity = numElements;
			initialCapacity |= (initialCapacity >>> 1);
			initialCapacity |= (initialCapacity >>> 2);
			initialCapacity |= (initialCapacity >>> 4);
			initialCapacity |= (initialCapacity >>> 8);
			initialCapacity |= (initialCapacity >>> 16);
			initialCapacity++;

			if (initialCapacity < 0) {
				initialCapacity >>>= 1;
			}
		}
		return initialCapacity;
	}

	@Override
	public boolean offer(Object content) {
		if (this.size() < elements.length) {
			return false;
		}
		if (content == null) {
			throw new NullPointerException();
		}
		elements[headIndex = (headIndex - 1) & (elements.length - 1)].init(content);
		return true;
	}

	@Override
	public Object poll() {
		if (headIndex == tailIndex) {
			return null;
		}
		Data data = elements[this.headIndex];
		Object content = data.getContent();
		headIndex = (headIndex + 1) & (elements.length - 1);
		return content;
	}
	
	public Object pollElement() {
		if (headIndex == tailIndex) {
			return null;
		}
		Data data = elements[this.headIndex];
		headIndex = (headIndex + 1) & (elements.length - 1);
		return data;
	}

	@Override
	public Object remove() {
		return poll();
	}

	@Override
	public Object element() {
		// TODO Auto-generated method stub
		return elements[headIndex];
	}

	@Override
	public Object peek() {
		return elements[headIndex].getContent();
	}
	
	public Data peekData() {
		return elements[headIndex];
	}

	@Override
	public Iterator<Object> iterator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int size() {
		return (tailIndex - headIndex) & (elements.length - 1);
	}

}
