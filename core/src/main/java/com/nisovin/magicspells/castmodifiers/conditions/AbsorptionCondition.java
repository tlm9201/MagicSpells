package com.nisovin.magicspells.castmodifiers.conditions;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;

import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.castmodifiers.Condition;

public class AbsorptionCondition extends Condition {

	private float health = 0;

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
			health = Float.parseFloat(var.substring(1));
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}
	
	@Override
	public boolean check(LivingEntity livingEntity) {
		return absorption(livingEntity);
	}
	
	@Override
	public boolean check(LivingEntity livingEntity, LivingEntity target) {
		return absorption(target);
	}
	
	@Override
	public boolean check(LivingEntity livingEntity, Location location) {
		return false;
	}

	private boolean absorption(LivingEntity target) {
		if (equals) return MagicSpells.getVolatileCodeHandler().getAbsorptionHearts(target) == health;
		else if (moreThan) return MagicSpells.getVolatileCodeHandler().getAbsorptionHearts(target) > health;
		else if (lessThan) return MagicSpells.getVolatileCodeHandler().getAbsorptionHearts(target) < health;
		return false;
	}
	
}
