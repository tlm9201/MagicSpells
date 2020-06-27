package com.nisovin.magicspells.util.itemreader;

import org.bukkit.Color;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.configuration.ConfigurationSection;

import com.nisovin.magicspells.handlers.DebugHandler;
import com.nisovin.magicspells.util.magicitems.MagicItemData;

public class LeatherArmorHandler {

	private final static String CONFIG_NAME = "color";

	public static ItemMeta process(ConfigurationSection config, ItemMeta meta, MagicItemData data) {
		if (!(meta instanceof LeatherArmorMeta)) return meta;
		if (!config.contains(CONFIG_NAME)) return meta;
		if (!config.isSet(CONFIG_NAME)) return meta;

		LeatherArmorMeta armorMeta = (LeatherArmorMeta) meta;

		try {
			int color = Integer.parseInt(config.get(CONFIG_NAME).toString().replace("#", ""), 16);
			armorMeta.setColor(Color.fromRGB(color));
			if (data != null) data.setColor(Color.fromRGB(color));
		} catch (NumberFormatException e) {
			DebugHandler.debugNumberFormat(e);
		}

		return armorMeta;
	}

	public static ItemMeta process(ItemMeta meta, MagicItemData data) {
		if (!(meta instanceof LeatherArmorMeta)) return meta;

		LeatherArmorMeta armorMeta = (LeatherArmorMeta) meta;

		if (data.getColor() == null) return meta;

		armorMeta.setColor(data.getColor());
		return armorMeta;
	}

	public static MagicItemData process(ItemStack itemStack, MagicItemData data) {
		if (data == null) return null;
		if (itemStack == null) return data;
		if (!(itemStack.getItemMeta() instanceof LeatherArmorMeta)) return data;
		LeatherArmorMeta meta = (LeatherArmorMeta) itemStack.getItemMeta();

		Color color = meta.getColor();

		String hex = Integer.toHexString(color.getRed()).toUpperCase() +
				Integer.toHexString(color.getGreen()).toUpperCase() +
				Integer.toHexString(color.getBlue()).toUpperCase();

		// default color is null
		if (!hex.equals("A06540")) data.setColor(meta.getColor());
		return data;
	}

	public static Color getColor(ItemMeta meta) {
		if (!(meta instanceof LeatherArmorMeta)) return null;

		Color color = ((LeatherArmorMeta) meta).getColor();
		String hex = Integer.toHexString(color.getRed()).toUpperCase() +
				Integer.toHexString(color.getGreen()).toUpperCase() +
				Integer.toHexString(color.getBlue()).toUpperCase();

		if (hex.equals("A06540")) return null;
		return color;
	}
	
}
