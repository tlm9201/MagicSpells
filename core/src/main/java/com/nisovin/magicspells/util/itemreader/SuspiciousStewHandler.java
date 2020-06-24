package com.nisovin.magicspells.util.itemreader;

import java.util.List;
import java.util.ArrayList;

import org.bukkit.potion.PotionEffect;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SuspiciousStewMeta;
import org.bukkit.configuration.ConfigurationSection;

import com.nisovin.magicspells.util.Util;
import com.nisovin.magicspells.util.magicitems.MagicItemData;

public class SuspiciousStewHandler {

	private static final String CONFIG_NAME = "potion-effects";

	public static ItemMeta process(ConfigurationSection config, ItemMeta meta, MagicItemData data) {
		if (!config.contains(CONFIG_NAME)) return meta;
		if (!config.isList(CONFIG_NAME)) return meta;
		if (!(meta instanceof SuspiciousStewMeta)) return meta;

		SuspiciousStewMeta stewMeta = (SuspiciousStewMeta) meta;
		stewMeta.clearCustomEffects();

		List<String> effects = config.getStringList(CONFIG_NAME);
		for (String str : effects) {

			PotionEffect potionEffect = Util.buildSuspiciousStewPotionEffect(str);
			if (potionEffect == null) continue;

			stewMeta.addCustomEffect(potionEffect, true);
			if (data != null) {
				if (data.getPotionEffects() == null) data.setPotionEffects(new ArrayList<>());
				data.getPotionEffects().add(potionEffect);
			}
		}

		return meta;
	}

	public static ItemMeta process(ItemMeta meta, MagicItemData data) {
		if (data == null) return meta;
		if (!(meta instanceof SuspiciousStewMeta)) return meta;

		SuspiciousStewMeta stewMeta = (SuspiciousStewMeta) meta;
		if (data.getPotionEffects() != null) {
			stewMeta.clearCustomEffects();
			data.getPotionEffects().forEach(potionEffect -> stewMeta.addCustomEffect(potionEffect, true));
		}

		return meta;
	}

	public static MagicItemData process(ItemStack itemStack, MagicItemData data) {
		if (data == null) return null;
		if (itemStack == null) return data;
		if (!(itemStack.getItemMeta() instanceof SuspiciousStewMeta)) return data;

		SuspiciousStewMeta meta = (SuspiciousStewMeta) itemStack.getItemMeta();
		data.setPotionEffects(meta.getCustomEffects());
		return data;
	}

}
