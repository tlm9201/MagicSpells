package com.nisovin.magicspells.util;

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

}
