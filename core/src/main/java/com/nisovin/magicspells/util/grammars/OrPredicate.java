package com.nisovin.magicspells.util.grammars;

import java.util.List;
import java.util.ArrayList;
import java.util.function.Predicate;

record OrPredicate<T>(List<Predicate<T>> predicates) implements Predicate<T> {

	public static <T> OrPredicate<T> or(Predicate<T> left, Predicate<T> right) {
		if (left instanceof OrPredicate(var leftList) && right instanceof OrPredicate(var rightList)) {
			leftList.addAll(rightList);
			return (OrPredicate<T>) left;
		}

		if (left instanceof OrPredicate(var list)) {
			list.add(right);
			return (OrPredicate<T>) left;
		}

		if (right instanceof OrPredicate(var list)) {
			list.add(left);
			return (OrPredicate<T>) right;
		}

		List<Predicate<T>> list = new ArrayList<>();
		list.add(left);
		list.add(right);

		return new OrPredicate<>(list);
	}

	@Override
	public boolean test(T value) {
		for (Predicate<T> predicate : predicates)
			if (predicate.test(value))
				return true;

		return false;
	}

}
