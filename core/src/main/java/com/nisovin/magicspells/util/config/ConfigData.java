package com.nisovin.magicspells.util.config;

import org.jetbrains.annotations.NotNull;

import com.nisovin.magicspells.util.SpellData;

public interface ConfigData<T> {

	T get(@NotNull SpellData data);

	default T get() {
		return get(SpellData.NULL);
	}

	default boolean isConstant() {
		return true;
	}

}
