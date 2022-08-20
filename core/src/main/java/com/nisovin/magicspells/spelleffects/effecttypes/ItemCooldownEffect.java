package com.nisovin.magicspells.spelleffects.effecttypes;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.configuration.ConfigurationSection;

import com.nisovin.magicspells.util.TimeUtil;
import com.nisovin.magicspells.util.SpellData;
import com.nisovin.magicspells.util.config.ConfigData;
import com.nisovin.magicspells.spelleffects.SpellEffect;
import com.nisovin.magicspells.util.config.ConfigDataUtil;

public class ItemCooldownEffect extends SpellEffect {

	private ConfigData<Integer> duration;
	private ConfigData<Material> type;

	@Override
	protected void loadFromConfig(ConfigurationSection config) {
		duration = ConfigDataUtil.getInteger(config, "duration", TimeUtil.TICKS_PER_SECOND);
		type = ConfigDataUtil.getEnum(config, "item", Material.class, Material.STONE);
	}

	@Override
	protected Runnable playEffectEntity(Entity entity, SpellData data) {
		if (!(entity instanceof Player player)) return null;

		player.setCooldown(type.get(data), duration.get(data));
		return null;
	}

}
