package com.nisovin.magicspells.util.itemreader;

import java.util.List;
import java.util.ArrayList;

import org.bukkit.potion.PotionEffect;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SuspiciousStewMeta;
import org.bukkit.configuration.ConfigurationSection;

import com.nisovin.magicspells.util.Util;
import com.nisovin.magicspells.util.magicitems.MagicItemData;
import static com.nisovin.magicspells.util.magicitems.MagicItemData.MagicItemAttribute.POTION_EFFECTS;

public class SuspiciousStewHandler {

	private static final String CONFIG_NAME = POTION_EFFECTS.toString();

	public static void process(ConfigurationSection config, ItemMeta meta, MagicItemData data) {
		if (!(meta instanceof SuspiciousStewMeta)) return;
		if (!config.isList(CONFIG_NAME)) return;

		SuspiciousStewMeta stewMeta = (SuspiciousStewMeta) meta;
		stewMeta.clearCustomEffects();

		List<String> effects = config.getStringList(CONFIG_NAME);
		List<PotionEffect> potionEffects = new ArrayList<>();
		for (String str : effects) {
			PotionEffect potionEffect = Util.buildSuspiciousStewPotionEffect(str);
			if (potionEffect == null) continue;

			stewMeta.addCustomEffect(potionEffect, true);
			potionEffects.add(potionEffect);
		}
		if (!potionEffects.isEmpty()) data.setAttribute(POTION_EFFECTS, potionEffects);
	}

	public static void processItemMeta(ItemMeta meta, MagicItemData data) {
		if (!(meta instanceof SuspiciousStewMeta)) return;

		SuspiciousStewMeta stewMeta = (SuspiciousStewMeta) meta;
		if (data.hasAttribute(POTION_EFFECTS)) {
			stewMeta.clearCustomEffects();
			((List<PotionEffect>) data.getAttribute(POTION_EFFECTS)).forEach(potionEffect -> stewMeta.addCustomEffect(potionEffect, true));
		}
	}

	public static void processMagicItemData(ItemMeta meta, MagicItemData data) {
		if (!(meta instanceof SuspiciousStewMeta)) return;

		SuspiciousStewMeta stewMeta = (SuspiciousStewMeta) meta;
		if (stewMeta.hasCustomEffects()) {
			List<PotionEffect> effects = stewMeta.getCustomEffects();
			if (!effects.isEmpty()) data.setAttribute(POTION_EFFECTS, effects);
		}
	}

}
