package com.nisovin.magicspells.castmodifiers.conditions;

import java.util.Set;
import java.util.HashSet;

import org.bukkit.Material;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.LivingEntity;

import com.nisovin.magicspells.util.Util;
import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.util.BlockInfo;
import com.nisovin.magicspells.handlers.DebugHandler;
import com.nisovin.magicspells.castmodifiers.Condition;

public class UnderBlockCondition extends Condition {

	private int height;

	private Set<BlockInfo> blockInfoSet;

	@Override
	public boolean initialize(String var) {
		//Lets TRY and catch some formatting mistakes for this modifier.
		String blocks;
		try {
			String[] variable = var.split(";",2);
			blocks = variable[0];
			height = Integer.parseInt(variable[1]);
		} catch (NumberFormatException e) { //Oh no, that variable[1] is somehow not a string? give them an Error!
			DebugHandler.debugNumberFormat(e);
			return false;
		} catch (ArrayIndexOutOfBoundsException missingSemiColon) { //No ; in modifier? Just great, give them an Error!
			MagicSpells.error("No ; seperator for depth was found!");
			return false;
		}

		//Checks if they put any blocks to compare with in the first place.
		if (blocks.isEmpty()) {
			MagicSpells.error("Didn't specify any blocks to compare with.");
			return false;
		}

		//We need to parse a list of the blocks required and check if they are valid.
		blockInfoSet = new HashSet<>();
		String[] split = blocks.split(",");

		for (String s : split) {
			BlockInfo bInfo = Util.getBlockInfo(s);
			if (bInfo.getMaterial() == null || !bInfo.getMaterial().isBlock()) return false;
			blockInfoSet.add(bInfo);
		}
		return true;
	}

	@Override
	public boolean check(LivingEntity livingEntity) {
		return underBlock(livingEntity.getLocation());
	}

	@Override
	public boolean check(LivingEntity livingEntity, LivingEntity target) {
		return underBlock(target.getLocation());
	}

	@Override
	public boolean check(LivingEntity livingEntity, Location location) {
		return underBlock(location);
	}

	private boolean underBlock(Location location) {
		Block block = location.clone().getBlock();

		for (int i = 0; i < height; i++) {

			for (BlockInfo blockInfo : blockInfoSet) {
				Material m = blockInfo.getMaterial();

				if (m != block.getType() && !blockInfo.blockDataMatches(block.getBlockData())) {
					block = block.getRelative(BlockFace.UP);
					continue;
				}

				return true;
			}
		}
		return false;
	}

}
