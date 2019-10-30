package com.nisovin.magicspells.castmodifiers.conditions;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;

import com.nisovin.magicspells.DebugHandler;
import com.nisovin.magicspells.castmodifiers.Condition;

public class DistanceCondition extends Condition {

	private double distanceSq;

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
			distanceSq = Double.parseDouble(var.substring(1));
			distanceSq = distanceSq * distanceSq;
			return true;
		} catch (NumberFormatException e) {
			DebugHandler.debugNumberFormat(e);
			return false;
		}
	}

	@Override
	public boolean check(LivingEntity livingEntity) {
		return false;
	}

	@Override
	public boolean check(LivingEntity livingEntity, LivingEntity target) {
		return distance(livingEntity.getLocation(), target.getLocation());
	}
	
	@Override
	public boolean check(LivingEntity livingEntity, Location location) {
		return distance(livingEntity.getLocation(), location);
	}

	private boolean distance(Location from, Location to) {
		if (equals) return from.distanceSquared(to) == distanceSq;
		else if (moreThan) return from.distanceSquared(to) > distanceSq;
		else if (lessThan) return from.distanceSquared(to) < distanceSq;
		return false;
	}

}
