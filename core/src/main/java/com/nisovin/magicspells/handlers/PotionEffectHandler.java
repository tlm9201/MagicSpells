package com.nisovin.magicspells.handlers;

import java.util.Map;
import java.util.HashMap;

import org.bukkit.potion.PotionType;
import org.bukkit.potion.PotionEffectType;

public enum PotionEffectHandler {

	SPEED("speed"),
	SLOW("slowness"),
	FAST_DIGGING("haste"),
	SLOW_DIGGING("mining_fatigue"),
	INCREASE_DAMAGE("strength"),
	HEAL("instant_health"),
	HARM("instant_damage"),
	JUMP("jump_boost"),
	CONFUSION("nausea"),
	REGENERATION("regeneration"),
	DAMAGE_RESISTANCE("resistance"),
	DARKNESS("darkness"),
	FIRE_RESISTANCE("fire_resistance"),
	WATER_BREATHING("water_breathing"),
	INVISIBILITY("invisibility"),
	BLINDNESS("blindness"),
	NIGHT_VISION("night_vision"),
	HUNGER( "hunger"),
	WEAKNESS("weakness"),
	POISON("poison"),
	WITHER("wither"),
	HEALTH_BOOST("health_boost"),
	ABSORPTION("absorption"),
	SATURATION("saturation"),
	GLOWING("glowing"),
	LEVITATION("levitation"),
	LUCK("luck"),
	UNLUCK("unluck"),
	SLOW_FALLING("slow_falling"),
	CONDUIT_POWER("conduit_power"),
	DOLPHINS_GRACE("dolphins_grace"),
	BAD_OMEN("bad_omen"),
	HERO_OF_THE_VILLAGE("hero_of_the_village");

	private String[] names;

	PotionEffectHandler(String... names) {
		this.names = names;
	}

	private static Map<String, PotionEffectType> namesToType = null;
	private static boolean initialized = false;

	private static void initialize() {
		if (initialized) return;

		namesToType = new HashMap<>();

		for (PotionEffectHandler potionEffect : PotionEffectHandler.values()) {
			PotionEffectType type = PotionEffectType.getByName(potionEffect.name());
			if (type == null) continue;

			// handle the names
			namesToType.put(potionEffect.name().toLowerCase(), type);
			for (String s: potionEffect.names) {
				namesToType.put(s.toLowerCase(), type);
			}
		}

		initialized = true;
	}

	public static PotionEffectType getPotionEffectType(String identification) {
		initialize();
		PotionEffectType potion = namesToType.get(identification.toLowerCase());
		if (potion != null) return potion;
		// Also check normal potion effect by name so this class doesn't need to be updated every time a new effect is added
		return PotionEffectType.getByName(identification);
	}

	public static PotionType getPotionType(String name) {
		try {
			return PotionType.valueOf(name.toUpperCase());
		} catch (IllegalArgumentException e) {
			DebugHandler.debugIllegalArgumentException(e);
		}
		return null;
	}

}
