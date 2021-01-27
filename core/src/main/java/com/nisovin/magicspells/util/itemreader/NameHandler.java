package com.nisovin.magicspells.util.itemreader;

import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.configuration.ConfigurationSection;

import com.nisovin.magicspells.util.Util;
import com.nisovin.magicspells.util.magicitems.MagicItemData;
import static com.nisovin.magicspells.util.magicitems.MagicItemData.MagicItemAttribute.*;

public class NameHandler {

	private static final String CONFIG_NAME = NAME.toString();

	public static void process(ConfigurationSection config, ItemMeta meta, MagicItemData data) {
		if (!config.isString(CONFIG_NAME)) return;

		String name = Util.colorize(config.getString(CONFIG_NAME));
		meta.setDisplayName(name);
		data.setAttribute(NAME, name);
	}

	public static void processItemMeta(ItemMeta meta, MagicItemData data) {
		if (data.hasAttribute(NAME)) meta.setDisplayName((String) data.getAttribute(NAME));
	}

	public static void processMagicItemData(ItemMeta meta, MagicItemData data) {
		if (meta.hasDisplayName()) data.setAttribute(NAME, meta.getDisplayName());
	}
	
}
