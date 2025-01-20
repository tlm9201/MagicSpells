package com.nisovin.magicspells.util.grammars;

import java.util.List;
import java.util.ArrayList;
import java.util.function.Predicate;

record XorPredicate<T>(List<Predicate<T>> predicates) implements Predicate<T> {

	public static <T> XorPredicate<T> xor(Predicate<T> left, Predicate<T> right) {
		if (left instanceof XorPredicate(var leftList) && right instanceof XorPredicate(var rightList)) {
			leftList.addAll(rightList);
			return (XorPredicate<T>) left;
		}

		if (left instanceof XorPredicate(var list)) {
			list.add(right);
			return (XorPredicate<T>) left;
		}

		if (right instanceof XorPredicate(var list)) {
			list.add(left);
			return (XorPredicate<T>) right;
		}

		List<Predicate<T>> list = new ArrayList<>();
		list.add(left);
		list.add(right);

		return new XorPredicate<>(list);
	}

	@Override
	public boolean test(T value) {
		boolean bool = predicates.getFirst().test(value);

		for (int i = 1; i < predicates.size(); i++)
			bool ^= predicates.get(i).test(value);

		return bool;
	}

}
