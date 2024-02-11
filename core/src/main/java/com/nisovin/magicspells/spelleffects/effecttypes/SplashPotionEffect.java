package com.nisovin.magicspells.spelleffects.effecttypes;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

import com.nisovin.magicspells.util.Name;
import com.nisovin.magicspells.util.SpellData;
import com.nisovin.magicspells.util.config.ConfigData;
import com.nisovin.magicspells.spelleffects.SpellEffect;
import com.nisovin.magicspells.util.config.ConfigDataUtil;

@Name("splash")
public class SplashPotionEffect extends SpellEffect {

	private ConfigData<Integer> pot;

	@Override
	public void loadFromConfig(ConfigurationSection config) {
		pot = ConfigDataUtil.getInteger(config, "potion", 0);
	}

	@Override
	public Runnable playEffectLocation(Location location, SpellData data) {
		location.getWorld().playEffect(location, Effect.POTION_BREAK, (int) pot.get(data));
		return null;
	}
	
}
