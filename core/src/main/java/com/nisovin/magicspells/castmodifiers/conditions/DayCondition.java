package com.nisovin.magicspells.castmodifiers.conditions;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;

import org.jetbrains.annotations.NotNull;

import com.nisovin.magicspells.castmodifiers.Condition;

public class DayCondition extends Condition {

	@Override
	public boolean initialize(@NotNull String var) {
		return true;
	}
	
	@Override
	public boolean check(LivingEntity caster) {
		return checkTime(caster.getLocation());
	}
	
	@Override
	public boolean check(LivingEntity caster, LivingEntity target) {
		return checkTime(target.getLocation());
	}
	
	@Override
	public boolean check(LivingEntity caster, Location location) {
		return checkTime(location);
	}

	private boolean checkTime(Location location) {
		return location.getWorld().isDayTime();
	}

}
