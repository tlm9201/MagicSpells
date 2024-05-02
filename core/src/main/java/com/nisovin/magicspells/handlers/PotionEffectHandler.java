package com.nisovin.magicspells.handlers;

import java.util.Map;
import java.util.HashMap;

import org.bukkit.Registry;
import org.bukkit.potion.PotionEffectType;

public class PotionEffectHandler {

	private static final Map<String, PotionEffectType> potionEffects = new HashMap<>();

	static {
		// Add bukkit and minecraft potion effect names
		for (PotionEffectType potionEffect : Registry.EFFECT) {
			//noinspection deprecation
			potionEffects.put(potionEffect.getName().toLowerCase(), potionEffect);
			potionEffects.put(potionEffect.key().value(), potionEffect);
		}
	}

	public static PotionEffectType getPotionEffectType(String identification) {
		return potionEffects.get(identification.trim().toLowerCase());
	}

}
