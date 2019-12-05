package com.nisovin.magicspells.util;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.potion.PotionEffectType;

@Deprecated
public class MagicValues {

	public enum PotionEffect {
		SPEED(1, "swiftness"),
		SLOW(2, "slowness"),
		FAST_DIGGING(3, "haste"),
		SLOW_DIGGING(4, "miningfatigue"),
		INCREASE_DAMAGE(5, "strength"),
		HEAL(6, "health", "instanthealth"),
		HARM(7, "damage"),
		JUMP(8, "jumpboost"),
		CONFUSION(9, "nausea"),
		REGENERATION(10, "regen"),
		DAMAGE_RESISTANCE(11, "resistance"),
		FIRE_RESISTANCE(12, "fireresistance"),
		WATER_BREATHING(13, "waterbreathing"),
		INVISIBILITY(14, "vanish"),
		BLINDNESS(15, "blind"),
		NIGHT_VISION(16, "nightvision"),
		HUNGER(17, "starve", "starving"),
		WEAKNESS(18, "weak"),
		POISON(19),
		WITHER(20),
		HEALTH_BOOST(21, "healthboost"),
		ABSORPTION(22),
		SATURATION(23, "food"),
		GLOWING(24, "glow"),
		LEVITATION(25),
		LUCK(26, "lucky"),
		UNLUCK(27, "badluck", "unlucky"),
		SLOW_FALLING(28, "slowfalling", "slowfall"),
		CONDUIT_POWER(29, "conduit", "conduitpower"),
		DOLPHINS_GRACE(30, "dolphingrace", "dolphinsgrace", "dolphin");

		private int id;
		private String[] names;

		PotionEffect(int id, String... names) {
			this.id = id;
			this.names = names;
		}


		private static Map<String, PotionEffectType> namesToType = null;
		private static Map<PotionEffectType, Integer> potionEffectToId = null;
		private static boolean initialized = false;

		private static void initialize() {
			if (initialized) return;

			namesToType = new HashMap<>();
			potionEffectToId = new HashMap<>();

			for (PotionEffect pe: PotionEffect.values()) {
				PotionEffectType type = PotionEffectType.getByName(pe.name());
				if (type == null) continue;

				// handle the names
				namesToType.put(pe.name().toLowerCase().replace("_", ""), type);
				namesToType.put(pe.id + "", type);
				for (String s: pe.names) {
					namesToType.put(s.toLowerCase().replace("_", ""), type);
				}

				// handle the type to id mappings
				potionEffectToId.put(type, pe.id);
			}

			initialized = true;
		}

		public static PotionEffectType getPotionEffectType(String identification) {
			initialize();
			PotionEffectType potion = namesToType.get(identification.toLowerCase().replace("_", ""));
			if (potion != null)
				return potion;

			// Also check normal potion effect by name so this class doesn't need to be updated
			// every time a new effect is added
			return PotionEffectType.getByName(identification);
		}

		public static int getId(PotionEffectType type) {
			initialize();
			return potionEffectToId.get(type);
		}
	}

	public enum Villager {
		// Profession
	}

	public enum Enchantments {
		PROTECTION_ENVIRONMENTAL(0, "prot", "protection"),
		PROTECTION_FIRE(1, "fireprot" , "fireprotection"),
		PROTECTION_FALL(2, "featherfalling"),
		PROTECTION_EXPLOSIONS(3, "blastprotection", "blastprot"),
		PROTECTION_PROJECTILE(4, "projectileprotection", "projectileprot"),
		OXYGEN(5, "respiration"),
		WATER_WORKER(6, "aquaaffinity"),
		THORNS(7),
		DEPTH_STRIDER(8),
		FROST_WALKER(9),
		BINDING_CURSE(10, "binding"),
		DAMAGE_ALL(16, "sharp", "sharpness"),
		DAMAGE_UNDEAD(17, "smite"),
		DAMAGE_ARTHROPODS(18, "bane", "baneofarthropods", "ardmg"),
		KNOCKBACK(19),
		FIRE_ASPECT(20),
		LOOT_BONUS_MOBS(21, "looting"),
		SWEEPING_EDGE(22, "sweeping"),
		DIG_SPEED(32, "efficiency"),
		SILK_TOUCH(33, "silk"),
		DURABILITY(34, "unbreaking"),
		LOOT_BONUS_BLOCKS(35, "fortune"),
		ARROW_DAMAGE(48, "power"),
		ARROW_KNOCKBACK(49, "punch"),
		ARROW_FIRE(50, "flame"),
		ARROW_INFINITE(51, "infinity"),
		LUCK(61, "luckofthesea"),
		LURE(62),
		MENDING(70),
		VANISHING_CURSE(71, "vanishing");

		private String[] names;
		private int id;

		Enchantments(int id, String... labels) {
			this.id = id;
			this.names = labels;
		}
		private static Map<String, Enchantment> namesToType = null;
		private static Map<Enchantment, Integer> enchantToId = null;
		private static boolean initialized = false;

		private static void initialize() {
			if (initialized) return;

			namesToType = new HashMap<>();
			enchantToId = new HashMap<>();

			for (Enchantments pe: Enchantments.values()) {
				Enchantment type = Enchantment.getByName(pe.name());
				if (type == null) continue;

				// deal with the names
				namesToType.put(pe.name().toLowerCase().replace("_", ""), type);
				namesToType.put(pe.id + "", type);
				for (String s: pe.names) {
					namesToType.put(s.toLowerCase().replace("_", ""), type);
				}

				// deal with the enchant to id
				enchantToId.put(type, pe.id);
			}

			initialized = true;
		}

		public static Enchantment getEnchantmentType(String identification) {
			initialize();
			Enchantment enchant = namesToType.get(identification.toLowerCase().replace("_", ""));
			if (enchant != null)
				return enchant;

			// Also check normal enchant class by identifier so this class doesn't need to be updated
			// every time a new enchant is added
			return Enchantment.getByKey(NamespacedKey.minecraft(identification.toLowerCase()));
		}

		public static int getId(Enchantment enchant) {
			initialize();
			return enchantToId.get(enchant);
		}
	}

	public enum Materials {
		//TODO map the materials to ids and such
	}

	public enum Projectile {
		;



		private Projectile() {

		}
	}

}
