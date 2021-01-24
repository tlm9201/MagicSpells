package com.nisovin.magicspells.util.itemreader;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.configuration.ConfigurationSection;

import com.nisovin.magicspells.util.Util;
import com.nisovin.magicspells.util.magicitems.MagicItemData;
import static com.nisovin.magicspells.util.magicitems.MagicItemData.ItemAttribute.*;

public class NameHandler {

	private static final String CONFIG_NAME = NAME.toString();

	public static ItemMeta process(ConfigurationSection config, ItemMeta meta, MagicItemData data) {
		if (!config.isString(CONFIG_NAME)) return meta;

		String name = config.getString(CONFIG_NAME);
		meta.setDisplayName(Util.colorize(name));
		data.setItemAttribute(NAME, Util.decolorize(name));

		return meta;
	}

	public static ItemMeta process(ItemMeta meta, MagicItemData data) {
		if (meta == null) return null;
		if (data == null) return meta;
		if (!data.hasItemAttribute(NAME)) return meta;

		meta.setDisplayName(Util.colorize((String) data.getItemAttribute(NAME)));

		return meta;
	}

	public static MagicItemData process(ItemStack itemStack, MagicItemData itemData) {
		if (itemData == null) return null;
		if (itemStack == null) return itemData;

		ItemMeta meta = itemStack.getItemMeta();
		if (meta == null) return itemData;

		if (!meta.getDisplayName().isEmpty()) itemData.setItemAttribute(NAME, Util.decolorize(meta.getDisplayName()));
		return itemData;

	}
	
}
