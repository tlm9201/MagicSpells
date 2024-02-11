package com.nisovin.magicspells.spelleffects.effecttypes;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

import com.nisovin.magicspells.util.Name;
import com.nisovin.magicspells.util.SpellData;
import com.nisovin.magicspells.spelleffects.SpellEffect;

@Name("explosion")
public class ExplosionEffect extends SpellEffect {

	@Override
	public void loadFromConfig(ConfigurationSection config) {
		// TODO make a config loading schema
	}

	@Override
	public Runnable playEffectLocation(Location location, SpellData data) {
		location.getWorld().createExplosion(location, 0F);
		return null;
	}

}
