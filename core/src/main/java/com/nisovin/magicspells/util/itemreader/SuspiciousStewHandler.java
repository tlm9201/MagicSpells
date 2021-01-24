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
import static com.nisovin.magicspells.util.magicitems.MagicItemData.ItemAttribute.POTION_EFFECTS;

public class SuspiciousStewHandler {

	private static final String CONFIG_NAME = POTION_EFFECTS.toString();

	public static ItemMeta process(ConfigurationSection config, ItemMeta meta, MagicItemData data) {
		if (!(meta instanceof SuspiciousStewMeta)) return meta;
		if (!config.isList(CONFIG_NAME)) return meta;

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
		data.setItemAttribute(POTION_EFFECTS, potionEffects);

		return meta;
	}

	public static ItemMeta process(ItemMeta meta, MagicItemData data) {
		if (!(meta instanceof SuspiciousStewMeta)) return meta;

		SuspiciousStewMeta stewMeta = (SuspiciousStewMeta) meta;
		if (data.hasItemAttribute(POTION_EFFECTS)) {
			stewMeta.clearCustomEffects();
			((List<PotionEffect>) data.getItemAttribute(POTION_EFFECTS)).forEach(potionEffect -> stewMeta.addCustomEffect(potionEffect, true));
		}

		return meta;
	}

	public static MagicItemData process(ItemStack itemStack, MagicItemData data) {
		if (data == null) return null;
		if (itemStack == null) return data;

		ItemMeta meta = itemStack.getItemMeta();
		if (!(meta instanceof SuspiciousStewMeta)) return data;

		data.setItemAttribute(POTION_EFFECTS, ((SuspiciousStewMeta) meta).getCustomEffects());
		return data;
	}

}
