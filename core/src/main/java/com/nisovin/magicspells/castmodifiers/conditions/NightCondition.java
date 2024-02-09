package com.nisovin.magicspells.castmodifiers.conditions;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;

import org.jetbrains.annotations.NotNull;

import com.nisovin.magicspells.util.Name;
import com.nisovin.magicspells.castmodifiers.Condition;

@Name("night")
public class NightCondition extends Condition {

	@Override
	public boolean initialize(@NotNull String var) {
		return true;
	}

	@Override
	public boolean check(LivingEntity caster) {
		return night(caster.getLocation());
	}
	
	@Override
	public boolean check(LivingEntity caster, LivingEntity target) {
		return night(target.getLocation());
	}
	
	@Override
	public boolean check(LivingEntity caster, Location location) {
		return night(location);
	}

	private boolean night(Location location) {
		return !location.getWorld().isDayTime();
	}
	
}
