package com.nisovin.magicspells.spelleffects.effecttypes;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.configuration.ConfigurationSection;

import com.nisovin.magicspells.util.TimeUtil;
import com.nisovin.magicspells.util.SpellData;
import com.nisovin.magicspells.util.config.ConfigData;
import com.nisovin.magicspells.spelleffects.SpellEffect;
import com.nisovin.magicspells.util.magicitems.MagicItem;
import com.nisovin.magicspells.util.config.ConfigDataUtil;
import com.nisovin.magicspells.util.magicitems.MagicItems;

public class ItemCooldownEffect extends SpellEffect {

	private ItemStack item;

	private ConfigData<Integer> duration;

	@Override
	protected void loadFromConfig(ConfigurationSection config) {
		MagicItem magicItem = MagicItems.getMagicItemFromString(config.getString("item", "stone"));
		if (magicItem != null) item = magicItem.getItemStack();

		duration = ConfigDataUtil.getInteger(config, "duration", TimeUtil.TICKS_PER_SECOND);
	}

	@Override
	protected Runnable playEffectEntity(Entity entity, SpellData data) {
		if (item == null || !(entity instanceof Player p)) return null;

		p.setCooldown(item.getType(), duration.get(data));
		return null;
	}

}
