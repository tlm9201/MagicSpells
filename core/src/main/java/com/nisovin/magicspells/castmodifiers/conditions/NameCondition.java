package com.nisovin.magicspells.castmodifiers.conditions;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;

import com.nisovin.magicspells.util.Util;
import com.nisovin.magicspells.castmodifiers.Condition;

public class NameCondition extends Condition {

	private String name;
	
	@Override
	public boolean initialize(String var) {
		if (var == null || var.isEmpty()) return false;
		name = Util.getPlainString(Util.getMiniMessage(var));
		return true;
	}

	@Override
	public boolean check(LivingEntity livingEntity) {
		return checkName(livingEntity);
	}

	@Override
	public boolean check(LivingEntity livingEntity, LivingEntity target) {
		return checkName(target);
	}

	@Override
	public boolean check(LivingEntity livingEntity, Location location) {
		return false;
	}

	private boolean checkName(LivingEntity livingEntity) {
		return Util.getPlainString(livingEntity.name()).equals(name);
	}

}
