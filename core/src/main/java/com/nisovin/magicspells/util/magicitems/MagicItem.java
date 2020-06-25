package com.nisovin.magicspells.util.magicitems;

import org.bukkit.inventory.ItemStack;

public class MagicItem {

	private ItemStack itemStack;
	private MagicItemData magicItemData;

	public MagicItem(ItemStack itemStack, MagicItemData magicItemData) {
		this.itemStack = itemStack;
		this.magicItemData = magicItemData;
	}

	public ItemStack getItemStack() {
		return itemStack;
	}

	public MagicItemData getMagicItemData() {
		return magicItemData;
	}

}
