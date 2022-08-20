package com.nisovin.magicspells.spelleffects.effecttypes;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.configuration.ConfigurationSection;

import com.nisovin.magicspells.util.Util;
import com.nisovin.magicspells.util.SpellData;
import com.nisovin.magicspells.spelleffects.SpellEffect;

public class ActionBarTextEffect extends SpellEffect {

	private String message;

	private boolean broadcast;

	@Override
	protected void loadFromConfig(ConfigurationSection config) {
		message = config.getString("message", "");
		broadcast = config.getBoolean("broadcast", false);
	}

	@Override
	protected Runnable playEffectEntity(Entity entity, SpellData data) {
		String[] args = data == null ? null : data.args();
		if (broadcast) Util.forEachPlayerOnline(p -> send(p, args));
		else if (entity instanceof Player p) send(p, args);
		return null;
	}

	private void send(Player player, String[] args) {
		player.sendActionBar(Util.getMiniMessageWithArgsAndVars(player, message, args));
	}

}
