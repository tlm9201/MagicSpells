package com.nisovin.magicspells.util.grammars;

import java.util.List;
import java.util.ArrayList;
import java.util.function.Predicate;

record AndPredicate<T>(List<Predicate<T>> predicates) implements Predicate<T> {

	public static <T> AndPredicate<T> and(Predicate<T> left, Predicate<T> right) {
		if (left instanceof AndPredicate(var leftList) && right instanceof AndPredicate(var rightList)) {
			leftList.addAll(rightList);
			return (AndPredicate<T>) left;
		}

		if (left instanceof AndPredicate(var list)) {
			list.add(right);
			return (AndPredicate<T>) left;
		}

		if (right instanceof AndPredicate(var list)) {
			list.add(left);
			return (AndPredicate<T>) right;
		}

		List<Predicate<T>> list = new ArrayList<>();
		list.add(left);
		list.add(right);

		return new AndPredicate<>(list);
	}

	@Override
	public boolean test(T value) {
		for (Predicate<T> predicate : predicates)
			if (!predicate.test(value))
				return false;

		return true;
	}

}
