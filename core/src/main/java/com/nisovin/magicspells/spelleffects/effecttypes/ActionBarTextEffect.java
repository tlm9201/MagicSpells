package com.nisovin.magicspells.spelleffects.effecttypes;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.configuration.ConfigurationSection;

import com.nisovin.magicspells.util.Util;
import com.nisovin.magicspells.util.SpellData;
import com.nisovin.magicspells.util.config.ConfigData;
import com.nisovin.magicspells.spelleffects.SpellEffect;
import com.nisovin.magicspells.util.config.ConfigDataUtil;

public class ActionBarTextEffect extends SpellEffect {

	private String message;

	private ConfigData<Boolean> broadcast;

	@Override
	protected void loadFromConfig(ConfigurationSection config) {
		message = config.getString("message", "");
		broadcast = ConfigDataUtil.getBoolean(config, "broadcast", false);
	}

	@Override
	protected Runnable playEffectEntity(Entity entity, SpellData data) {
		if (broadcast.get(data)) Util.forEachPlayerOnline(p -> send(p, data));
		else if (entity instanceof Player p) send(p, data);
		return null;
	}

	private void send(Player player, SpellData data) {
		player.sendActionBar(Util.getMiniMessage(message, player, data));
	}

}
