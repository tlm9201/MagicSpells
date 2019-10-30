package com.nisovin.magicspells.castmodifiers.conditions;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.entity.LivingEntity;

import com.nisovin.magicspells.DebugHandler;
import com.nisovin.magicspells.castmodifiers.Condition;

public class LevelCondition extends Condition {

	private int level = 0;

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
			level = Integer.parseInt(var.substring(1));
			return true;
		} catch (NumberFormatException e) {
			DebugHandler.debugNumberFormat(e);
			return false;
		}
	}
	
	@Override
	public boolean check(LivingEntity livingEntity) {
		return level(livingEntity);
	}

	@Override
	public boolean check(LivingEntity livingEntity, LivingEntity target) {
		return level(target);
	}

	@Override
	public boolean check(LivingEntity livingEntity, Location location) {
		return false;
	}

	private boolean level(LivingEntity livingEntity) {
		if (!(livingEntity instanceof Player)) return false;
		if (equals) return ((Player) livingEntity).getLevel() == level;
		else if (moreThan) return ((Player) livingEntity).getLevel() > level;
		else if (lessThan) return ((Player) livingEntity).getLevel() < level;
		return false;
	}

}
