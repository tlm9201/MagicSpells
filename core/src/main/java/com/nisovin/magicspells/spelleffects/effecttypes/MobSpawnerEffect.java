package com.nisovin.magicspells.spelleffects.effecttypes;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

import com.nisovin.magicspells.util.Name;
import com.nisovin.magicspells.util.SpellData;
import com.nisovin.magicspells.spelleffects.SpellEffect;

@Name("spawn")
public class MobSpawnerEffect extends SpellEffect {

	@Override
	public void loadFromConfig(ConfigurationSection config) {
		//TODO make a config schema for this effect
	}

	@Override
	public Runnable playEffectLocation(Location location, SpellData data) {
		location.getWorld().playEffect(location, Effect.MOBSPAWNER_FLAMES, 0);
		return null;
	}
	
}
