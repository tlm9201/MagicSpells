package com.nisovin.magicspells.spelleffects.effecttypes;

import java.util.Set;
import java.util.List;
import java.util.Random;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.util.Vector;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;

import com.nisovin.magicspells.util.Util;
import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.util.BlockInfo;
import com.nisovin.magicspells.util.BlockUtils;
import com.nisovin.magicspells.util.SpellAnimation;
import com.nisovin.magicspells.spelleffects.SpellEffect;

public class NovaEffect extends SpellEffect {

	private Random random;

	private List<BlockInfo> blockInfoList;

	private BlockInfo blockInfo;

	private double range;

	private int radius;
	private int startRadius;
	private int heightPerTick;
	private int expandInterval;
	private int expandingRadiusChange;

	private boolean circleShape;
	private boolean removePreviousBlocks;

	@Override
	public void loadFromString(String string) {
		super.loadFromString(string);
	}

	@Override
	public void loadFromConfig(ConfigurationSection config) {

		List<String> materialList = config.getStringList("types");
		if (!materialList.isEmpty()) {
			blockInfoList = new ArrayList<>();
			BlockInfo block;

			for (String str : materialList) {
				block = Util.getBlockInfo(str);
				if (block.getMaterial() == null || !block.getMaterial().isBlock()) {
					MagicSpells.error("Wrong nova type defined: '" + str + "'");
					continue;
				}
				blockInfoList.add(block);
			}
		}

		String blockName = config.getString("type", "fire");
		blockInfo = Util.getBlockInfo(blockName);

		if (blockInfo.getMaterial() == null || !blockInfo.getMaterial().isBlock()) {
			blockInfo = null;
			MagicSpells.error("Wrong nova type defined: '" + blockName + "'");
		}

		range = Math.max(config.getDouble("range", 20), 1);
		if (range > MagicSpells.getGlobalRadius()) range = MagicSpells.getGlobalRadius();

		radius = config.getInt("radius", 3);
		startRadius = config.getInt("start-radius", 0);
		heightPerTick = config.getInt("height-per-tick", 0);
		expandInterval = config.getInt("expand-interval", 5);
		expandingRadiusChange = config.getInt("expanding-radius-change", 1);
		if (expandingRadiusChange < 1) expandingRadiusChange = 1;

		circleShape = config.getBoolean("circle-shape", false);
		removePreviousBlocks = config.getBoolean("remove-previous-blocks", true);

		random = ThreadLocalRandom.current();

	}

	@Override
	public Runnable playEffectLocation(Location location) {
		if (blockInfo == null) return null;

		// Get nearby players
		Collection<Entity> nearbyEntities = location.getWorld().getNearbyEntities(location, range, range, range);
		List<Player> nearby = new ArrayList<>();
		for (Entity e : nearbyEntities) {
			if (!(e instanceof Player)) continue;
			nearby.add((Player) e);
		}

		// Start animation
		if (circleShape) {
			if (blockInfoList != null && !blockInfoList.isEmpty()) new NovaAnimationCircle(nearby, location.getBlock(), blockInfoList, radius, expandInterval, expandingRadiusChange);
			else new NovaAnimationCircle(nearby, location.getBlock(), blockInfo, radius, expandInterval, expandingRadiusChange);
		} else {
			if (blockInfoList != null && !blockInfoList.isEmpty()) new NovaAnimationSquare(nearby, location.getBlock(), blockInfoList, radius, expandInterval, expandingRadiusChange);
			else new NovaAnimationSquare(nearby, location.getBlock(), blockInfo, radius, expandInterval, expandingRadiusChange);
		}
		return null;
	}

	private class NovaAnimationSquare extends SpellAnimation {

		List<Player> nearby;
		Set<Block> blocks;
		Block center;
		BlockInfo blockInfo;
		List<BlockInfo> blockInfoList;
		int radiusNova;
		int radiusChange;

