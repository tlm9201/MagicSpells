package com.nisovin.magicspells.util.itemreader;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.configuration.ConfigurationSection;

import com.nisovin.magicspells.util.ItemUtil;
import com.nisovin.magicspells.util.magicitems.MagicItemData;
import static com.nisovin.magicspells.util.magicitems.MagicItemData.ItemAttribute.CUSTOM_MODEL_DATA;


public class CustomModelDataHandler {

	private static final String CONFIG_NAME = CUSTOM_MODEL_DATA.toString();

	public static ItemMeta process(ConfigurationSection config, ItemMeta meta, MagicItemData data) {
		if (!config.isInt(CONFIG_NAME)) return meta;

		int customModelData = config.getInt(CONFIG_NAME);

		ItemUtil.setCustomModelData(meta, customModelData);
		data.setItemAttribute(CUSTOM_MODEL_DATA, customModelData);
		return meta;
	}

	public static ItemMeta process(ItemMeta meta, MagicItemData data) {
		if (meta == null) return null;
		if (data == null) return meta;

		if (!data.hasItemAttribute(CUSTOM_MODEL_DATA)) return meta;
		int customModelData = (int) data.getItemAttribute(CUSTOM_MODEL_DATA);
		ItemUtil.setCustomModelData(meta, customModelData);

		return meta;
	}

	public static MagicItemData process(ItemStack itemStack, MagicItemData itemData) {
		if (itemData == null) return null;
		if (itemStack == null) return itemData;

		itemData.setItemAttribute(CUSTOM_MODEL_DATA, ItemUtil.getCustomModelData(itemStack.getItemMeta()));
		return itemData;
	}

}
