package com.nisovin.magicspells.spelleffects;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.configuration.ConfigurationSection;

import com.nisovin.magicspells.util.Util;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class ActionBarTextEffect extends SpellEffect {

	private String message;

	private boolean broadcast;

	@Override
	protected void loadFromConfig(ConfigurationSection config) {
		message = config.getString("message", "");
		broadcast = config.getBoolean("broadcast", false);
	}
	
	@Override
	protected Runnable playEffectEntity(Entity entity) {
		if (broadcast) Util.forEachPlayerOnline(this::send);
		else if (entity instanceof Player) send((Player) entity);
		return null;
	}
	
	private void send(Player player) {
		String msg = Util.doVarReplacementAndColorize(player, message);
		player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(msg));
	}
	
}
