package com.nisovin.magicspells.castmodifiers.conditions.util;

import org.jetbrains.annotations.NotNull;

import com.nisovin.magicspells.castmodifiers.Condition;

public abstract class OperatorCondition extends Condition {

	public boolean equals;
	public boolean moreThan;
	public boolean lessThan;

	@Override
	public boolean initialize(@NotNull String var) {
		switch (var.charAt(0)) {
			case '=', ':' -> equals = true;
			case '>' -> moreThan = true;
			case '<' -> lessThan = true;
			default -> {
				return false;
			}
		}
		return true;
	}

	protected boolean compare(double a, double b) {
		if (equals) return a == b;
		else if (moreThan) return a > b;
		else if (lessThan) return a < b;
		return false;
	}

	protected boolean compare(long a, long b) {
		if (equals) return a == b;
		else if (moreThan) return a > b;
		else if (lessThan) return a < b;
		return false;
	}

}
