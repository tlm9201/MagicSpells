package com.nisovin.magicspells.util.itemreader;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.configuration.ConfigurationSection;

import com.nisovin.magicspells.util.magicitems.MagicItemData;

public class DurabilityHandler {

	private static final String CONFIG_NAME = "durability";

	public static ItemMeta process(ConfigurationSection config, ItemMeta meta, MagicItemData data) {
		if (!(meta instanceof Damageable)) return meta;
		Damageable damageableMeta = (Damageable) meta;
		data.setDurability(damageableMeta.getDamage());

		if (!config.contains(CONFIG_NAME)) return meta;
		if (!config.isInt(CONFIG_NAME)) return meta;

		int durability = config.getInt(CONFIG_NAME);

		damageableMeta.setDamage(durability);
		data.setDurability(durability);
		return meta;
	}

	public static ItemMeta process(ItemMeta meta, MagicItemData data) {
		if (data == null) return meta;
		if (!(meta instanceof Damageable)) return meta;

		int durability = data.getDurability();

		((Damageable) meta).setDamage(durability);
		return meta;
	}

	public static MagicItemData process(ItemStack itemStack, MagicItemData itemData) {
		if (itemData == null) return null;
		if (itemStack == null) return itemData;
		if (!(itemStack.getItemMeta() instanceof Damageable)) return itemData;

		int damage = ((Damageable) itemStack.getItemMeta()).getDamage();
		itemData.setDurability(damage);
		
		return itemData;
	}

	public static int getDurability(ItemMeta meta) {
		if (!(meta instanceof Damageable)) return -1;

		return ((Damageable) meta).getDamage();
	}

}
