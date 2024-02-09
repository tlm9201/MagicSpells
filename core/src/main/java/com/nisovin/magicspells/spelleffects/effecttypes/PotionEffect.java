package com.nisovin.magicspells.spelleffects.effecttypes;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.configuration.ConfigurationSection;

import com.nisovin.magicspells.util.Name;
import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.util.SpellData;
import com.nisovin.magicspells.handlers.DebugHandler;
import com.nisovin.magicspells.util.config.ConfigData;
import com.nisovin.magicspells.spelleffects.SpellEffect;
import com.nisovin.magicspells.util.config.ConfigDataUtil;

@Name("potion")
public class PotionEffect extends SpellEffect {

	private ConfigData<String> color;

	private ConfigData<Integer> duration;

	@Override
	public void loadFromConfig(ConfigurationSection config) {
		color = ConfigDataUtil.getString(config, "color", null);
		duration = ConfigDataUtil.getInteger(config, "duration", 30);
	}

	@Override
	public Runnable playEffectEntity(Entity entity, SpellData data) {
		if (!(entity instanceof LivingEntity le)) return null;

		String color = this.color.get(data);
		if (color == null) return null;

		int c;
		try {
			c = Integer.parseInt(color, 16);
		} catch (NumberFormatException e) {
			DebugHandler.debugNumberFormat(e);
			return null;
		}

		MagicSpells.getVolatileCodeHandler().addPotionGraphicalEffect(le, c, duration.get(data));
		return null;
	}

}
