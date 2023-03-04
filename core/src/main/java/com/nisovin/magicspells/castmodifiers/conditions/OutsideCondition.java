package com.nisovin.magicspells.castmodifiers.conditions;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;

import com.nisovin.magicspells.castmodifiers.Condition;

public class OutsideCondition extends Condition {

	@Override
	public boolean initialize(String var) {
		return true;
	}

	@Override
	public boolean check(LivingEntity livingEntity) {
		return outside(livingEntity, null);
	}

	@Override
	public boolean check(LivingEntity livingEntity, LivingEntity target) {
		return outside(livingEntity, null);
	}
	
	@Override
	public boolean check(LivingEntity livingEntity, Location location) {
		return outside(livingEntity, location);
	}

	private boolean outside(LivingEntity livingEntity, Location location) {
		if (location != null) return location.getWorld().getHighestBlockYAt(location) <= location.getY();
		return livingEntity.getWorld().getHighestBlockYAt(livingEntity.getLocation()) <= livingEntity.getEyeLocation().getY();
	}

}
