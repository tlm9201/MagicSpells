package com.nisovin.magicspells.storage.types;

import com.nisovin.magicspells.Spellbook;
import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.storage.Database;
import com.nisovin.magicspells.storage.StorageHandler;

public class DatabaseStorage extends StorageHandler {

	private final Database database;

	public DatabaseStorage(MagicSpells plugin, Database database) {
		super(plugin);
		this.database = database;
	}

	@Override
	public void initialize() {
		database.initialize();
	}

	@Override
	public void load(Spellbook spellbook) {
		database.load(spellbook);
		database.getConnection();
	}

	@Override
	public void save(Spellbook spellbook) {
		database.save(spellbook);
	}

	@Override
	public void disable() {
		database.disable();
	}

}
