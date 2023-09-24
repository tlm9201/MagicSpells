package com.nisovin.magicspells.events;

import org.bukkit.event.Event;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import com.nisovin.magicspells.Spellbook;

import org.jetbrains.annotations.NotNull;

public class SpellbookReloadEvent extends Event {

	private static final HandlerList handlers = new HandlerList();

	private Player player;

	private Spellbook spellBook;

	public SpellbookReloadEvent(Player player, Spellbook spellbook) {
		this.player = player;
		this.spellBook = spellbook;
	}

	public Player getPlayer() {
		return player;
	}

	public Spellbook getSpellBook() {
		return spellBook;
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
