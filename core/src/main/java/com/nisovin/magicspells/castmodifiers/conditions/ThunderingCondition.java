package com.nisovin.magicspells.castmodifiers.conditions;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;

import org.jetbrains.annotations.NotNull;

import com.nisovin.magicspells.util.Name;
import com.nisovin.magicspells.castmodifiers.Condition;

@Name("thundering")
public class ThunderingCondition extends Condition {

	@Override
	public boolean initialize(@NotNull String var) {
		return true;
	}

	@Override
	public boolean check(LivingEntity caster) {
		return thundering(caster.getLocation());
	}
	
	@Override
	public boolean check(LivingEntity caster, LivingEntity target) {
		return thundering(target.getLocation());
	}
	
	@Override
	public boolean check(LivingEntity caster, Location location) {
		return thundering(location);
	}

	private boolean thundering(Location location) {
		return location.getWorld().isThundering();
	}

}
