package com.nisovin.magicspells.util.itemreader;

import java.util.List;
import java.util.ArrayList;

import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.configuration.ConfigurationSection;

import com.nisovin.magicspells.util.Util;
import com.nisovin.magicspells.util.magicitems.MagicItemData;

public class LoreHandler {

	private static final String CONFIG_NAME = "lore";

	public static ItemMeta process(ConfigurationSection config, ItemMeta meta, MagicItemData data) {
		if (!config.contains(CONFIG_NAME)) return meta;
		if (config.isList(CONFIG_NAME)) {

			List<String> lore = config.getStringList(CONFIG_NAME);
			for (int i = 0; i < lore.size(); i++) {
				lore.set(i, Util.colorize(lore.get(i)));
			}

			meta.setLore(lore);
			if (data != null) data.setLore(lore);
		} else if (config.isString(CONFIG_NAME)) {
			List<String> lore = new ArrayList<>();
			lore.add(Util.colorize(config.getString(CONFIG_NAME)));
			meta.setLore(lore);
			if (data != null) data.setLore(lore);
		}
		return meta;
	}

	public static ItemMeta process(ItemMeta meta, MagicItemData data) {
		if (meta == null) return null;
		if (data == null) return meta;
		if (data.getLore() == null) return meta;

		List<String> lore = data.getLore();
		for (int i = 0; i < lore.size(); i++) {
			lore.set(i, Util.colorize(lore.get(i)));
		}

		meta.setLore(lore);
		return meta;
	}
	
}
