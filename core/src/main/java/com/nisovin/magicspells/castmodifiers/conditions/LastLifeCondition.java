package com.nisovin.magicspells.castmodifiers.conditions;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.entity.LivingEntity;

import org.jetbrains.annotations.NotNull;

import com.nisovin.magicspells.util.Name;
import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.handlers.DebugHandler;
import com.nisovin.magicspells.castmodifiers.conditions.util.OperatorCondition;

@Name("lastlife")
public class LastLifeCondition extends OperatorCondition {

	private int time;

	@Override
	public boolean initialize(@NotNull String var) {
		if (var.length() < 2 || !super.initialize(var)) return false;

		try {
			time = Integer.parseInt(var.substring(1));
			return true;
		} catch (NumberFormatException e) {
			DebugHandler.debugNumberFormat(e);
			return false;
		}
	}

	@Override
	public boolean check(LivingEntity caster) {
		return lifeLength(caster);
	}

	@Override
	public boolean check(LivingEntity caster, LivingEntity target) {
		return lifeLength(target);
	}

	@Override
	public boolean check(LivingEntity caster, Location location) {
		return false;
	}

	private boolean lifeLength(LivingEntity target) {
		if (!(target instanceof Player pl)) return false;
		return compare(MagicSpells.getLifeLengthTracker().getLastLifeLength(pl), time);
	}

}
