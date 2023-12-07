package com.nisovin.magicspells.spells.targeted;

import java.util.Set;
import java.util.TreeSet;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.util.RayTraceResult;
import org.bukkit.block.data.MultipleFacing;

import com.nisovin.magicspells.util.*;
import com.nisovin.magicspells.spells.TargetedSpell;
import com.nisovin.magicspells.util.config.ConfigData;

public class VinesSpell extends TargetedSpell {

	private final ConfigData<Integer> up;
	private final ConfigData<Integer> down;
	private final ConfigData<Integer> width;
	private final ConfigData<Integer> animateInterval;
	
	public VinesSpell(MagicConfig config, String spellName) {
		super(config, spellName);
		
		up = getConfigDataInt("up", 3);
		down = getConfigDataInt("down", 1);
		width = getConfigDataInt("width", 1);
		animateInterval = getConfigDataInt("animate-interval", 0);
	}

	@Override
	public CastResult cast(SpellData data) {
		RayTraceResult result = rayTraceBlocks(data);
		if (result == null) return noTarget(data);

		Block solid = result.getHitBlock();
		if (!solid.isSolid()) return noTarget(data);

		Block air = solid.getRelative(result.getHitBlockFace());
		if (!air.getType().isAir()) return noTarget(data);

		return growVines(data, air, solid);
	}

	private CastResult growVines(SpellData data, Block air, Block solid) {
		BlockFace face = air.getFace(solid);
		int x = 0;
		int z = 0;

		if (face == BlockFace.NORTH || face == BlockFace.SOUTH) x = 1;
		else if (face == BlockFace.EAST || face == BlockFace.WEST) z = 1;
		else return noTarget(data);

		TreeSet<VineBlock> blocks = new TreeSet<>();

		blocks.add(new VineBlock(air, air));
		int up = this.up.get(data);
		int down = this.down.get(data);
		growVinesVert(blocks, air, solid, air, up, down);

		int width = this.width.get(data);
		if (width > 1) {
			for (int i = 1; i <= width / 2; i++) {
				Block a = air.getRelative(x * i, 0, z * i);
				Block s = solid.getRelative(x * i, 0, z * i);
				if (a.getType() == Material.AIR && s.getType().isSolid()) {
					blocks.add(new VineBlock(a, air));
					growVinesVert(blocks, a, s, air, up, down);
				} else break;
			}
			for (int i = 1; i <= width / 2; i++) {
				Block a = air.getRelative(x * -i, 0, z * -i);
				Block s = solid.getRelative(x * -i, 0, z * -i);
				if (a.getType() == Material.AIR && s.getType().isSolid()) {
					blocks.add(new VineBlock(a, air));
					growVinesVert(blocks, a, s, air, up, down);
				} else break;
			}
		}
		
		if (blocks.isEmpty()) return noTarget(data);

		int animateInterval = this.animateInterval.get(data);
		if (animateInterval <= 0) {
			for (VineBlock vine : blocks) setBlockToVine(vine.block, face);
		} else new VineAnimation(face, blocks, animateInterval);

		playSpellEffects(data);
		return new CastResult(PostCastAction.HANDLE_NORMALLY, data);
	}
	
	private void setBlockToVine(Block block, BlockFace face) {
		if (block.getType() != Material.AIR) return;
		BlockState state = block.getState();
		state.setType(Material.VINE);
		MultipleFacing facing = (MultipleFacing) state.getBlockData();
		if (!facing.getAllowedFaces().contains(face)) return;
		facing.setFace(face, true);
		state.setBlockData(facing);
		state.update(true, false);
	}
	
	private void growVinesVert(Set<VineBlock> blocks, Block air, Block solid, Block center, int up, int down) {
		Block b;
		for (int i = 1; i <= up; i++) {
			b = air.getRelative(0, i, 0);
			if (b.getType() == Material.AIR && solid.getRelative(0, i, 0).getType().isSolid()) {
				blocks.add(new VineBlock(b, center));
			} else break;
		}
		for (int i = 1; i <= down; i++) {
			b = air.getRelative(0, -i, 0);
			if (b.getType() == Material.AIR && solid.getRelative(0, -i, 0).getType().isSolid()) {
				blocks.add(new VineBlock(b, center));
			} else break;
		}
	}
	
	private static class VineBlock implements Comparable<VineBlock> {

		private final Block block;
		private final double distanceSquared;

		private VineBlock(Block block, Block center) {
			this.block = block;
			this.distanceSquared = block.getLocation().distanceSquared(center.getLocation());
		}
		
		@Override
		public int compareTo(VineBlock o) {
			if (o.distanceSquared < this.distanceSquared) return 1;
			if (o.distanceSquared > this.distanceSquared) return -1;
			return o.block.getLocation().toString().compareTo(this.block.getLocation().toString());
		}
		
	}

	private class VineAnimation extends SpellAnimation {

		private final BlockFace face;
		private final TreeSet<VineBlock> blocks;

		private VineAnimation(BlockFace face, TreeSet<VineBlock> blocks, int animateInterval) {
			super(animateInterval, true);
			this.face = face;
			this.blocks = blocks;
		}

		@Override
		protected void onTick(int tick) {
			VineBlock block = blocks.pollFirst();
			if (block != null) setBlockToVine(block.block, face);
			else this.stop(true);
		}
		
	}
	
}
