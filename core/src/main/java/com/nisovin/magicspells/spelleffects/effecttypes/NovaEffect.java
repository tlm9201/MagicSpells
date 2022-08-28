package com.nisovin.magicspells.spelleffects.effecttypes;

import java.util.Set;
import java.util.List;
import java.util.Random;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.util.Vector;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.configuration.ConfigurationSection;

import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.util.SpellData;
import com.nisovin.magicspells.util.BlockUtils;
import com.nisovin.magicspells.util.SpellAnimation;
import com.nisovin.magicspells.util.config.ConfigData;
import com.nisovin.magicspells.spelleffects.SpellEffect;
import com.nisovin.magicspells.util.config.ConfigDataUtil;

public class NovaEffect extends SpellEffect {

	private Random random;

	private List<BlockData> blockDataList;

	private BlockData blockData;

	private ConfigData<Double> range;

	private ConfigData<Integer> radius;
	private ConfigData<Integer> startRadius;
	private ConfigData<Integer> heightPerTick;
	private ConfigData<Integer> expandInterval;
	private ConfigData<Integer> expandingRadiusChange;

	private boolean circleShape;
	private boolean removePreviousBlocks;
	@Override
	public void loadFromConfig(ConfigurationSection config) {
		List<String> materialList = config.getStringList("types");
		if (!materialList.isEmpty()) {
			blockDataList = new ArrayList<>();

			for (String str : materialList) {
				BlockData data;
				try {
					data = Bukkit.createBlockData(str.toLowerCase());
				} catch (IllegalArgumentException e) {
					MagicSpells.error("Wrong nova type defined: '" + str + "'");
					continue;
				}

				if (!data.getMaterial().isBlock()) {
					MagicSpells.error("Wrong nova type defined: '" + str + "'");
					continue;
				}

				blockDataList.add(data);
			}
		}

		String blockName = config.getString("type", "fire");

		try {
			blockData = Bukkit.createBlockData(blockName.toLowerCase());
		} catch (IllegalArgumentException e) {
			blockData = null;
			MagicSpells.error("Wrong nova type defined: '" + blockName + "'");
		}

		if (blockData != null && !blockData.getMaterial().isBlock()) {
			blockData = null;
			MagicSpells.error("Wrong nova type defined: '" + blockName + "'");
		}

		range = ConfigDataUtil.getDouble(config, "range", 20);

		radius = ConfigDataUtil.getInteger(config, "radius", 3);
		startRadius = ConfigDataUtil.getInteger(config, "start-radius", 0);
		heightPerTick = ConfigDataUtil.getInteger(config, "height-per-tick", 0);
		expandInterval = ConfigDataUtil.getInteger(config, "expand-interval", 5);
		expandingRadiusChange = ConfigDataUtil.getInteger(config, "expanding-radius-change", 1);

		circleShape = config.getBoolean("circle-shape", false);
		removePreviousBlocks = config.getBoolean("remove-previous-blocks", true);

		random = ThreadLocalRandom.current();
	}

	@Override
	public Runnable playEffectLocation(Location location, SpellData data) {
		if (blockData == null) return null;

		double range = this.range.get(data);
		range = Math.max(Math.min(range, MagicSpells.getGlobalRadius()), 1);

		// Get nearby players
		Collection<Entity> nearbyEntities = location.getWorld().getNearbyEntities(location, range, range, range);
		List<Player> nearby = new ArrayList<>();
		for (Entity e : nearbyEntities) {
			if (!(e instanceof Player)) continue;
			nearby.add((Player) e);
		}

		// Start animation
		if (circleShape) {
			if (blockDataList != null && !blockDataList.isEmpty())
				new NovaAnimationCircle(nearby, location.getBlock(), blockDataList, data);
			else
				new NovaAnimationCircle(nearby, location.getBlock(), blockData, data);
		} else {
			if (blockDataList != null && !blockDataList.isEmpty())
				new NovaAnimationSquare(nearby, location.getBlock(), blockDataList, data);
			else
				new NovaAnimationSquare(nearby, location.getBlock(), blockData, data);
		}
		return null;
	}

	private abstract class NovaAnimation extends SpellAnimation {

		protected final List<Player> nearby;
		protected final Block center;

		protected final List<BlockData> blockDataList;
		protected final BlockData blockData;

		protected final Set<Block> blocks;

		protected final int radius;
		protected final int startRadius;
		protected final int heightPerTick;
		protected final int expandingRadiusChange;

		public NovaAnimation(List<Player> nearby, Block center, BlockData blockData, List<BlockData> blockDataList, SpellData data) {
			super(expandInterval.get(data), true);

			this.nearby = nearby;
			this.center = center;
			this.blockData = blockData;
			this.blockDataList = blockDataList;

			blocks = new HashSet<>();

			radius = NovaEffect.this.radius.get(data);
			startRadius = NovaEffect.this.startRadius.get(data);
			heightPerTick = NovaEffect.this.heightPerTick.get(data);

			int radiusChange = NovaEffect.this.expandingRadiusChange.get(data);
			if (radiusChange < 1) radiusChange = 1;
			expandingRadiusChange = radiusChange;
		}

	}

	private class NovaAnimationSquare extends NovaAnimation {

		public NovaAnimationSquare(List<Player> nearby, Block center, BlockData blockData, SpellData data) {
			super(nearby, center, blockData, null, data);
		}

		public NovaAnimationSquare(List<Player> nearby, Block center, List<BlockData> blockDataList, SpellData data) {
			super(nearby, center, null, blockDataList, data);
		}

