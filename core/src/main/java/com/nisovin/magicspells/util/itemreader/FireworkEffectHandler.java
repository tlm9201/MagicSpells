package com.nisovin.magicspells.util.itemreader;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.FireworkEffectMeta;
import org.bukkit.configuration.ConfigurationSection;

import com.nisovin.magicspells.util.Util;
import com.nisovin.magicspells.DebugHandler;
import com.nisovin.magicspells.util.magicitems.MagicItemData;

public class FireworkEffectHandler {

	private static final String TYPE_CONFIG_NAME = "firework-type";
	private static final String TRAIL_CONFIG_NAME = "trail";
	private static final String FLICKER_CONFIG_NAME = "flicker";
	private static final String COLORS_CONFIG_NAME = "colors";
	private static final String FADE_COLORS_CONFIG_NAME = "fade-colors";

	public static ItemMeta process(ConfigurationSection config, ItemMeta meta, MagicItemData data) {
		if (!(meta instanceof FireworkEffectMeta)) return meta;

		String type = "ball";

		boolean trail = false;
		boolean flicker = false;

		Color[] colors = null;
		Color[] fadeColors = null;

		if (config.contains(TYPE_CONFIG_NAME) && config.isString(TYPE_CONFIG_NAME)) {
			type = config.getString(TYPE_CONFIG_NAME).trim();
		}

		if (config.contains(TRAIL_CONFIG_NAME) && config.isBoolean(TRAIL_CONFIG_NAME)) {
			trail = config.getBoolean(TRAIL_CONFIG_NAME);
		}

		if (config.contains(FLICKER_CONFIG_NAME) && config.isBoolean(FLICKER_CONFIG_NAME)) {
			flicker = config.getBoolean(FLICKER_CONFIG_NAME);
		}

		if (config.contains(COLORS_CONFIG_NAME) && config.isString(COLORS_CONFIG_NAME)) {
			colors = Util.getColorsFromString(config.getString(COLORS_CONFIG_NAME, "FF0000"));
		}

		if (config.contains(FADE_COLORS_CONFIG_NAME) && config.isString(FADE_COLORS_CONFIG_NAME)) {
			fadeColors = Util.getColorsFromString(config.getString(FADE_COLORS_CONFIG_NAME, "FF0000"));
		}

		// colors cant be null
		if (colors == null) return meta;
		if (fadeColors == null) fadeColors = new Color[0];

		FireworkEffect.Type fireworkType = null;
		try {
			fireworkType = FireworkEffect.Type.valueOf(type.toUpperCase());
		} catch (IllegalArgumentException e) {
			DebugHandler.debugIllegalArgumentException(e);
		}

		if (fireworkType == null) return meta;

		FireworkEffect effect = FireworkEffect.builder()
				.flicker(flicker)
				.trail(trail)
				.with(fireworkType)
				.withColor(colors)
				.withFade(fadeColors)
				.build();

		FireworkEffectMeta fireworkEffectMeta = (FireworkEffectMeta) meta;

		fireworkEffectMeta.setEffect(effect);
		data.setFireworkEffect(effect);

		return fireworkEffectMeta;
	}

	public static ItemMeta process(ItemMeta meta, MagicItemData data) {
		if (data == null) return meta;
		if (!(meta instanceof FireworkEffectMeta)) return meta;

		if (data.getFireworkEffect() == null) return meta;
		FireworkEffectMeta fireworkEffectMeta = (FireworkEffectMeta) meta;

		fireworkEffectMeta.setEffect(data.getFireworkEffect());
		return fireworkEffectMeta;
	}

	public static MagicItemData process(ItemStack itemStack, MagicItemData data) {
		if (data == null) return null;
		if (itemStack == null) return data;
		if (!(itemStack.getItemMeta() instanceof FireworkEffectMeta)) return data;

		FireworkEffectMeta meta = (FireworkEffectMeta) itemStack.getItemMeta();
		data.setFireworkEffect(meta.getEffect());
		return data;
	}

}
