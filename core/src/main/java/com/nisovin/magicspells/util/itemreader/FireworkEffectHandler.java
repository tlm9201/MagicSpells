package com.nisovin.magicspells.util.itemreader;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.FireworkEffectMeta;
import org.bukkit.configuration.ConfigurationSection;

import com.nisovin.magicspells.util.Util;
import com.nisovin.magicspells.handlers.DebugHandler;
import com.nisovin.magicspells.util.magicitems.MagicItemData;
import static com.nisovin.magicspells.util.magicitems.MagicItemData.ItemAttribute.FIREWORK_EFFECT;

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

		if (config.isString(TYPE_CONFIG_NAME)) {
			type = config.getString(TYPE_CONFIG_NAME).trim();
		}

		if (config.isBoolean(TRAIL_CONFIG_NAME)) {
			trail = config.getBoolean(TRAIL_CONFIG_NAME);
		}

		if (config.isBoolean(FLICKER_CONFIG_NAME)) {
			flicker = config.getBoolean(FLICKER_CONFIG_NAME);
		}

		if (config.isString(COLORS_CONFIG_NAME)) {
			colors = Util.getColorsFromString(config.getString(COLORS_CONFIG_NAME, "FF0000"));
		}

		if (config.isString(FADE_COLORS_CONFIG_NAME)) {
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

		((FireworkEffectMeta) meta).setEffect(effect);
		data.setItemAttribute(FIREWORK_EFFECT, effect);

		return meta;
	}

	public static ItemMeta process(ItemMeta meta, MagicItemData data) {
		if (!(meta instanceof FireworkEffectMeta)) return meta;
		if (!data.hasItemAttribute(FIREWORK_EFFECT)) return meta;

		((FireworkEffectMeta) meta).setEffect((FireworkEffect) data.getItemAttribute(FIREWORK_EFFECT));
		return meta;
	}

	public static MagicItemData process(ItemStack itemStack, MagicItemData data) {
		if (data == null) return null;
		if (itemStack == null) return data;

		ItemMeta meta = itemStack.getItemMeta();
		if (!(meta instanceof FireworkEffectMeta)) return data;

		FireworkEffect effect = ((FireworkEffectMeta) meta).getEffect();
		data.setItemAttribute(FIREWORK_EFFECT, effect);

		return data;
	}

}
