package com.nisovin.magicspells.util.projectile;

import org.bukkit.Material;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.inventory.ItemStack;

import org.jetbrains.annotations.NotNull;

public class ProjectileManagerThrownPotion extends ProjectileManager {

	private static final ItemStack POTION = new ItemStack(Material.POTION);

	@Override
	public Class<? extends Projectile> getProjectileClass() {
		return ThrownPotion.class;
	}

	@NotNull
	public ItemStack getItem() {
		return POTION;
	}

}
