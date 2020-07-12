package com.nisovin.magicspells.util;

import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.enchantments.Enchantment;

public class ItemUtil {

	public static ItemStack addFakeEnchantment(ItemStack item) {
		ItemMeta meta = item.getItemMeta();
		if (meta == null) return item;
		item.addUnsafeEnchantment(Enchantment.FROST_WALKER, -1);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		item.setItemMeta(meta);
		return item;
	}

	public static boolean hasDurability(Material type) {
		String name = type.name().toUpperCase();
		return
				name.contains("HELMET") ||
				name.contains("CHESTPLATE") ||
				name.contains("LEGGINGS") ||
				name.contains("BOOTS") ||
				name.contains("PICKAXE") ||
				name.contains("SHOVEL") ||
				name.contains("AXE") ||
				name.contains("HOE") ||
				name.contains("SWORD") ||
				name.contains("FISHING_ROD") ||
				name.contains("CARROT_ON_A_STICK") ||
				name.contains("FLINT_AND_STEEL") ||
				name.contains("BOW") ||
				name.contains("CROSSBOW") ||
				name.contains("TRIDENT") ||
				name.contains("ELYTRA") ||
				name.contains("SHIELD") ||
				name.contains("WARPED_FUNGUS_ON_A_STICK") ||
				name.contains("SHEARS");
	}

}
