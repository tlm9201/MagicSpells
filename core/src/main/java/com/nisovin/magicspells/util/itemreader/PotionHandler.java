package com.nisovin.magicspells.util.itemreader;

import java.util.List;
import java.util.ArrayList;
import java.util.Locale;

import com.nisovin.magicspells.handlers.DebugHandler;
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

import static com.nisovin.magicspells.util.magicitems.MagicItemData.ItemAttribute.POTION_EFFECTS;
import static com.nisovin.magicspells.util.magicitems.MagicItemData.ItemAttribute.POTION_TYPE;
import static com.nisovin.magicspells.util.magicitems.MagicItemData.ItemAttribute.COLOR;

public class PotionHandler {

	public static final String POTION_EFFECT_CONFIG_NAME = POTION_EFFECTS.toString();
	public static final String POTION_TYPE_CONFIG_NAME = POTION_TYPE.toString();
	public static final String POTION_COLOR_CONFIG_NAME = "potion-color";

	public static ItemMeta process(ConfigurationSection config, ItemMeta meta, MagicItemData data) {
		if (!(meta instanceof PotionMeta)) return meta;
		
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

			data.setItemAttribute(POTION_EFFECTS, potionEffects);
		}

		if (config.isString(POTION_COLOR_CONFIG_NAME)) {
			try {
				int color = Integer.parseInt(config.getString(POTION_COLOR_CONFIG_NAME).replace("#", ""), 16);
				Color c = Color.fromRGB(color);

				potionMeta.setColor(c);
				data.setItemAttribute(COLOR, c);
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
				data.setItemAttribute(POTION_TYPE, potionType);
			} catch (IllegalArgumentException e) {
				DebugHandler.debugBadEnumValue(PotionType.class, potionTypeString);
			}
		}
		
		return potionMeta;
	}

	public static ItemMeta process(ItemMeta meta, MagicItemData data) {
		if (!(meta instanceof PotionMeta)) return meta;

		PotionMeta potionMeta = (PotionMeta) meta;
		if (data.hasItemAttribute(POTION_EFFECTS)) {
			potionMeta.clearCustomEffects();
			((List<PotionEffect>) data.getItemAttribute(POTION_EFFECTS)).forEach(potionEffect -> potionMeta.addCustomEffect(potionEffect, true));
		}
		if (data.hasItemAttribute(COLOR)) potionMeta.setColor((Color) data.getItemAttribute(COLOR));
		if (data.hasItemAttribute(POTION_TYPE)) potionMeta.setBasePotionData(new PotionData((PotionType) data.getItemAttribute(POTION_TYPE)));

		return potionMeta;
	}

	public static MagicItemData process(ItemStack itemStack, MagicItemData data) {
		if (data == null) return null;
		if (itemStack == null) return data;
		if (!(itemStack.getItemMeta() instanceof PotionMeta)) return data;

		PotionMeta meta = (PotionMeta) itemStack.getItemMeta();
		data.setItemAttribute(POTION_TYPE, meta.getBasePotionData().getType());
		if (!meta.getCustomEffects().isEmpty()) data.setItemAttribute(POTION_EFFECTS, meta.getCustomEffects());
		data.setItemAttribute(COLOR, meta.getColor());

		return data;
	}

	public static PotionType getPotionType(ItemMeta meta) {
		if (!(meta instanceof PotionMeta)) return null;

		PotionMeta potionMeta = (PotionMeta) meta;
		return potionMeta.getBasePotionData().getType();
	}
	
}
