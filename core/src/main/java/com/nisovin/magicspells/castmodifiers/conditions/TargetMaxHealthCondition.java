package com.nisovin.magicspells.castmodifiers.conditions;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;

import com.nisovin.magicspells.util.Util;
import com.nisovin.magicspells.castmodifiers.Condition;

public class TargetMaxHealthCondition extends Condition {

	private double health;

	private boolean equals;
	private boolean moreThan;
	private boolean lessThan;
	
	@Override
	public boolean setVar(String var) {
		if (var.length() < 2) {
			return false;
		}

		switch (var.charAt(0)) {
			case '=':
			case ':':
				equals = true;
				break;
			case '>':
				moreThan = true;
				break;
			case '<':
				lessThan = true;
				break;
		}

		try {
			health = Double.parseDouble(var.substring(1));
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	@Override
	public boolean check(LivingEntity livingEntity) {
		return false;
	}

	@Override
	public boolean check(LivingEntity livingEntity, LivingEntity target) {
		return maxHealth(target);
	}

	@Override
	public boolean check(LivingEntity livingEntity, Location location) {
		return false;
	}

	private boolean maxHealth(LivingEntity livingEntity) {
		if (equals) return Util.getMaxHealth(livingEntity) == health;
		else if (moreThan) return Util.getMaxHealth(livingEntity) > health;
		else if (lessThan) return Util.getMaxHealth(livingEntity) < health;
		return false;
	}

}
