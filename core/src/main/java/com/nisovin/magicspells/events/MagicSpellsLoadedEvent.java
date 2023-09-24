package com.nisovin.magicspells.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import org.jetbrains.annotations.NotNull;

import com.nisovin.magicspells.MagicSpells;

/**
 * This event is fired whenever MagicSpells finishes loading, either after the server first starts,
 * after a server reload (/reload), or after an internal reload (/ms reload).
 */
public class MagicSpellsLoadedEvent extends Event {

	private static final HandlerList handlers = new HandlerList();

	private final MagicSpells plugin;

	public MagicSpellsLoadedEvent(MagicSpells plugin) {
		this.plugin = plugin;
	}

	/**
	 * Gets the instance of the MagicSpells plugin
	 * @return plugin instance
	 */
	public MagicSpells getPlugin() {
		return plugin;
	}

	@NotNull
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

}
