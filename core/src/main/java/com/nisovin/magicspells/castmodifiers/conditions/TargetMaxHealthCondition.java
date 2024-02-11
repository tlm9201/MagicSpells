package com.nisovin.magicspells.castmodifiers.conditions;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;

import org.jetbrains.annotations.NotNull;

import com.nisovin.magicspells.util.Name;
import com.nisovin.magicspells.util.Util;
import com.nisovin.magicspells.castmodifiers.conditions.util.OperatorCondition;

@Name("targetmaxhealth")
public class TargetMaxHealthCondition extends OperatorCondition {

	private double health;
	
	@Override
	public boolean initialize(@NotNull String var) {
		if (var.length() < 2 || !super.initialize(var)) return false;

		try {
			health = Double.parseDouble(var.substring(1));
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	@Override
	public boolean check(LivingEntity caster) {
		return maxHealth(caster);
	}

	@Override
	public boolean check(LivingEntity caster, LivingEntity target) {
		return maxHealth(target);
	}

	@Override
	public boolean check(LivingEntity caster, Location location) {
		return false;
	}

	private boolean maxHealth(LivingEntity target) {
		return compare(Util.getMaxHealth(target), health);
	}

}
