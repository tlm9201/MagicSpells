package com.nisovin.magicspells.util;

import java.util.Map;
import java.util.HashMap;

import org.bukkit.Particle;

public class ParticleUtil {

	public enum ParticleEffect {

		EXPLOSION_NORMAL("explosion_normal", "poof"),
		EXPLOSION_LARGE("explosion_large", "explosion"),
		EXPLOSION_HUGE("explosion_huge", "explosion_emitter"),
		WATER_BUBBLE("water_bubble", "bubble"),
		WATER_SPLASH("water_splash", "splash"),
		WATER_WAKE("water_wake", "fishing"),
		WATER_DROP("water_drop", "rain"),
		SPELL("spell", "effect"),
		SPELL_INSTANT("spell_instant", "instant_effect"),
		SPELL_MOB("spell_mob", "entity_effect"),
		SPELL_MOB_AMBIENT("spell_mob_ambient", "ambient_entity_effect"),
		SPELL_WITCH("spell_witch", "witch"),
		ITEM_CRACK("item_crack", "item"),
		BLOCK_CRACK("block_crack", "blockcrack"),
		BLOCK_DUST("block_dust", "blockdust", "block"),
		SMOKE_NORMAL("smoke_normal", "smoke"),
		SMOKE_LARGE("smoke_large", "large_smoke"),
		DRIP_WATER("drip_water", "dripping_water"),
		DRIP_LAVA("drip_lava", "dripping_lava"),
		VILLAGER_ANGRY("villager_angry", "angry_villager"),
		VILLAGER_HAPPY("villager_happy", "happy_villager"),
		FIREWORKS_SPARK("fireworks_spark", "firework"),
		SUSPENDED("suspended", "underwater"),
		CRIT_MAGIC("crit_magic", "enchanted_hit"),
		TOWN_AURA("town_aura", "mycelium"),
		ENCHANTMENT_TABLE("enchantment_table", "enchant"),
		REDSTONE("redstone", "dust"),
		SNOWBALL("snowball", "item_snowball"),
		SLIME("slime", "item_slime"),
		MOB_APPEARANCE("mob_appearance", "elder_guardian"),
		;

		private final String[] names;

		ParticleEffect(String... names) {
			this.names = names;
		}

		private static final Map<String, Particle> namesToType = new HashMap<>();
		private static boolean initialized = false;

		private static void initialize() {
			if (initialized) return;

			for (ParticleEffect pe : ParticleEffect.values()) {
				Particle particle = null;

				try {
					particle = Particle.valueOf(pe.name());
				} catch (Exception e) {
					// ignored
				}

				if (particle == null) continue;

				// handle the names
				namesToType.put(pe.name().toLowerCase(), particle);
				for (String s : pe.names) {
					namesToType.put(s.toLowerCase(), particle);
				}

			}

			initialized = true;
		}

		public static Particle getParticle(String particleName) {
			initialize();

			Particle particle = namesToType.get(particleName.toLowerCase());
			if (particle != null) return particle;

			try {
				particle = Particle.valueOf(particleName.toUpperCase());
			} catch (IllegalArgumentException ignored) {}

			return particle;
		}

	}

}
