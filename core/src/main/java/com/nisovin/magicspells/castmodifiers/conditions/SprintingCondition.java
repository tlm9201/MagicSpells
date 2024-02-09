package com.nisovin.magicspells.castmodifiers.conditions;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.entity.LivingEntity;

import org.jetbrains.annotations.NotNull;

import com.nisovin.magicspells.util.Name;
import com.nisovin.magicspells.castmodifiers.Condition;

@Name("sprinting")
public class SprintingCondition extends Condition {
	
	@Override
	public boolean initialize(@NotNull String var) {
		return true;
	}
	
	@Override
	public boolean check(LivingEntity caster) {
		return isSprinting(caster);
	}
	
	@Override
	public boolean check(LivingEntity caster, LivingEntity target) {
		return isSprinting(target);
	}
	
	@Override
	public boolean check(LivingEntity caster, Location location) {
		return false;
	}

	private boolean isSprinting(LivingEntity target) {
		if (!(target instanceof Player pl)) return false;
		return pl.isSprinting();
	}
	
}
