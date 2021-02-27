package com.nisovin.magicspells.castmodifiers.conditions;

import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;

import com.nisovin.magicspells.handlers.DebugHandler;
import com.nisovin.magicspells.castmodifiers.Condition;

public class ChanceCondition extends Condition {

	private double chance;

	@Override
	public boolean initialize(String var) {
		try {
			chance = Double.parseDouble(var) / 100;
			return chance >= 0 && chance <= 1;
		} catch (NumberFormatException e) {
			DebugHandler.debugNumberFormat(e);
			return false;
		}
	}

	@Override
	public boolean check(LivingEntity livingEntity) {
		return chance != 0 && (chance == 1 || ThreadLocalRandom.current().nextDouble() < chance);
	}

	@Override
	public boolean check(LivingEntity livingEntity, LivingEntity target) {
		return check(target);
	}
	
	@Override
	public boolean check(LivingEntity livingEntity, Location location) {
		return check(livingEntity);
	}

}
