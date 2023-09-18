package com.nisovin.magicspells.spelleffects.effecttypes;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.configuration.ConfigurationSection;

import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.util.SpellData;
import com.nisovin.magicspells.util.config.ConfigData;
import com.nisovin.magicspells.spelleffects.SpellEffect;
import com.nisovin.magicspells.util.config.ConfigDataUtil;

public class BlockBreakEffect extends SpellEffect {

	private static int sourceId = 0;

	private ConfigData<Integer> range;
	private ConfigData<Integer> stage;
	@Override
	public void loadFromConfig(ConfigurationSection config) {
		range = ConfigDataUtil.getInteger(config, "range", 32);
		stage = ConfigDataUtil.getInteger(config, "stage", 0);
	}

	@Override
	public Runnable playEffectLocation(Location location, SpellData data) {
		int stage = this.stage.get(data);
		float progress = (stage >= 0 && stage <= 9) ? Math.min((stage + 1) / 10f, 1) : 0f;

		double range = Math.min(this.range.get(data), MagicSpells.getGlobalRadius());
		for (Player player : location.getNearbyPlayers(range))
			player.sendBlockDamage(location, progress, --sourceId);

		return null;
	}

}
