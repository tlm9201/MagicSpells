package com.nisovin.magicspells.spells.targeted.cleanse;

import java.util.List;
import java.util.ArrayList;

import org.jetbrains.annotations.NotNull;

import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffectType;

import com.nisovin.magicspells.handlers.PotionEffectHandler;
import com.nisovin.magicspells.spells.targeted.cleanse.util.Cleanser;

public class PotionCleanser implements Cleanser {

	private final List<PotionEffectType> potions = new ArrayList<>();

	@Override
	public boolean add(@NotNull String string) {
		PotionEffectType potion = PotionEffectHandler.getPotionEffectType(string);
		if (potion == null) return false;
		potions.add(potion);
		return true;
	}

	@Override
	public boolean isAnyActive(@NotNull LivingEntity entity) {
		for (PotionEffectType potion : potions) {
			if (entity.hasPotionEffect(potion)) return true;
		}
		return false;
	}

	@Override
	public void cleanse(@NotNull LivingEntity entity) {
		potions.forEach(entity::removePotionEffect);
	}

}
