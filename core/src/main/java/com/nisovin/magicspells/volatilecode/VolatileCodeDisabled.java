package com.nisovin.magicspells.volatilecode;

import org.bukkit.entity.*;
import org.bukkit.Location;
import org.bukkit.util.Vector;
import org.bukkit.EntityEffect;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;

import org.jetbrains.annotations.NotNull;

public class VolatileCodeDisabled extends VolatileCodeHandle {

	public VolatileCodeDisabled() {
		super(null);
	}

	@Override
	public void addPotionGraphicalEffect(LivingEntity entity, int color, long duration) {

	}

	@Override
	public void sendFakeSlotUpdate(Player player, int slot, ItemStack item) {

	}

	@Override
	public boolean simulateTnt(Location target, LivingEntity source, float explosionSize, boolean fire) {
		return false;
	}

	@Override
	public void playDragonDeathEffect(Location location) {

	}

	@Override
	public void setClientVelocity(Player player, Vector velocity) {

	}

	@Override
	public void startAutoSpinAttack(Player player, int ticks) {

	}

	@Override
	public void playHurtAnimation(LivingEntity entity, float yaw) {
		entity.playEffect(EntityEffect.HURT);
	}

	@Override
	public Recipe createSmithingRecipe(@NotNull NamespacedKey namespacedKey, @NotNull ItemStack result, @NotNull RecipeChoice template, @NotNull RecipeChoice base, @NotNull RecipeChoice addition, boolean copyNbt) {
		return null;
	}

}
