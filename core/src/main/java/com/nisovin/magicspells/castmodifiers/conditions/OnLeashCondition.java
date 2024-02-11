package com.nisovin.magicspells.castmodifiers.conditions;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;

import org.jetbrains.annotations.NotNull;

import com.nisovin.magicspells.util.Name;
import com.nisovin.magicspells.castmodifiers.Condition;

@Name("onleash")
public class OnLeashCondition extends Condition {

	@Override
	public boolean initialize(@NotNull String var) {
		return true;
	}

	@Override
	public boolean check(LivingEntity caster) {
		return isLeashed(caster);
	}

	@Override
	public boolean check(LivingEntity caster, LivingEntity target) {
		return isLeashed(target);
	}

	@Override
	public boolean check(LivingEntity caster, Location location) {
		return false;
	}

	private boolean isLeashed(LivingEntity target) {
		return target.isLeashed();
	}

}
