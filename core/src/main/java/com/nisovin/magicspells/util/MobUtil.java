package com.nisovin.magicspells.util;

import org.bukkit.Material;
import org.bukkit.entity.Creature;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;

public class MobUtil {

	public static ItemStack getEggItemForEntityType(EntityType type) {
		Material eggMaterial = Material.getMaterial(type.name() + "_SPAWN_EGG");
		if (eggMaterial == null) return null;

		return new ItemStack(eggMaterial);
	}

	public static void setTarget(LivingEntity mob, LivingEntity target) {
		if (!(mob instanceof Creature)) return;
		((Creature) mob).setTarget(target);
	}

}
