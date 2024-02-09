package com.nisovin.magicspells.castmodifiers.conditions;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;

import org.jetbrains.annotations.NotNull;

import com.nisovin.magicspells.util.Name;
import com.nisovin.magicspells.castmodifiers.Condition;

@Name("outside")
public class OutsideCondition extends Condition {

	@Override
	public boolean initialize(@NotNull String var) {
		return true;
	}

	@Override
	public boolean check(LivingEntity caster) {
		return outside(caster, null);
	}

	@Override
	public boolean check(LivingEntity caster, LivingEntity target) {
		return outside(target, null);
	}
	
	@Override
	public boolean check(LivingEntity caster, Location location) {
		return outside(caster, location);
	}

	private boolean outside(LivingEntity target, Location location) {
		if (location != null) return location.getWorld().getHighestBlockYAt(location) <= location.getY();
		return target.getWorld().getHighestBlockYAt(target.getLocation()) <= target.getEyeLocation().getY();
	}

}
