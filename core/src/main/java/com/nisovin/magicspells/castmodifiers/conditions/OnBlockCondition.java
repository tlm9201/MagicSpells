package com.nisovin.magicspells.castmodifiers.conditions;

import java.util.Set;
import java.util.HashSet;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;

import com.nisovin.magicspells.util.Util;
import com.nisovin.magicspells.util.BlockInfo;
import com.nisovin.magicspells.castmodifiers.Condition;

public class OnBlockCondition extends Condition {

	private Set<BlockInfo> blockInfoSet;
	private BlockInfo blockInfo;

	@Override
	public boolean initialize(String var) {
		if (var.contains(",")) {
			blockInfoSet = new HashSet<>();
			String[] split = var.split(",");
			for (String s : split) {
				BlockInfo bInfo = Util.getBlockInfo(s);
				if (bInfo.getMaterial() == null) return false;
				if (!bInfo.getMaterial().isBlock()) return false;
				blockInfoSet.add(bInfo);
			}
			return true;
		}

		blockInfo = Util.getBlockInfo(var);
		if (blockInfo.getMaterial() == null) {
			blockInfo = null;
			return false;
		}
		return blockInfo.getMaterial().isBlock();
	}

	@Override
	public boolean check(LivingEntity livingEntity) {
		return onBlock(livingEntity);
	}
	
	@Override
	public boolean check(LivingEntity livingEntity, LivingEntity target) {
		return onBlock(target);
	}
	
	@Override
	public boolean check(LivingEntity livingEntity, Location location) {
		return false;
	}

	private boolean onBlock(LivingEntity entity) {
		Block block = entity.getLocation().subtract(0, 1, 0).getBlock();
		if (blockInfo != null) return blockInfo.getMaterial().equals(block.getType());

		for (BlockInfo bInfo : blockInfoSet) {
			// check block and block data if it's not null
			if (bInfo.getMaterial() == block.getType() && bInfo.blockDataMatches(block.getBlockData())) return true;
		}
		return false;
	}

}
