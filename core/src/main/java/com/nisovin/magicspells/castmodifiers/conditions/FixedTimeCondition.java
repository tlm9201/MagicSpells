package com.nisovin.magicspells.castmodifiers.conditions;

import org.jetbrains.annotations.NotNull;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;

import com.nisovin.magicspells.util.Name;
import com.nisovin.magicspells.castmodifiers.Condition;

@Name("fixedtime")
public class FixedTimeCondition extends Condition {

	@Override
	public boolean initialize(@NotNull String var) {
		return true;
	}

	@Override
	public boolean check(LivingEntity caster) {
		return caster.getWorld().isFixedTime();
	}

	@Override
	public boolean check(LivingEntity caster, LivingEntity target) {
		return target.getWorld().isFixedTime();
	}

	@Override
	public boolean check(LivingEntity caster, Location location) {
		return location.getWorld().isFixedTime();
	}

}
