package com.nisovin.magicspells.util.itemreader;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.configuration.ConfigurationSection;

import com.nisovin.magicspells.util.magicitems.MagicItemData;
import static com.nisovin.magicspells.util.magicitems.MagicItemData.ItemAttribute.DURABILITY;

public class DurabilityHandler {

	private static final String CONFIG_NAME = DURABILITY.toString();

	public static ItemMeta process(ConfigurationSection config, ItemMeta meta, MagicItemData data) {
		if (!(meta instanceof Damageable)) return meta;
		if (!config.isInt(CONFIG_NAME)) return meta;

		int durability = config.getInt(CONFIG_NAME);
		((Damageable) meta).setDamage(durability);
		data.setItemAttribute(DURABILITY, durability);

		return meta;
	}

	public static ItemMeta process(ItemMeta meta, MagicItemData data) {
		if (!(meta instanceof Damageable)) return meta;
		if (!data.hasItemAttribute(DURABILITY)) return meta;

		int durability = (int) data.getItemAttribute(DURABILITY);
		((Damageable) meta).setDamage(durability);

		return meta;
	}

	public static MagicItemData process(ItemStack itemStack, MagicItemData itemData) {
		if (itemData == null) return null;
		if (itemStack == null) return itemData;
		if (!(itemStack.getItemMeta() instanceof Damageable)) return itemData;

		int damage = ((Damageable) itemStack.getItemMeta()).getDamage();
		itemData.setItemAttribute(DURABILITY, damage);
		
		return itemData;
	}

	public static int getDurability(ItemMeta meta) {
		if (!(meta instanceof Damageable)) return -1;

		return ((Damageable) meta).getDamage();
	}

}