		public NovaAnimationSquare(List<Player> nearby, Block center, BlockInfo blockInfo, int radius, int tickInterval, int activeRadiusChange) {
			super(tickInterval, true);
			this.nearby = nearby;
			this.center = center;
			this.blockInfo = blockInfo;
			this.radiusNova = radius;
			this.blocks = new HashSet<>();
			this.radiusChange = activeRadiusChange;
		}

		public NovaAnimationSquare(List<Player> nearby, Block center, List<BlockInfo> blockInfoList, int radius, int tickInterval, int activeRadiusChange) {
			super(tickInterval, true);
			this.nearby = nearby;
			this.center = center;
			this.blockInfoList = blockInfoList;
			this.radiusNova = radius;
			this.blocks = new HashSet<>();
			this.radiusChange = activeRadiusChange;
		}

		@Override
		protected void onTick(int tick) {
			tick += startRadius;
			tick *= radiusChange;

			// Remove old blocks
			if (removePreviousBlocks) {
				for (Block b : blocks) {
					for (Player p : nearby) p.sendBlockChange(b.getLocation(), b.getType().createBlockData());
				}
				blocks.clear();
			}

			if (tick > radiusNova + 1) {
				stop(true);
				return;
			} else if (tick > radiusNova) {
				return;
			}

			// Set next ring
			int bx = center.getX();
			int y = center.getY();
			int bz = center.getZ();
			y += tick * heightPerTick;

			BlockInfo bInfo;

			for (int x = bx - tick; x <= bx + tick; x++) {
				for (int z = bz - tick; z <= bz + tick; z++) {
					if (Math.abs(x - bx) != tick && Math.abs(z - bz) != tick) continue;

					Block b = center.getWorld().getBlockAt(x, y, z);
					if (BlockUtils.isAir(b.getType()) || b.getType() == Material.TALL_GRASS) {
						Block under = b.getRelative(BlockFace.DOWN);
						if (BlockUtils.isAir(under.getType()) || under.getType() == Material.TALL_GRASS) b = under;
					} else if (BlockUtils.isAir(b.getRelative(BlockFace.UP).getType()) || b.getRelative(BlockFace.UP).getType() == Material.TALL_GRASS) {
						b = b.getRelative(BlockFace.UP);
					}

					if (!BlockUtils.isAir(b.getType()) && b.getType() != Material.TALL_GRASS) continue;

					if (blocks.contains(b)) continue;
					for (Player p : nearby) {
						if (blockInfoList != null && !blockInfoList.isEmpty()) {
							bInfo = blockInfoList.get(random.nextInt(blockInfoList.size()));
							p.sendBlockChange(b.getLocation(), bInfo.getMaterial().createBlockData(bInfo.getBlockDataString()));
						} else if (blockInfo != null) {
							p.sendBlockChange(b.getLocation(), blockInfo.getMaterial().createBlockData(blockInfo.getBlockDataString()));
						}
					}
					blocks.add(b);
				}
			}
		}

		@Override
		public void stop(boolean removeEntry) {
			super.stop(removeEntry);

			for (Block b : blocks) {
				for (Player p : nearby) p.sendBlockChange(b.getLocation(), b.getType().createBlockData());
			}

			blocks.clear();
		}

	}

	private class NovaAnimationCircle extends SpellAnimation {

		List<Player> nearby;
		Set<Block> blocks;
		Block center;
		BlockInfo blockInfo;
		List<BlockInfo> blockInfoList;
		int radiusNova;
		int radiusChange;

		public NovaAnimationCircle(List<Player> nearby, Block center, BlockInfo blockInfo, int radius, int tickInterval, int activeRadiusChange) {
			super(tickInterval, true);
			this.nearby = nearby;
			this.center = center;
			this.blockInfo = blockInfo;
			this.radiusNova = radius;
			this.blocks = new HashSet<>();
			this.radiusChange = activeRadiusChange;
		}

