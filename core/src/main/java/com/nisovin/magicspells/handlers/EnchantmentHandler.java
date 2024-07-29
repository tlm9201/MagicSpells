package com.nisovin.magicspells.handlers;

import org.jetbrains.annotations.NotNull;

import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;

import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.RegistryAccess;

public class EnchantmentHandler {

	public static Enchantment getEnchantment(@NotNull String name) {
		NamespacedKey key = NamespacedKey.fromString(name.toLowerCase());
		return key == null ? null : RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT).get(key);
	}

}
