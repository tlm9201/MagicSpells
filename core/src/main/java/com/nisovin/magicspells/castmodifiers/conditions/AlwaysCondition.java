package com.nisovin.magicspells.castmodifiers.conditions;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;

import org.jetbrains.annotations.NotNull;

import com.nisovin.magicspells.util.Name;
import com.nisovin.magicspells.castmodifiers.Condition;

@Name("always")
public class AlwaysCondition extends Condition {

	@Override
	public boolean initialize(@NotNull String var) {
		return true;
	}

	@Override
	public boolean check(LivingEntity caster) {
		return true;
	}

	@Override
	public boolean check(LivingEntity caster, LivingEntity target) {
		return true;
	}

	@Override
	public boolean check(LivingEntity caster, Location location) {
		return true;
	}

}
