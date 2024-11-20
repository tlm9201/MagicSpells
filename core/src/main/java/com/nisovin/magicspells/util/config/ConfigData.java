package com.nisovin.magicspells.util.config;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.nisovin.magicspells.util.SpellData;

public interface ConfigData<T> {

	T get(@NotNull SpellData data);

	default T get() {
		return get(SpellData.NULL);
	}

	default T getOr(@NotNull SpellData data, @Nullable T fallback) {
		T value = get(data);
		return value == null ? fallback : value;
	}

	default boolean isConstant() {
		return true;
	}

	default boolean isNull() {
		return isConstant() && get() == null;
	}

}
