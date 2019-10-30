package com.nisovin.magicspells.castmodifiers.conditions;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;

import com.nisovin.magicspells.util.Util;
import com.nisovin.magicspells.DebugHandler;
import com.nisovin.magicspells.castmodifiers.Condition;

public class HealthCondition extends Condition {

	private int health = 0;
	private boolean percent = false;

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

		String number = var.substring(1);

		try {
			if (number.endsWith("%")) {
				percent = true;
				number = number.replace("%", "");
			}
			health = Integer.parseInt(number);
			return true;
		} catch (NumberFormatException e) {
			DebugHandler.debugNumberFormat(e);
			return false;
		}
	}
	
	@Override
	public boolean check(LivingEntity livingEntity) {
		return health(livingEntity);
	}
	
	@Override
	public boolean check(LivingEntity livingEntity, LivingEntity target) {
		return health(target);
	}
	
	@Override
	public boolean check(LivingEntity livingEntity, Location location) {
		return false;
	}

	private boolean health(LivingEntity livingEntity) {
		double hp = livingEntity.getHealth();
		double percentHp = hp / Util.getMaxHealth(livingEntity) * 100;
		if (equals) {
			if (percent) return percentHp == health;
			return hp == health;
		} else if (moreThan) {
			if (percent) return percentHp > health;
			return hp > health;
		} else if (lessThan) {
			if (percent) return percentHp < health;
			return hp < health;
		}
		return false;
	}

}
