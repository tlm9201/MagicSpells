package com.nisovin.magicspells.castmodifiers.conditions;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;

import org.jetbrains.annotations.NotNull;

import com.nisovin.magicspells.util.Name;
import com.nisovin.magicspells.handlers.DebugHandler;
import com.nisovin.magicspells.castmodifiers.conditions.util.OperatorCondition;

@Name("rotation")
public class RotationCondition extends OperatorCondition {

	private float rotation;
	
	@Override
	public boolean initialize(@NotNull String var) {
		if (var.length() < 2 || !super.initialize(var)) return false;

		try {
			rotation = Float.parseFloat(var.substring(1));
			return true;
		} catch (NumberFormatException e) {
			DebugHandler.debugNumberFormat(e);
			return false;
		}
	}

	@Override
	public boolean check(LivingEntity caster) {
		return rotation(caster.getLocation());
	}

	@Override
	public boolean check(LivingEntity caster, LivingEntity target) {
		return rotation(target.getLocation());
	}

	@Override
	public boolean check(LivingEntity caster, Location location) {
		return rotation(location);
	}

	private boolean rotation(Location location) {
		float yaw = location.getYaw();
		if (yaw < 0) yaw += 360;
		return compare(yaw, rotation);
	}

}
