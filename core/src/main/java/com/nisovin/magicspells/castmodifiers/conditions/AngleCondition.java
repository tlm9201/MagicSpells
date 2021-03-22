package com.nisovin.magicspells.castmodifiers.conditions;

import org.apache.commons.math3.util.FastMath;

import org.bukkit.Location;
import org.bukkit.util.Vector;
import org.bukkit.entity.LivingEntity;

import com.nisovin.magicspells.handlers.DebugHandler;
import com.nisovin.magicspells.castmodifiers.conditions.util.OperatorCondition;

public class AngleCondition extends OperatorCondition {

	private double angle;

	@Override
	public boolean initialize(String var) {
		if (var.length() < 2 || !super.initialize(var)) return false;

		try {
			angle = FastMath.toRadians(Double.parseDouble(var.substring(1)));
			return true;
		} catch (NumberFormatException e) {
			DebugHandler.debugNumberFormat(e);
			return false;
		}
	}

	@Override
	public boolean check(LivingEntity livingEntity) {
		return false;
	}

	@Override
	public boolean check(LivingEntity livingEntity, LivingEntity target) {
		Location casterLocation = livingEntity.getLocation();

		Vector targetVector = target.getLocation().toVector();
		Vector casterVector = casterLocation.toVector();
		Vector facing = casterLocation.getDirection();

		float degrees = targetVector.subtract(casterVector).angle(facing);
		return checkAngle(degrees);
	}

	@Override
	public boolean check(LivingEntity livingEntity, Location location) {
		Location casterLocation = livingEntity.getLocation();

		Vector casterVector = casterLocation.toVector();
		Vector facing = casterLocation.getDirection();
		Vector targetVector = location.toVector();

		float degrees = targetVector.subtract(casterVector).angle(facing);
		return checkAngle(degrees);
	}

	private boolean checkAngle(double degrees) {
		if (equals) return degrees == angle;
		else if (moreThan) return degrees > angle;
		else if (lessThan) return degrees < angle;
		return false;
	}

}
