package com.nisovin.magicspells.castmodifiers.conditions;

import java.util.Set;
import java.util.EnumSet;

import org.bukkit.Location;
import org.bukkit.entity.Pose;
import org.bukkit.entity.LivingEntity;

import org.jetbrains.annotations.NotNull;

import com.nisovin.magicspells.castmodifiers.Condition;

public class PoseCondition extends Condition {

	private final Set<Pose> poses = EnumSet.noneOf(Pose.class);

	@Override
	public boolean initialize(@NotNull String var) {
		String[] split = var.split(",");

		for (String pose : split) {
			try {
				poses.add(Pose.valueOf(pose.trim().toUpperCase()));
			} catch (IllegalArgumentException e) {
				return false;
			}
		}

		return !poses.isEmpty();
	}

	@Override
	public boolean check(LivingEntity caster) {
		return poses.contains(caster.getPose());
	}

	@Override
	public boolean check(LivingEntity caster, LivingEntity target) {
		return poses.contains(target.getPose());
	}

	@Override
	public boolean check(LivingEntity caster, Location location) {
		return false;
	}

}