		@Override
		protected void onTick(int tick) {
			tick += startRadius;
			tick *= expandingRadiusChange;

			// Remove old blocks
			if (removePreviousBlocks) {
				for (Block b : blocks)
					for (Player p : nearby)
						p.sendBlockChange(b.getLocation(), b.getBlockData());

				blocks.clear();
			}

			if (tick > radius + 1) {
				stop(true);
				return;
			} else if (tick > radius) {
				return;
			}

			// Set next ring
			int bx = center.getX();
			int y = center.getY();
			int bz = center.getZ();
			y += tick * heightPerTick;

			for (int x = bx - tick; x <= bx + tick; x++) {
				for (int z = bz - tick; z <= bz + tick; z++) {
					if (Math.abs(x - bx) != tick && Math.abs(z - bz) != tick) continue;

					Block b = center.getWorld().getBlockAt(x, y, z);
					if (BlockUtils.isPathable(b) && !BlockUtils.isLiquid(b)) {
						Block under = b.getRelative(BlockFace.DOWN);
						if (BlockUtils.isPathable(under) && !BlockUtils.isLiquid(under)) b = under;
					} else if (BlockUtils.isPathable(b.getRelative(BlockFace.UP)) && !BlockUtils.isLiquid(b.getRelative(BlockFace.UP))) {
						b = b.getRelative(BlockFace.UP);
					}

					if (!BlockUtils.isPathable(b) || BlockUtils.isLiquid(b) || blocks.contains(b)) continue;

					for (Player p : nearby) {
						if (blockDataList != null && !blockDataList.isEmpty())
							p.sendBlockChange(b.getLocation(), blockDataList.get(random.nextInt(blockDataList.size())));
						else if (blockData != null)
							p.sendBlockChange(b.getLocation(), blockData);
					}

					blocks.add(b);
				}
			}
		}

		@Override
		public void stop(boolean removeEntry) {
			super.stop(removeEntry);

			for (Block b : blocks) {
				for (Player p : nearby)
					p.sendBlockChange(b.getLocation(), b.getBlockData());
			}

			blocks.clear();
		}

	}

	private class NovaAnimationCircle extends NovaAnimation {

		public NovaAnimationCircle(List<Player> nearby, Block center, BlockData blockData, SpellData data) {
			super(nearby, center, blockData, null, data);
		}

		public NovaAnimationCircle(List<Player> nearby, Block center, List<BlockData> blockDataList, SpellData data) {
			super(nearby, center, null, blockDataList, data);
		}

		@Override
		protected void onTick(int tick) {
			tick += startRadius;
			tick *= expandingRadiusChange;

			// Remove old blocks
			if (removePreviousBlocks) {
				for (Block b : blocks)
					for (Player p : nearby)
						p.sendBlockChange(b.getLocation(), b.getBlockData());

				blocks.clear();
			}

			if (tick > radius + 1) {
				stop(true);
				return;
			} else if (tick > radius) {
				return;
			}

			// Generate the bottom block
			Location centerLocation = center.getLocation().clone();
			centerLocation.add(0.5, tick * heightPerTick, 0.5);
			Block b;

			if (startRadius == 0 && tick == 0) {
				b = centerLocation.getWorld().getBlockAt(centerLocation);
				if (BlockUtils.isPathable(b) && !BlockUtils.isLiquid(b)) {
					Block under = b.getRelative(BlockFace.DOWN);
					if (BlockUtils.isPathable(under) && !BlockUtils.isLiquid(under)) b = under;
				} else if (BlockUtils.isPathable(b.getRelative(BlockFace.UP)) && !BlockUtils.isLiquid(b.getRelative(BlockFace.UP))) {
					b = b.getRelative(BlockFace.UP);
				}

				if (!BlockUtils.isPathable(b) || BlockUtils.isLiquid(b) || blocks.contains(b)) return;

				for (Player p : nearby) {
					if (blockDataList != null && !blockDataList.isEmpty())
						p.sendBlockChange(b.getLocation(), blockDataList.get(random.nextInt(blockDataList.size())));
					else if (blockData != null)
						p.sendBlockChange(b.getLocation(), blockData);
				}

				blocks.add(b);
			}

			// Generate the circle
			Vector v;
			double angle, x, z;
			double amount = tick * 64;
			double inc = (2 * Math.PI) / amount;
			for (int i = 0; i < amount; i++) {
				angle = i * inc;
				x = tick * Math.cos(angle);
				z = tick * Math.sin(angle);
				v = new Vector(x, 0, z);
				b = center.getWorld().getBlockAt(centerLocation.add(v));
				centerLocation.subtract(v);

				if (BlockUtils.isPathable(b) && !BlockUtils.isLiquid(b)) {
					Block under = b.getRelative(BlockFace.DOWN);
					if (BlockUtils.isPathable(under) && !BlockUtils.isLiquid(under)) b = under;
				} else if (BlockUtils.isPathable(b.getRelative(BlockFace.UP)) && !BlockUtils.isLiquid(b.getRelative(BlockFace.UP))) {
					b = b.getRelative(BlockFace.UP);
				}

				if (!BlockUtils.isPathable(b) || BlockUtils.isLiquid(b) || blocks.contains(b)) continue;

				for (Player p : nearby) {
					if (blockDataList != null && !blockDataList.isEmpty())
						p.sendBlockChange(b.getLocation(), blockDataList.get(random.nextInt(blockDataList.size())));
					else if (blockData != null)
						p.sendBlockChange(b.getLocation(), blockData);
				}

				blocks.add(b);
			}

		}

		@Override
		public void stop(boolean removeEntry) {
			super.stop(removeEntry);

			for (Block b : blocks)
				for (Player p : nearby)
					p.sendBlockChange(b.getLocation(), b.getBlockData());

			blocks.clear();
		}

	}

}
