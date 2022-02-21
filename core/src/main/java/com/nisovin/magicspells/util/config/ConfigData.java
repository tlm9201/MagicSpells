package com.nisovin.magicspells.util.config;

import org.bukkit.entity.LivingEntity;

import com.nisovin.magicspells.util.SpellData;

public interface ConfigData<T> {

	T get(LivingEntity caster, LivingEntity target, float power, String[] args);

	default T get(SpellData data) {
		if (data == null) return get(null, null, 1f, null);
		return get(data.caster(), data.target(), data.power(), data.args());
	}

	default boolean isConstant() {
		return true;
	}

	default boolean isTargeted() {
		return false;
	}

}
