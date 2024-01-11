package com.nisovin.magicspells.util;

import java.util.HashMap;

public class IntMap<T> extends HashMap<T, Integer> {

	/**
	 * Same as {@link HashMap#getOrDefault(Object, Object)} with a default value of 0.
	 * @return the value to which the specified key is mapped, or 0 if this map contains no mapping for the key
	 */
	@Override
	public Integer get(Object key) {
		return super.getOrDefault(key, 0);
	}

	/**
	 * @return the previous value associated with {@code key}, or {@code 0} if there was no mapping for {@code key}.
	 */
	@Override
	public Integer put(T key, Integer value) {
		Integer prev = super.put(key, value);
		return prev == null ? 0 : prev;
	}

	/**
	 * @return the previous value associated with {@code key}, or {@code 0} if there was no mapping for {@code key}.
	 */
	@Override
	public Integer remove(Object key) {
		Integer prev = super.remove(key);
		return prev == null ? 0 : prev;
	}

	public int increment(T key) {
		return increment(key, 1);
	}

	public int increment(T key, int amount) {
		return compute(key, (k, v) -> (v == null ? 0 : v) + amount);
	}

	public int decrement(T key) {
		return decrement(key, 1);
	}

	public int decrement(T key, int amount) {
		return compute(key, (k, v) -> (v == null ? 0 : v) - amount);
	}

}
