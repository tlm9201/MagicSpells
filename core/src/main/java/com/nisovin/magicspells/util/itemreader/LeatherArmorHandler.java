package com.nisovin.magicspells.util.itemreader;

import org.bukkit.Color;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.configuration.ConfigurationSection;

import com.nisovin.magicspells.handlers.DebugHandler;
import com.nisovin.magicspells.util.magicitems.MagicItemData;
import static com.nisovin.magicspells.util.magicitems.MagicItemData.ItemAttribute.COLOR;

public class LeatherArmorHandler {

	private final static String CONFIG_NAME = COLOR.toString();

	public static ItemMeta process(ConfigurationSection config, ItemMeta meta, MagicItemData data) {
		if (!(meta instanceof LeatherArmorMeta)) return meta;
		if (!config.isString(CONFIG_NAME)) return meta;

		LeatherArmorMeta armorMeta = (LeatherArmorMeta) meta;

		try {
			int color = Integer.parseInt(config.getString(CONFIG_NAME).replace("#", ""), 16);
			Color c = Color.fromRGB(color);

			armorMeta.setColor(c);
			if (data != null) data.setItemAttribute(COLOR, c);
		} catch (NumberFormatException e) {
			DebugHandler.debugNumberFormat(e);
		}

		return armorMeta;
	}

	public static ItemMeta process(ItemMeta meta, MagicItemData data) {
		if (!(meta instanceof LeatherArmorMeta)) return meta;

		LeatherArmorMeta armorMeta = (LeatherArmorMeta) meta;
		if (!data.hasItemAttribute(COLOR)) return meta;

		armorMeta.setColor((Color) data.getItemAttribute(COLOR));
		return armorMeta;
	}

	public static MagicItemData process(ItemStack itemStack, MagicItemData data) {
		if (data == null) return null;
		if (itemStack == null) return data;

		ItemMeta meta = itemStack.getItemMeta();
		if (!(meta instanceof LeatherArmorMeta)) return data;

		Color color = ((LeatherArmorMeta) meta).getColor();

		String hex = Integer.toHexString(color.getRed()).toUpperCase() +
				Integer.toHexString(color.getGreen()).toUpperCase() +
				Integer.toHexString(color.getBlue()).toUpperCase();

		// default color is null
		if (!hex.equals("A06540")) data.setItemAttribute(COLOR, color);
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
