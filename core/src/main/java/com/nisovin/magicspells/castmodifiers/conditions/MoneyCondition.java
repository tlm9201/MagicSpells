package com.nisovin.magicspells.castmodifiers.conditions;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.entity.LivingEntity;

import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.castmodifiers.Condition;

public class MoneyCondition extends Condition {

	private float money;

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
			money = Float.parseFloat(var.substring(1));
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	@Override
	public boolean check(LivingEntity livingEntity) {
		return money(livingEntity);
	}

	@Override
	public boolean check(LivingEntity livingEntity, LivingEntity target) {
		return money(target);
	}

	@Override
	public boolean check(LivingEntity livingEntity, Location location) {
		return false;
	}

	private boolean money(LivingEntity livingEntity) {
		if (!(livingEntity instanceof Player)) return false;
		if (equals) return MagicSpells.getMoneyHandler().checkMoney((Player) livingEntity) == money;
		else if (moreThan) return MagicSpells.getMoneyHandler().checkMoney((Player) livingEntity) > money;
		else if (lessThan) return MagicSpells.getMoneyHandler().checkMoney((Player) livingEntity) < money;
		return false;
	}

}
