package com.nisovin.magicspells.castmodifiers.conditions;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;

import org.jetbrains.annotations.NotNull;

import com.nisovin.magicspells.util.Name;
import com.nisovin.magicspells.handlers.DebugHandler;
import com.nisovin.magicspells.castmodifiers.conditions.util.OperatorCondition;

@Name("oxygen")
public class OxygenCondition extends OperatorCondition {
	
	private int oxygen;
	
	@Override
	public boolean initialize(@NotNull String var) {
		if (var.length() < 2 || !super.initialize(var)) return false;

		try {
			oxygen = Integer.parseInt(var.substring(1));
			return true;
		} catch (NumberFormatException e) {
			DebugHandler.debugNumberFormat(e);
			return false;
		}
	}

	@Override
	public boolean check(LivingEntity caster) {
		return oxygen(caster);
	}

	@Override
	public boolean check(LivingEntity caster, LivingEntity target) {
		return oxygen(target);
	}

	@Override
	public boolean check(LivingEntity caster, Location location) {
		return false;
	}

	private boolean oxygen(LivingEntity target) {
		return compare(target.getRemainingAir(), oxygen);
	}
	
}

