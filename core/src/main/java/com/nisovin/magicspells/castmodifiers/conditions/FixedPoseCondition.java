package com.nisovin.magicspells.castmodifiers.conditions;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;

import org.jetbrains.annotations.NotNull;

import com.nisovin.magicspells.util.Name;
import com.nisovin.magicspells.castmodifiers.Condition;

@Name("fixedpose")
public class FixedPoseCondition extends Condition {

	@Override
	public boolean initialize(@NotNull String var) {
		return var.isEmpty();
	}

	@Override
	public boolean check(LivingEntity caster) {
		return caster.hasFixedPose();
	}

	@Override
	public boolean check(LivingEntity caster, LivingEntity target) {
		return target.hasFixedPose();
	}

	@Override
	public boolean check(LivingEntity caster, Location location) {
		return false;
	}

}
