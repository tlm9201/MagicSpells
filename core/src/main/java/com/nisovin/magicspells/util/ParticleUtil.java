package com.nisovin.magicspells.util;

import java.util.Map;
import java.util.HashMap;

import org.bukkit.Particle;
import org.bukkit.Registry;
import org.bukkit.NamespacedKey;

public enum ParticleUtil {

	EXPLOSION_NORMAL("explode"),
	EXPLOSION_LARGE("largeexplode"),
	EXPLOSION_HUGE("hugeexplosion"),
	WATER_WAKE("wake"),
	WATER_DROP("droplet"),
	SPELL_INSTANT("instantspell"),
	SPELL_MOB("mobspell"),
	SPELL_MOB_AMBIENT("mobspellambient"),
	SPELL_WITCH("witchmagic"),
	ITEM_CRACK("iconcrack"),
	BLOCK_CRACK("blockcrack"),
	BLOCK_DUST("blockdust"),
	SMOKE_LARGE("largesmoke"),
	DRIP_WATER("dripwater"),
	DRIP_LAVA("driplava"),
	VILLAGER_ANGRY("angryvillager"),
	VILLAGER_HAPPY("happyvillager"),
	FIREWORKS_SPARK("fireworksspark"),
	SUSPENDED_DEPTH("depthsuspend"),
	CRIT_MAGIC("magiccrit"),
	TOWN_AURA("townaura"),
	ENCHANTMENT_TABLE("enchantmenttable"),
	REDSTONE("reddust"),
	SNOWBALL("snowballpoof"),
	MOB_APPEARANCE("mobappearance"),
	SNOW_SHOVEL("snowshovel"),
	DRAGON_BREATH("dragonbreath"),
	END_ROD("endrod"),
	DAMAGE_INDICATOR("damageindicator"),
	SWEEP_ATTACK("sweepattack"),
	FALLING_DUST("fallingdust");

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

		String lower = particleName.toLowerCase();

		Particle particle = namesToType.get(lower);
		if (particle != null) return particle;

		try {
			return Particle.valueOf(particleName.toUpperCase());
		} catch (IllegalArgumentException ignored) {
		}

		NamespacedKey key = NamespacedKey.fromString(lower);
		return key != null ? Registry.PARTICLE_TYPE.get(key) : null;
	}

}
