package com.nisovin.magicspells.spelleffects.effecttypes;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.spelleffects.SpellEffect;

public class DragonDeathEffect extends SpellEffect {

	@Override
	protected void loadFromConfig(ConfigurationSection config) {

	}

	@Override
	public Runnable playEffectLocation(Location location) {
		MagicSpells.getVolatileCodeHandler().playDragonDeathEffect(location);
		return null;
	}

}
