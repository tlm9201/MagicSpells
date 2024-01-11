package com.nisovin.magicspells.spells.targeted.cleanse;

import org.bukkit.entity.LivingEntity;

import org.jetbrains.annotations.NotNull;

import com.nisovin.magicspells.spells.targeted.cleanse.util.Cleanser;

public class FreezeCleanser implements Cleanser {

	private static final String NAME = "freeze";

	@Override
	public boolean add(@NotNull String string) {
		return string.equalsIgnoreCase(NAME);
	}

	@Override
	public boolean isAnyActive(@NotNull LivingEntity entity) {
		return entity.getFreezeTicks() > 0;
	}

	@Override
	public void cleanse(@NotNull LivingEntity entity) {
		entity.setFreezeTicks(0);
	}

}
