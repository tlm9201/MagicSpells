package com.nisovin.magicspells.util.itemreader;

import com.nisovin.magicspells.util.Util;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.configuration.ConfigurationSection;

public class NameHandler {

	public static ItemMeta process(ConfigurationSection config, ItemMeta meta) {
		if (!config.contains("name") || !config.isString("name")) return meta;
		meta.setDisplayName(Util.colorize(config.getString("name")));
		return meta;
	}
	
}
