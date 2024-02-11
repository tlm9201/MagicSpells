package com.nisovin.magicspells.castmodifiers.conditions;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.block.data.BlockData;

import org.jetbrains.annotations.NotNull;

import com.nisovin.magicspells.util.Name;
import com.nisovin.magicspells.util.LocationUtil;
import com.nisovin.magicspells.handlers.DebugHandler;
import com.nisovin.magicspells.castmodifiers.Condition;

@Name("testforblock")
public class TestForBlockCondition extends Condition {

	private Location location;
	private BlockData blockData;

	@Override
	public boolean initialize(@NotNull String var) {
		String[] vars = var.split("=");
		try {
			location = LocationUtil.fromString(vars[0]);
			blockData = Bukkit.createBlockData(vars[1].toLowerCase());

			return location != null && blockData.getMaterial().isBlock();
		} catch (Exception e) {
			DebugHandler.debugGeneral(e);
			return false;
		}
	}

	@Override
	public boolean check(LivingEntity caster) {
		return testForBlock();
	}

	@Override
	public boolean check(LivingEntity caster, LivingEntity target) {
		return testForBlock();
	}

	@Override
	public boolean check(LivingEntity caster, Location location) {
		return testForBlock();
	}

	public boolean testForBlock() {
		return location.getBlock().getBlockData().matches(blockData);
	}

}
