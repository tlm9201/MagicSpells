package com.nisovin.magicspells.spelleffects.effecttypes;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.configuration.ConfigurationSection;

import com.nisovin.magicspells.util.SpellData;
import com.nisovin.magicspells.util.config.ConfigData;
import com.nisovin.magicspells.spelleffects.SpellEffect;
import com.nisovin.magicspells.util.config.ConfigDataUtil;

public class BlockBreakEffect extends SpellEffect {

	private static final int VANILLA_RADIUS = 32;

	private static int sourceId = 0;

	private ConfigData<Integer> range;
	private ConfigData<Integer> stage;

	@Override
	public void loadFromConfig(ConfigurationSection config) {
		range = ConfigDataUtil.getInteger(config, "range", VANILLA_RADIUS);
		stage = ConfigDataUtil.getInteger(config, "stage", 1);
	}

	@Override
	public Runnable playEffectLocation(Location location, SpellData data) {
		if (data == null) return null;
		if (location.getBlock().getType().isAir()) return null;

		double radius = Math.min(range.get(data), VANILLA_RADIUS);
		float progress = Math.max(0, Math.min(1, stage.get(data) / 10F));
		for (Player player : location.getNearbyPlayers(radius)) {
			player.sendBlockDamage(location, progress, sourceId++);
		}

		return null;
	}

}
