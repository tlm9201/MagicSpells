package com.nisovin.magicspells.castmodifiers.conditions;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;

import com.nisovin.magicspells.DebugHandler;
import com.nisovin.magicspells.castmodifiers.Condition;

public class PitchCondition extends Condition {

	private float pitch;

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
			pitch = Float.parseFloat(var.substring(1));
			return true;
		} catch (NumberFormatException e) {
			DebugHandler.debugNumberFormat(e);
			return false;
		}
	}

	@Override
	public boolean check(LivingEntity livingEntity) {
		return pitch(livingEntity.getLocation());
	}

	@Override
	public boolean check(LivingEntity livingEntity, LivingEntity target) {
		return pitch(target.getLocation());
	}

	@Override
	public boolean check(LivingEntity livingEntity, Location location) {
		return pitch(location);
	}

	private boolean pitch(Location location) {
		if (equals) return location.getPitch() == pitch;
		else if (moreThan) return location.getPitch() > pitch;
		else if (lessThan) return location.getPitch() < pitch;
		return false;
	}

}
