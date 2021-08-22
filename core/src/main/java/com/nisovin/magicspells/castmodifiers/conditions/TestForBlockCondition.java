package com.nisovin.magicspells.castmodifiers.conditions;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;

import com.nisovin.magicspells.util.Util;
import com.nisovin.magicspells.util.BlockInfo;
import com.nisovin.magicspells.util.MagicLocation;
import com.nisovin.magicspells.handlers.DebugHandler;
import com.nisovin.magicspells.castmodifiers.Condition;

public class TestForBlockCondition extends Condition {

	private MagicLocation location;
	private BlockInfo blockInfo;
	
	@Override
	public boolean initialize(String var) {
		try {
			String[] vars = var.split("=");
			String[] locs = vars[0].split(",");

			location = new MagicLocation(locs[0], Integer.parseInt(locs[1]), Integer.parseInt(locs[2]), Integer.parseInt(locs[3]));
			blockInfo = Util.getBlockInfo(vars[1]);

			return blockInfo.getMaterial() != null && blockInfo.getMaterial().isBlock();
		} catch (Exception e) {
			DebugHandler.debugGeneral(e);
			return false;
		}
	}

	@Override
	public boolean check(LivingEntity livingEntity) {
		return testForBlock();
	}

	@Override
	public boolean check(LivingEntity livingEntity, LivingEntity target) {
		return testForBlock();
	}

	@Override
	public boolean check(LivingEntity livingEntity, Location location) {
		return testForBlock();
	}

	public boolean testForBlock() {
		Block b = location.getLocation().getBlock();
		return location != null && blockInfo.getMaterial() == b.getType() && blockInfo.blockDataMatches(b.getBlockData());
	}

}
