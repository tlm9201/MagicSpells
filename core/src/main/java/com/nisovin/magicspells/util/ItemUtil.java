package com.nisovin.magicspells.util;

import com.nisovin.magicspells.MagicSpells;

import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.inventory.ItemStack;

public class ItemUtil {

    public static NamespacedKey FAKE_ENCHANTMENT_KEY = new NamespacedKey(MagicSpells.getInstance(), "fakeenchant");

    public static ItemStack addFakeEnchantment(ItemStack item) {
        item.addEnchantment(Enchantment.getByKey(FAKE_ENCHANTMENT_KEY), 1);
        return item;
    }

    public static class FakeEnchantment extends Enchantment {

        public FakeEnchantment(NamespacedKey key) {
            super(key);
        }

        @Override
        public String getName() {
            return getKey().getKey();
        }

        @Override
        public int getMaxLevel() {
            return 1;
        }

        @Override
        public int getStartLevel() {
            return 1;
        }

        @Override
        public EnchantmentTarget getItemTarget() {
            return EnchantmentTarget.ALL;
        }

        @Override
        public boolean isTreasure() {
            return false;
        }

        @Override
        public boolean isCursed() {
            return false;
        }

        @Override
        public boolean conflictsWith(Enchantment enchantment) {
            return false;
        }

        @Override
        public boolean canEnchantItem(ItemStack itemStack) {
            return true;
        }
    }
}
