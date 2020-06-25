package com.nisovin.magicspells.util.itemreader;

import java.util.List;
import java.util.ArrayList;

import org.bukkit.Color;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.configuration.ConfigurationSection;

import com.nisovin.magicspells.util.Util;
import com.nisovin.magicspells.util.magicitems.MagicItemData;

public class PotionHandler {

	public static final String POTION_EFFECT_CONFIG_NAME = "potion-effects";
	public static final String POTION_COLOR_CONFIG_NAME = "potion-color";
	public static final String POTION_TYPE_CONFIG_NAME = "potion-type";

	public static ItemMeta process(ConfigurationSection config, ItemMeta meta, MagicItemData data) {
		if (!(meta instanceof PotionMeta)) return meta;
		
		PotionMeta potionMeta = (PotionMeta) meta;
		
		if (config.contains(POTION_EFFECT_CONFIG_NAME) && config.isList(POTION_EFFECT_CONFIG_NAME)) {
			potionMeta.clearCustomEffects();
			List<String> potionEffects = config.getStringList(POTION_EFFECT_CONFIG_NAME);

			for (String potionEffect : potionEffects) {

				PotionEffect eff = Util.buildPotionEffect(potionEffect);
				if (eff == null) continue;

				potionMeta.addCustomEffect(eff, true);
				if (data != null) {
					if (data.getPotionEffects() == null) data.setPotionEffects(new ArrayList<>());
					data.getPotionEffects().add(eff);
				}
			}
		}

		if (config.contains(POTION_COLOR_CONFIG_NAME) && config.isSet(POTION_COLOR_CONFIG_NAME)) {
			int color = Integer.parseInt(config.get(POTION_COLOR_CONFIG_NAME).toString().replace("#", ""), 16);
			Color c = Color.fromRGB(color);
			potionMeta.setColor(c);
			if (data != null) data.setColor(c);
		}


		if (config.contains(POTION_TYPE_CONFIG_NAME) && config.isString(POTION_TYPE_CONFIG_NAME)) {
			PotionType potionType = PotionType.valueOf(config.getString(POTION_TYPE_CONFIG_NAME).toUpperCase());
			if (potionType == null) potionType = PotionType.UNCRAFTABLE;

			PotionData potionData = new PotionData(potionType);
			if (potionData == null) return potionMeta;

			potionMeta.setBasePotionData(potionData);
			if (data != null) data.setPotionType(potionType);
		}
		
		return potionMeta;
	}

	public static ItemMeta process(ItemMeta meta, MagicItemData data) {
		if (data == null) return meta;
		if (!(meta instanceof PotionMeta)) return meta;

		PotionMeta potionMeta = (PotionMeta) meta;
		if (data.getPotionEffects() != null) {
			potionMeta.clearCustomEffects();
			data.getPotionEffects().forEach(potionEffect -> potionMeta.addCustomEffect(potionEffect, true));
		}

		if (data.getColor() != null) potionMeta.setColor(data.getColor());

		if (data.getPotionType() != null) potionMeta.setBasePotionData(new PotionData(data.getPotionType()));

		return potionMeta;
	}

	public static MagicItemData process(ItemStack itemStack, MagicItemData data) {
		if (data == null) return null;
		if (itemStack == null) return data;
		if (!(itemStack.getItemMeta() instanceof PotionMeta)) return data;

		PotionMeta meta = (PotionMeta) itemStack.getItemMeta();
		data.setPotionType(meta.getBasePotionData().getType());
		if (!meta.getCustomEffects().isEmpty()) data.setPotionEffects(meta.getCustomEffects());
		data.setColor(meta.getColor());

		return data;
	}

	public static PotionType getPotionType(ItemMeta meta) {
		if (!(meta instanceof PotionMeta)) return null;

		PotionMeta potionMeta = (PotionMeta) meta;
		return potionMeta.getBasePotionData().getType();
	}
	
}