		public NovaAnimationCircle(List<Player> nearby, Block center, List<BlockInfo> blockInfoList, int radius, int tickInterval, int activeRadiusChange) {
			super(tickInterval, true);
			this.nearby = nearby;
			this.center = center;
			this.blockInfoList = blockInfoList;
			this.radiusNova = radius;
			this.blocks = new HashSet<>();
			this.radiusChange = activeRadiusChange;
		}

		@Override
		protected void onTick(int tick) {
			tick += startRadius;
			tick *= radiusChange;

			// Remove old blocks
			if (removePreviousBlocks) {
				for (Block b : blocks) {
					for (Player p : nearby) p.sendBlockChange(b.getLocation(), b.getType().createBlockData());
				}
				blocks.clear();
			}

			if (tick > radiusNova + 1) {
				stop(true);
				return;
			} else if (tick > radiusNova) {
				return;
			}

			// Generate the bottom block
			Location centerLocation = center.getLocation().clone();
			centerLocation.add(0.5, tick * heightPerTick, 0.5);
			Block b;

			BlockInfo bInfo;

			if (startRadius == 0 && tick == 0) {
				b = centerLocation.getWorld().getBlockAt(centerLocation);
				if (BlockUtils.isAir(b.getType()) || b.getType() == Material.TALL_GRASS) {
					Block under = b.getRelative(BlockFace.DOWN);
					if (BlockUtils.isAir(under.getType()) || under.getType() == Material.TALL_GRASS) b = under;
				} else if (BlockUtils.isAir(b.getRelative(BlockFace.UP).getType()) || b.getRelative(BlockFace.UP).getType() == Material.TALL_GRASS) {
					b = b.getRelative(BlockFace.UP);
				}

				if (!BlockUtils.isAir(b.getType()) && b.getType() != Material.TALL_GRASS) return;

				if (blocks.contains(b)) return;
				for (Player p : nearby) {
					if (blockInfoList != null && !blockInfoList.isEmpty()) {
						bInfo = blockInfoList.get(random.nextInt(blockInfoList.size()));
						p.sendBlockChange(b.getLocation(), bInfo.getMaterial().createBlockData(bInfo.getBlockDataString()));
					} else if (blockInfo != null) {
						p.sendBlockChange(b.getLocation(), blockInfo.getMaterial().createBlockData(blockInfo.getBlockDataString()));
					}
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

				if (BlockUtils.isAir(b.getType()) || b.getType() == Material.TALL_GRASS) {
					Block under = b.getRelative(BlockFace.DOWN);
					if (BlockUtils.isAir(under.getType()) || under.getType() == Material.TALL_GRASS) b = under;
				} else if (BlockUtils.isAir(b.getRelative(BlockFace.UP).getType()) || b.getRelative(BlockFace.UP).getType() == Material.TALL_GRASS) {
					b = b.getRelative(BlockFace.UP);
				}

				if (!BlockUtils.isAir(b.getType()) && b.getType() != Material.TALL_GRASS) continue;

				if (blocks.contains(b)) continue;
				for (Player p : nearby) {
					if (blockInfoList != null && !blockInfoList.isEmpty()) {
						bInfo = blockInfoList.get(random.nextInt(blockInfoList.size()));
						p.sendBlockChange(b.getLocation(), bInfo.getMaterial().createBlockData(bInfo.getBlockDataString()));
					} else if (blockInfo != null) {
						p.sendBlockChange(b.getLocation(), blockInfo.getMaterial().createBlockData(blockInfo.getBlockDataString()));
					}
				}
				blocks.add(b);
			}

		}

		@Override
		public void stop(boolean removeEntry) {
			super.stop(removeEntry);

			for (Block b : blocks) {
				for (Player p : nearby) p.sendBlockChange(b.getLocation(), b.getType().createBlockData());
			}

			blocks.clear();
		}

	}

}
