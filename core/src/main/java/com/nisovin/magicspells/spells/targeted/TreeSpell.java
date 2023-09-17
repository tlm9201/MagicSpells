package com.nisovin.magicspells.spells.targeted;

import java.util.List;
import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.TreeType;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;

import com.nisovin.magicspells.spelleffects.EffectPosition;
import com.nisovin.magicspells.util.*;
import com.nisovin.magicspells.spells.TargetedSpell;
import com.nisovin.magicspells.util.config.ConfigData;
import com.nisovin.magicspells.spells.TargetedLocationSpell;

public class TreeSpell extends TargetedSpell implements TargetedLocationSpell {

	private final ConfigData<TreeType> treeType;

	private final ConfigData<Integer> speed;
	
	public TreeSpell(MagicConfig config, String spellName) {
		super(config, spellName);

		treeType = getConfigDataEnum("tree-type", TreeType.class, TreeType.TREE);

		speed = getConfigDataInt("animation-speed", 20);
	}

	@Override
	public CastResult cast(SpellData data) {
		TargetInfo<Location> info = getTargetedBlockLocation(data, false);
		if (info.noTarget()) return noTarget(info);

		return castAtLocation(info.spellData());
	}

	@Override
	public CastResult castAtLocation(SpellData data) {
		Block target = data.location().getBlock().getRelative(BlockFace.UP);
		if (!target.getType().isAir()) return noTarget(data);

		Location loc = target.getLocation();
		data = data.location(loc);

		TreeType treeType = this.treeType.get(data);

		int speed = this.speed.get(data);
		if (speed > 0) {
			List<BlockState> blockStates = new ArrayList<>();
			loc.getWorld().generateTree(loc, random, treeType, state -> {
				blockStates.add(state);
				return false;
			});

			if (!blockStates.isEmpty()) {
				new GrowAnimation(data, loc.getBlockX(), loc.getBlockZ(), blockStates, speed);

				playSpellEffects(data);
				return new CastResult(PostCastAction.HANDLE_NORMALLY, data);
			}

			return new CastResult(PostCastAction.ALREADY_HANDLED, data);
		}

		if (loc.getWorld().generateTree(loc, random, treeType)) {
			playSpellEffects(data);
			return new CastResult(PostCastAction.HANDLE_NORMALLY, data);
		}

		return new CastResult(PostCastAction.ALREADY_HANDLED, data);
	}

	private class GrowAnimation extends SpellAnimation {
		
		private final List<BlockState> blockStates;
		private final int blocksPerTick;
		private final SpellData data;

		private GrowAnimation(SpellData data, int centerX, int centerZ, List<BlockState> blocks, int speed) {
			super(speed < 20 ? 20 / speed : 1, true);

			this.data = data;
			this.blockStates = blocks;
			this.blocksPerTick = speed/20 + 1;
			blockStates.sort((o1, o2) -> {
				if (o1.getY() < o2.getY()) return -1;
				if (o1.getY() > o2.getY()) return 1;

				int dist1 = Math.abs(o1.getX() - centerX) + Math.abs(o1.getZ() - centerZ);
				int dist2 = Math.abs(o2.getX() - centerX) + Math.abs(o2.getZ() - centerZ);
				return Integer.compare(dist1, dist2);
			});
		}

		@Override
		protected void onTick(int tick) {
			for (int i = 0; i < blocksPerTick; i++) {
				BlockState state = blockStates.remove(0);
				state.update(true);
				playSpellEffects(EffectPosition.SPECIAL, state.getLocation(), data);
				if (blockStates.isEmpty()) {
					stop(true);
					break;
				}
			}
		}
		
	}

}
