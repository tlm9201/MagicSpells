package com.nisovin.magicspells.spells.targeted.cleanse.util;

import org.bukkit.entity.LivingEntity;

import org.jetbrains.annotations.NotNull;

public interface Cleanser {

	boolean add(@NotNull String string);

	boolean isAnyActive(@NotNull LivingEntity entity);

	void cleanse(@NotNull LivingEntity entity);

}
