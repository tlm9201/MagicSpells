package com.nisovin.magicspells.castmodifiers.conditions;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;

import org.jetbrains.annotations.NotNull;

import com.nisovin.magicspells.util.Name;
import com.nisovin.magicspells.handlers.DebugHandler;
import com.nisovin.magicspells.castmodifiers.conditions.util.OperatorCondition;

@Name("elevation")
public class ElevationCondition extends OperatorCondition {

	private double y;
	
	@Override
	public boolean initialize(@NotNull String var) {
		if (var.length() < 2 || !super.initialize(var)) return false;

		try {
			y = Double.parseDouble(var.substring(1));
			return true;
		} catch (NumberFormatException e) {
			DebugHandler.debugNumberFormat(e);
			return false;
		}
	}

	@Override
	public boolean check(LivingEntity caster) {
		return elevation(caster.getLocation());
	}

	@Override
	public boolean check(LivingEntity caster, LivingEntity target) {
		return elevation(target.getLocation());
	}

	@Override
	public boolean check(LivingEntity caster, Location location) {
		return elevation(location);
	}

	private boolean elevation(Location location) {
		return compare(location.getY(), y);
	}

}
