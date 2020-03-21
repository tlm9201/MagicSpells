package com.nisovin.magicspells.util.itemreader;

import java.util.ArrayList;
import java.util.List;

import com.nisovin.magicspells.util.Util;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.configuration.ConfigurationSection;

public class LoreHandler {

	public static ItemMeta process(ConfigurationSection config, ItemMeta meta) {
		if (!config.contains("lore")) return meta;
		if (config.isList("lore")) {
			List<String> lore = config.getStringList("lore");
			for (int i = 0; i < lore.size(); i++) {
				lore.set(i, Util.colorize(lore.get(i)));
			}
			meta.setLore(lore);
		} else if (config.isString("lore")) {
			List<String> lore = new ArrayList<>();
			lore.add(Util.colorize(config.getString("lore")));
			meta.setLore(lore);
		}
		return meta;
	}
	
}
