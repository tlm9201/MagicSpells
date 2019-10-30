package com.nisovin.magicspells.castmodifiers.conditions;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;

import com.nisovin.magicspells.castmodifiers.Condition;

public class PlayerCountCondition extends Condition {

	private int count;

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
			count = Integer.parseInt(var.substring(1));
			return true;
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public boolean check(LivingEntity livingEntity) {
		return playerCount();
	}

	@Override
	public boolean check(LivingEntity livingEntity, LivingEntity target) {
		return playerCount();
	}

	@Override
	public boolean check(LivingEntity livingEntity, Location location) {
		return playerCount();
	}

	private boolean playerCount() {
		if (equals) return Bukkit.getServer().getOnlinePlayers().size() == count;
		else if (moreThan) return Bukkit.getServer().getOnlinePlayers().size() > count;
		else if (lessThan) return Bukkit.getServer().getOnlinePlayers().size() < count;
		return false;
	}

}
