package com.nisovin.magicspells.util.itemreader;

import java.util.List;
import java.util.Collections;

import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.configuration.ConfigurationSection;

import com.nisovin.magicspells.util.Util;
import com.nisovin.magicspells.util.magicitems.MagicItemData;
import static com.nisovin.magicspells.util.magicitems.MagicItemData.MagicItemAttribute.LORE;

public class LoreHandler {

	private static final String CONFIG_NAME = LORE.toString();

	public static void process(ConfigurationSection config, ItemMeta meta, MagicItemData data) {
		if (!config.contains(CONFIG_NAME)) return;

		if (config.isList(CONFIG_NAME)) {
			List<String> lore = config.getStringList(CONFIG_NAME);
			for (int i = 0; i < lore.size(); i++) {
				lore.set(i, Util.colorize(lore.get(i)));
			}

			if (!lore.isEmpty()) {
				meta.setLore(lore);
				data.setAttribute(LORE, meta.getLore());
			}
		} else if (config.isString(CONFIG_NAME)) {
			List<String> lore = Collections.singletonList(Util.colorize(config.getString(CONFIG_NAME)));

			meta.setLore(lore);
			data.setAttribute(LORE, lore);
		}
	}

	public static void processItemMeta(ItemMeta meta, MagicItemData data) {
		if (data.hasAttribute(LORE)) meta.setLore((List<String>) data.getAttribute(LORE));
	}
	
}
