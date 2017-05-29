package com.nisovin.magicspells.spells.buff;

import java.util.HashSet;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.GrassSpecies;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.material.LongGrass;

import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.spells.BuffSpell;
import com.nisovin.magicspells.util.ConfigData;
import com.nisovin.magicspells.util.MagicConfig;
import com.nisovin.magicspells.util.PlayerNameUtils;

public class LifewalkSpell extends BuffSpell {
	
	HashSet<String> lifewalkers;
	private Grower grower;
	Random random;
	
	@ConfigData(field="tick-interval", dataType="int", defaultValue="15")
	int tickInterval;
	
	@ConfigData(field="red-flower-chance", dataType="int", defaultValue="15")
	int redFlowerChance;
	
	@ConfigData(field="yellow-flower-chance", dataType="int", defaultValue="15")
	int yellowFlowerChance;
	
	@ConfigData(field="sapling-chance", dataType="int", defaultValue="5")
	int saplingChance;
	
	@ConfigData(field="tallgrass-chance", dataType="int", defaultValue="25")
	int tallgrassChance;
	
	@ConfigData(field="fern-chance", dataType="int", defaultValue="15")
	int fernChance;
	
	public LifewalkSpell(MagicConfig config, String spellName) {
		super(config, spellName);
		
		lifewalkers = new HashSet<>();
		random = new Random();
		
		tickInterval = getConfigInt("tick-interval", 15);
		redFlowerChance = getConfigInt("red-flower-chance", 15);
		yellowFlowerChance = getConfigInt("yellow-flower-chance", 15);
		saplingChance = getConfigInt("sapling-chance", 5);
		tallgrassChance = getConfigInt("tallgrass-chance", 25);
		fernChance = getConfigInt("fern-chance", 15);
	}

	@Override
	public boolean castBuff(Player player, float power, String[] args) {
		lifewalkers.add(player.getName());
		if (grower == null) grower = new Grower();
		return true;
	}	
	
	@Override
	public void turnOffBuff(Player player) {
		lifewalkers.remove(player.getName());
		if (!lifewalkers.isEmpty()) return;
		if (grower == null) return;
		
		grower.stop();
		grower = null;
	}
	
	@Override
	protected void turnOff() {
		lifewalkers.clear();
		if (grower == null) return;
		
		grower.stop();
		grower = null;
	}

	private class Grower implements Runnable {
		
		int taskId;
		String[] strArr = new String[0];
		
		public Grower() {
			taskId = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(MagicSpells.plugin, this, tickInterval, tickInterval);
		}
		
		public void stop() {
			Bukkit.getServer().getScheduler().cancelTask(taskId);
		}
		
		@Override
		public void run() {
			for (String s : lifewalkers.toArray(strArr)) {
				Player player = PlayerNameUtils.getPlayer(s);
				if (player != null) {
					if (isExpired(player)) {
						turnOff(player);
						continue;
					}
					Block feet = player.getLocation().getBlock();
					Block ground = feet.getRelative(BlockFace.DOWN);
					if (feet.getType() == Material.AIR && (ground.getType() == Material.DIRT || ground.getType() == Material.GRASS)) {
						if (ground.getType() == Material.DIRT) {
							ground.setType(Material.GRASS);
						}
						int rand = random.nextInt(100);
						if (rand < redFlowerChance) {
							feet.setType(Material.RED_ROSE);
							addUse(player);
							chargeUseCost(player);
						} else {
							rand -= redFlowerChance;
							if (rand < yellowFlowerChance) {
								feet.setType(Material.YELLOW_FLOWER);
								addUse(player);
								chargeUseCost(player);
							} else {
								rand -= yellowFlowerChance;
								if (rand < saplingChance) {
									feet.setType(Material.SAPLING);
									addUse(player);
									chargeUseCost(player);
								} else {
									rand -= saplingChance;
									if (rand < tallgrassChance) {
										BlockState state = feet.getState();
										state.setType(Material.LONG_GRASS);
										state.setData(new LongGrass(GrassSpecies.NORMAL));
										state.update(true);
										addUse(player);
										chargeUseCost(player);
									} else {
										rand -= tallgrassChance;
										if (rand < fernChance) {
											BlockState state = feet.getState();
											state.setType(Material.LONG_GRASS);
											state.setData(new LongGrass(GrassSpecies.FERN_LIKE));
											state.update(true);
											addUse(player);
											chargeUseCost(player);
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}

	@Override
	public boolean isActive(Player player) {
		return lifewalkers.contains(player.getName());
	}

}
