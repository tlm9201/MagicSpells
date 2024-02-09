package com.nisovin.magicspells.castmodifiers.conditions;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;

import org.jetbrains.annotations.NotNull;

import com.nisovin.magicspells.util.Name;
import com.nisovin.magicspells.castmodifiers.Condition;

@Name("raining")
public class RainingCondition extends Condition {

	@Override
	public boolean initialize(@NotNull String var) {
		return true;
	}

	@Override
	public boolean check(LivingEntity caster) {
		return isRaining(caster.getLocation());
	}
	
	@Override
	public boolean check(LivingEntity caster, LivingEntity target) {
		return isRaining(target.getLocation());
	}
	
	@Override
	public boolean check(LivingEntity caster, Location location) {
		return isRaining(location);
	}

	private boolean isRaining(Location location) {
		return location.getWorld().hasStorm();
	}

}
