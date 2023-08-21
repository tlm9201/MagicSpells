package com.nisovin.magicspells.util;

import java.util.Map;
import java.util.HashMap;

import org.bukkit.Particle;

public enum ParticleUtil {

	EXPLOSION_NORMAL("poof", "explode"),
	EXPLOSION_LARGE("explosion", "largeexplode"),
	EXPLOSION_HUGE("explosion_emitter", "hugeexplosion"),
	WATER_BUBBLE("bubble"),
	WATER_SPLASH("splash"),
	WATER_WAKE("fishing", "wake"),
	WATER_DROP("rain", "droplet"),
	SPELL("effect"),
	SPELL_INSTANT("instant_effect", "instantspell"),
	SPELL_MOB("entity_effect", "mobspell"),
	SPELL_MOB_AMBIENT("ambient_entity_effect", "mobspellambient"),
	SPELL_WITCH("witch", "witchmagic"),
	ITEM_CRACK("item", "iconcrack"),
	BLOCK_CRACK("blockcrack"),
	BLOCK_DUST("blockdust", "block"),
	SMOKE_NORMAL("smoke"),
	SMOKE_LARGE("large_smoke", "largesmoke"),
	DRIP_WATER("dripping_water", "dripwater"),
	DRIP_LAVA("dripping_lava", "driplava"),
	VILLAGER_ANGRY("angry_villager", "angryvillager"),
	VILLAGER_HAPPY("happy_villager", "happyvillager"),
	FIREWORKS_SPARK("firework", "fireworksspark"),
	SUSPENDED("underwater"),
	SUSPENDED_DEPTH("depthsuspend"),
	CRIT_MAGIC("enchanted_hit", "magiccrit"),
	TOWN_AURA("mycelium", "townaura"),
	ENCHANTMENT_TABLE("enchant", "enchantmenttable"),
	REDSTONE("dust", "reddust"),
	SNOWBALL("item_snowball", "snowballpoof"),
	SLIME("item_slime"),
	MOB_APPEARANCE("elder_guardian", "mobappearance"),
	SNOW_SHOVEL("snowshovel"),
	DRAGON_BREATH("dragonbreath"),
	END_ROD("endrod"),
	DAMAGE_INDICATOR("damageindicator"),
	SWEEP_ATTACK("sweepattack"),
	FALLING_DUST("fallingdust"),
	TOTEM("totem_of_undying")
	;

	private static final Map<String, Particle> namesToType = new HashMap<>();
	private static boolean initialized = false;

	private final String[] names;

	ParticleUtil(String... names) {
		this.names = names;
	}

	private static void initialize() {
		if (initialized) return;

		for (ParticleUtil p : ParticleUtil.values()) {
			Particle particle = null;

			try {
				particle = Particle.valueOf(p.name());
			} catch (Exception e) {
				// ignored
			}

			if (particle == null) continue;

			// handle the names
			namesToType.put(p.name().toLowerCase(), particle);
			for (String s : p.names) {
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
