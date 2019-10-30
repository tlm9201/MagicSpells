package com.nisovin.magicspells.castmodifiers.conditions;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;

import com.nisovin.magicspells.castmodifiers.Condition;

public class YawCondition extends Condition {
	
	private float yaw;

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
			yaw = Float.parseFloat(var.substring(1));
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	@Override
	public boolean check(LivingEntity livingEntity) {
		return yaw(livingEntity.getLocation());
	}

	@Override
	public boolean check(LivingEntity livingEntity, LivingEntity target) {
		return yaw(target.getLocation());
	}

	@Override
	public boolean check(LivingEntity livingEntity, Location location) {
		return yaw(location);
	}

	private boolean yaw(Location location) {
		if (equals) return location.getYaw() == yaw;
		else if (moreThan) return location.getYaw() > yaw;
		else if (lessThan) return location.getYaw() < yaw;
		return false;
	}

}
