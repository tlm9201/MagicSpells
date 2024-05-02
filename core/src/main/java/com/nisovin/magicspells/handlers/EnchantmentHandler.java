package com.nisovin.magicspells.handlers;

import org.jetbrains.annotations.NotNull;

import org.bukkit.Registry;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;

public class EnchantmentHandler {

	public static Enchantment getEnchantment(@NotNull String name) {
		NamespacedKey key = NamespacedKey.fromString(name.toLowerCase());
		return key == null ? null : Registry.ENCHANTMENT.get(key);
	}

}
