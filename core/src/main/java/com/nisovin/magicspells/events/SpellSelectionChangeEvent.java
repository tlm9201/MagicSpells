package com.nisovin.magicspells.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

import com.nisovin.magicspells.Spell;
import com.nisovin.magicspells.Spellbook;
import com.nisovin.magicspells.util.CastItem;

public class SpellSelectionChangeEvent extends SpellEvent implements Cancellable {

	private CastItem castItem;
	private Spellbook spellbook;

	private boolean cancelled = false;

	public SpellSelectionChangeEvent(Spell spell, Player caster, CastItem castItem, Spellbook spellbook) {
		super(spell, caster);
		this.castItem = castItem;
		this.spellbook = spellbook;
	}

	public CastItem getCastItem() {
		return castItem;
	}

	public Spellbook getSpellbook() {
		return spellbook;
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}

}
