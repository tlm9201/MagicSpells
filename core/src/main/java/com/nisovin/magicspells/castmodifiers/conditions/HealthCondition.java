package com.nisovin.magicspells.castmodifiers.conditions;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;

import org.jetbrains.annotations.NotNull;

import com.nisovin.magicspells.util.Util;
import com.nisovin.magicspells.handlers.DebugHandler;
import com.nisovin.magicspells.castmodifiers.conditions.util.OperatorCondition;

public class HealthCondition extends OperatorCondition {

	private int health = 0;
	private boolean percent = false;

	@Override
	public boolean initialize(@NotNull String var) {
		if (var.length() < 2 || !super.initialize(var)) return false;

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
	public boolean check(LivingEntity caster) {
		return health(caster);
	}
	
	@Override
	public boolean check(LivingEntity caster, LivingEntity target) {
		return health(target);
	}
	
	@Override
	public boolean check(LivingEntity caster, Location location) {
		return false;
	}

	private boolean health(LivingEntity target) {
		double hp = target.getHealth();
		double percentHp = hp / Util.getMaxHealth(target) * 100D;
		return compare(percent ? percentHp : hp, health);
	}

}
