package com.nisovin.magicspells.castmodifiers.conditions;

import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.RayTraceResult;
import org.bukkit.block.data.BlockData;

import org.jetbrains.annotations.NotNull;

import com.nisovin.magicspells.util.Name;
import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.castmodifiers.Condition;

@Name("lookingatblock")
public class LookingAtBlockCondition extends Condition {

	private BlockData blockData;
	private int dist = 4;

	@Override
	public boolean initialize(@NotNull String var) {
		String[] split = var.split(",(?![^\\[]*])");

		try {
			blockData = Bukkit.createBlockData(split[0].trim().toLowerCase());
		} catch (IllegalArgumentException e) {
			return false;
		}

		if (!blockData.getMaterial().isBlock()) return false;

		if (split.length > 1) {
			try {
				dist = Integer.parseInt(split[1].trim());
			} catch (NumberFormatException e) {
				return false;
			}
		}

		return true;
	}

	@Override
	public boolean check(LivingEntity caster) {
		return lookingAt(caster);
	}

	@Override
	public boolean check(LivingEntity caster, LivingEntity target) {
		return lookingAt(target);
	}

	@Override
	public boolean check(LivingEntity caster, Location location) {
		return false;
	}

	private boolean lookingAt(LivingEntity target) {
		Set<Material> transparent = MagicSpells.getTransparentBlocks();
		Location location = target.getEyeLocation();

		RayTraceResult result = location.getWorld().rayTraceBlocks(location, location.getDirection(), dist, MagicSpells.getFluidCollisionMode(), MagicSpells.isIgnoringPassableBlocks(), block -> !transparent.contains(block.getType()));
		if (result == null) return false;

		return result.getHitBlock().getBlockData().matches(blockData);
	}

}
