package com.nisovin.magicspells.castmodifiers.conditions;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.entity.LivingEntity;

import org.jetbrains.annotations.NotNull;

import com.nisovin.magicspells.util.Name;
import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.castmodifiers.conditions.util.OperatorCondition;

@Name("money")
public class MoneyCondition extends OperatorCondition {

	private float money;
	
	@Override
	public boolean initialize(@NotNull String var) {
		if (var.length() < 2 || !super.initialize(var)) return false;

		try {
			money = Float.parseFloat(var.substring(1));
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	@Override
	public boolean check(LivingEntity caster) {
		return money(caster);
	}

	@Override
	public boolean check(LivingEntity caster, LivingEntity target) {
		return money(target);
	}

	@Override
	public boolean check(LivingEntity caster, Location location) {
		return false;
	}

	private boolean money(LivingEntity target) {
		if (!(target instanceof Player pl)) return false;
		return compare(MagicSpells.getMoneyHandler().checkMoney(pl), money);
	}

}
