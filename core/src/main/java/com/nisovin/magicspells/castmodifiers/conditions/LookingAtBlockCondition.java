package com.nisovin.magicspells.castmodifiers.conditions;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;

import com.nisovin.magicspells.util.Util;
import com.nisovin.magicspells.util.BlockInfo;
import com.nisovin.magicspells.util.BlockUtils;
import com.nisovin.magicspells.castmodifiers.Condition;

public class LookingAtBlockCondition extends Condition {

	private BlockInfo blockInfo;
	private int dist = 4;
	
	@Override
	public boolean initialize(String var) {
		try {
			String[] split = var.split(",");
			blockInfo = Util.getBlockInfo(split[0]);

			if (blockInfo.getMaterial() == null || !blockInfo.getMaterial().isBlock()) return false;
			if (split.length > 1) dist = Integer.parseInt(split[1]);

			return true;
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public boolean check(LivingEntity livingEntity) {
		return check(livingEntity, livingEntity);
	}

	@Override
	public boolean check(LivingEntity livingEntity, LivingEntity target) {
		Block block = BlockUtils.getTargetBlock(null, target, dist);
		if (block == null) return false;
		return blockInfo.getMaterial().equals(block.getType()) && (blockInfo.blockDataMatches(block.getBlockData()));
	}

	@Override
	public boolean check(LivingEntity livingEntity, Location location) {
		return false;
	}
	
}
