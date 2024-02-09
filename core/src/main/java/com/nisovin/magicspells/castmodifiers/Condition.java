package com.nisovin.magicspells.castmodifiers;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;

import org.jetbrains.annotations.NotNull;

import com.nisovin.magicspells.util.Name;

/**
 * Annotate this class with {@link Name} which will hold the configuration name of the condition.
 */
public abstract class Condition {

	public abstract boolean initialize(@NotNull String var);

	public abstract boolean check(LivingEntity livingEntity);

	public abstract boolean check(LivingEntity livingEntity, LivingEntity target);

	public abstract boolean check(LivingEntity livingEntity, Location location);

}
