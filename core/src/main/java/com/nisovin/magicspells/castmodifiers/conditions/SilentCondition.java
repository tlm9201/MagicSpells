package com.nisovin.magicspells.castmodifiers.conditions;

import org.jetbrains.annotations.NotNull;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;

import com.nisovin.magicspells.castmodifiers.Condition;

public class SilentCondition extends Condition {

	@Override
	public boolean initialize(@NotNull String var) {
		return var.isEmpty();
	}

	@Override
	public boolean check(LivingEntity caster) {
		return caster.isSilent();
	}

	@Override
	public boolean check(LivingEntity caster, LivingEntity target) {
		return target.isSilent();
	}

	@Override
	public boolean check(LivingEntity caster, Location location) {
		return false;
	}

}
