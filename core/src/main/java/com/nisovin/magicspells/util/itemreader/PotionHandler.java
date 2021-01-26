package com.nisovin.magicspells.util.itemreader;

import java.util.List;
import java.util.ArrayList;

import org.bukkit.Color;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.configuration.ConfigurationSection;

import com.nisovin.magicspells.util.Util;
import com.nisovin.magicspells.handlers.DebugHandler;
import com.nisovin.magicspells.util.magicitems.MagicItemData;
import static com.nisovin.magicspells.util.magicitems.MagicItemData.MagicItemAttribute.COLOR;
import static com.nisovin.magicspells.util.magicitems.MagicItemData.MagicItemAttribute.POTION_TYPE;
import static com.nisovin.magicspells.util.magicitems.MagicItemData.MagicItemAttribute.POTION_EFFECTS;

public class PotionHandler {

	public static final String POTION_EFFECT_CONFIG_NAME = POTION_EFFECTS.toString();
	public static final String POTION_TYPE_CONFIG_NAME = POTION_TYPE.toString();
	public static final String POTION_COLOR_CONFIG_NAME = "potion-color";

	public static void process(ConfigurationSection config, ItemMeta meta, MagicItemData data) {
		if (!(meta instanceof PotionMeta)) return;
		
		PotionMeta potionMeta = (PotionMeta) meta;
		
		if (config.isList(POTION_EFFECT_CONFIG_NAME)) {
			potionMeta.clearCustomEffects();
			List<String> potionEffectStrings = config.getStringList(POTION_EFFECT_CONFIG_NAME);
			List<PotionEffect> potionEffects = new ArrayList<>();

			for (String potionEffect : potionEffectStrings) {

				PotionEffect eff = Util.buildPotionEffect(potionEffect);
				if (eff == null) continue;

				potionMeta.addCustomEffect(eff, true);
				potionEffects.add(eff);
			}

			data.setAttribute(POTION_EFFECTS, potionEffects);
		}

		if (config.isString(POTION_COLOR_CONFIG_NAME)) {
			try {
				int color = Integer.parseInt(config.getString(POTION_COLOR_CONFIG_NAME).replace("#", ""), 16);
				Color c = Color.fromRGB(color);

				potionMeta.setColor(c);
				data.setAttribute(COLOR, c);
			} catch (NumberFormatException e) {
				DebugHandler.debugNumberFormat(e);
			}
		}


		if (config.isString(POTION_TYPE_CONFIG_NAME)) {
			String potionTypeString = config.getString(POTION_TYPE_CONFIG_NAME).toUpperCase();
			try {
				PotionType potionType = PotionType.valueOf(potionTypeString);
				PotionData potionData = new PotionData(potionType);

				potionMeta.setBasePotionData(potionData);
				data.setAttribute(POTION_TYPE, potionType);
			} catch (IllegalArgumentException e) {
				DebugHandler.debugBadEnumValue(PotionType.class, potionTypeString);
			}
		}
	}

	public static void processItemMeta(ItemMeta meta, MagicItemData data) {
		if (!(meta instanceof PotionMeta)) return;

		PotionMeta potionMeta = (PotionMeta) meta;
		if (data.hasAttribute(POTION_EFFECTS)) {
			potionMeta.clearCustomEffects();
			((List<PotionEffect>) data.getAttribute(POTION_EFFECTS)).forEach(potionEffect -> potionMeta.addCustomEffect(potionEffect, true));
		}
		if (data.hasAttribute(COLOR)) potionMeta.setColor((Color) data.getAttribute(COLOR));
		if (data.hasAttribute(POTION_TYPE)) potionMeta.setBasePotionData(new PotionData((PotionType) data.getAttribute(POTION_TYPE)));
	}

	public static void processMagicItemData(ItemMeta meta, MagicItemData data) {
		if (!(meta instanceof PotionMeta)) return;

		PotionMeta potionMeta = (PotionMeta) meta;
		data.setAttribute(POTION_TYPE, potionMeta.getBasePotionData().getType());
		data.setAttribute(POTION_EFFECTS, potionMeta.getCustomEffects());
		data.setAttribute(COLOR, potionMeta.getColor());
	}

	public static PotionType getPotionType(ItemMeta meta) {
		if (!(meta instanceof PotionMeta)) return null;

		PotionMeta potionMeta = (PotionMeta) meta;
		return potionMeta.getBasePotionData().getType();
	}
	
}
