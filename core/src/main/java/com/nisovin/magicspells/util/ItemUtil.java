package com.nisovin.magicspells.util;

import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.meta.Damageable;

public class ItemUtil {

	public static void addFakeEnchantment(ItemMeta meta) {
		if (meta == null) return;
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		meta.addEnchant(Enchantment.FROST_WALKER, -1, true);
	}

	public static boolean hasFakeEnchantment(ItemMeta meta) {
		return meta.hasItemFlag(ItemFlag.HIDE_ENCHANTS)
			&& meta.hasEnchant(Enchantment.FROST_WALKER)
			&& meta.getEnchantLevel(Enchantment.FROST_WALKER) == 65535;
	}

	public static int getDurability(ItemStack item) {
		return item.getItemMeta() instanceof Damageable damageable ? damageable.getDamage() : 0;
	}

	public static int getCustomModelData(ItemMeta meta) {
		if (meta == null) return 0;
		if (meta.hasCustomModelData()) return meta.getCustomModelData();
		return 0;
	}

}
