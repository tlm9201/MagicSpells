package com.nisovin.magicspells.castmodifiers.conditions;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;

import org.jetbrains.annotations.NotNull;

import com.nisovin.magicspells.castmodifiers.Condition;

public class InWorldCondition extends Condition {

	private String world = "";

	@Override
	public boolean initialize(@NotNull String var) {
		if (var.isEmpty()) return false;
		world = var;
		return true;
	}
	
	@Override
	public boolean check(LivingEntity caster) {
		return checkWorld(caster.getLocation());
	}
	
	@Override
	public boolean check(LivingEntity caster, LivingEntity target) {
		return checkWorld(target.getLocation());
	}
	
	@Override
	public boolean check(LivingEntity caster, Location location) {
		return checkWorld(location);
	}

	private boolean checkWorld(Location location) {
		return location.getWorld().getName().equalsIgnoreCase(world);
	}

}
