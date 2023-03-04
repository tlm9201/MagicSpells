package com.nisovin.magicspells.castmodifiers.conditions;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.entity.LivingEntity;

import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.castmodifiers.Condition;

/**
 * Condition check to see if a player has more money than the target
 * 
 * @author TheComputerGeek2
 */
public class RicherThanCondition extends Condition {
	
	@Override
	public boolean initialize(String var) {
		return true;
	}

	@Override
	public boolean check(LivingEntity livingEntity) {
		return false;
	}

	@Override
	public boolean check(LivingEntity livingEntity, LivingEntity target) {
		return richer(livingEntity, target);
	}

	@Override
	public boolean check(LivingEntity livingEntity, Location location) {
		return false;
	}

	private boolean richer(LivingEntity livingEntity, LivingEntity target) {
		if (!(livingEntity instanceof Player c) || !(target instanceof Player t)) return false;
		return MagicSpells.getMoneyHandler().checkMoney(c) > MagicSpells.getMoneyHandler().checkMoney(t);
	}

}
