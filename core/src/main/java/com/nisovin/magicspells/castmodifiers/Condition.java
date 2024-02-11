package com.nisovin.magicspells.castmodifiers;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;

import org.jetbrains.annotations.NotNull;

import com.nisovin.magicspells.util.Name;
import com.nisovin.magicspells.util.DependsOn;

/**
 * Annotations:
 * <ul>
 *     <li>{@link Name} (required): Holds the configuration name of the condition.</li>
 *     <li>{@link DependsOn} (optional): Requires listed plugins to be enabled before this condition is created.</li>
 * </ul>
 */
public abstract class Condition {

	public abstract boolean initialize(@NotNull String var);

	public abstract boolean check(LivingEntity livingEntity);

	public abstract boolean check(LivingEntity livingEntity, LivingEntity target);

	public abstract boolean check(LivingEntity livingEntity, Location location);

}
