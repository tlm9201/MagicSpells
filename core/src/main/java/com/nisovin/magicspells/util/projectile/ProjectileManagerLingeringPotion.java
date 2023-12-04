package com.nisovin.magicspells.util.projectile;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import org.jetbrains.annotations.NotNull;

public class ProjectileManagerLingeringPotion extends ProjectileManagerThrownPotion {

	private static final ItemStack POTION = new ItemStack(Material.LINGERING_POTION);

	@NotNull
	@Override
	public ItemStack getItem() {
		return POTION;
	}

}
