package com.nisovin.magicspells.storage;

import com.nisovin.magicspells.Spellbook;
import com.nisovin.magicspells.MagicSpells;

public abstract class StorageHandler {

	protected MagicSpells plugin;

	StorageHandler(MagicSpells plugin) {
		this.plugin = plugin;
	}

	public abstract void initialize();

	public abstract void load(Spellbook spellbook);

	public abstract void save(Spellbook spellbook);

	public abstract void disable();

}
