package com.nisovin.magicspells.castmodifiers.conditions;

import org.bukkit.Location;
import org.bukkit.entity.Creature;
import org.bukkit.entity.LivingEntity;

import org.jetbrains.annotations.NotNull;

import com.nisovin.magicspells.util.Name;
import com.nisovin.magicspells.castmodifiers.Condition;

@Name("hastarget")
public class HasTargetCondition extends Condition {

	@Override
	public boolean initialize(@NotNull String var) {
		return true;
	}

	@Override
	public boolean check(LivingEntity caster) {
		return hasTarget(caster);
	}

	@Override
	public boolean check(LivingEntity caster, LivingEntity target) {
		return hasTarget(target);
	}

	@Override
	public boolean check(LivingEntity caster, Location location) {
		return false;
	}

	private boolean hasTarget(LivingEntity target) {
		if (!(target instanceof Creature creature)) return false;
		LivingEntity t = creature.getTarget();
		return t != null && t.isValid();
	}

}
