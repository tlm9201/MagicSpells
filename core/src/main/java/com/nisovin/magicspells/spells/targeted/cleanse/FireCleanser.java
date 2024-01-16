package com.nisovin.magicspells.spells.targeted.cleanse;

import org.bukkit.entity.LivingEntity;

import org.jetbrains.annotations.NotNull;

import com.nisovin.magicspells.spells.targeted.cleanse.util.Cleanser;

public class FireCleanser implements Cleanser {

	private static final String NAME = "fire";

	@Override
	public boolean add(@NotNull String string) {
		return string.equalsIgnoreCase(NAME);
	}

	@Override
	public boolean isAnyActive(@NotNull LivingEntity entity) {
		return entity.getFireTicks() > 0;
	}

	@Override
	public void cleanse(@NotNull LivingEntity entity) {
		entity.setFireTicks(0);
	}

}
