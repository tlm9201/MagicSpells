package com.nisovin.magicspells.util.itemreader;

import java.util.List;
import java.util.ArrayList;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.configuration.ConfigurationSection;

import com.nisovin.magicspells.util.Util;
import com.nisovin.magicspells.handlers.DebugHandler;
import com.nisovin.magicspells.util.magicitems.MagicItemData;
import static com.nisovin.magicspells.util.magicitems.MagicItemData.ItemAttribute.POWER;
import static com.nisovin.magicspells.util.magicitems.MagicItemData.ItemAttribute.FIREWORK_EFFECTS;

public class FireworkHandler {

	private static final String FIREWORK_EFFECTS_CONFIG_NAME = FIREWORK_EFFECTS.toString();
	private static final String POWER_CONFIG_NAME = POWER.toString();

	public static ItemMeta process(ConfigurationSection config, ItemMeta meta, MagicItemData data) {
		if (!(meta instanceof FireworkMeta)) return meta;
		if (!config.isList(FIREWORK_EFFECTS_CONFIG_NAME)) return meta;

		FireworkMeta fireworkMeta = (FireworkMeta) meta;

		int power = 0;
		if (config.isInt(POWER_CONFIG_NAME)) power = config.getInt(POWER_CONFIG_NAME);

		if (config.isList(FIREWORK_EFFECTS_CONFIG_NAME)) {
			List<String> argList = config.getStringList(FIREWORK_EFFECTS_CONFIG_NAME);

			List<FireworkEffect> fireworkEffects = new ArrayList<>();

			// <type> <trail> <flicker> <colors>(,) <fadeColors>(,)
			for (String str : argList) {
				String[] args = str.split(" ");
				if (args.length < 5) continue;

				String type = args[0];
				FireworkEffect.Type fireworkType = null;
				try {
					fireworkType = FireworkEffect.Type.valueOf(type.toUpperCase());
				} catch (IllegalArgumentException e) {
					DebugHandler.debugBadEnumValue(FireworkEffect.Type.class, type.toUpperCase());
				}
				if (fireworkType == null) continue;

				boolean trail = Boolean.parseBoolean(args[1]);
				boolean flicker = Boolean.parseBoolean(args[2]);

				Color[] colors = Util.getColorsFromString(args[3]);
				Color[] fadeColors = Util.getColorsFromString(args[4]);

				FireworkEffect effect = FireworkEffect.builder()
						.flicker(flicker)
						.trail(trail)
						.with(fireworkType)
						.withColor(colors)
						.withFade(fadeColors)
						.build();

				fireworkEffects.add(effect);
			}

			fireworkMeta.addEffects(fireworkEffects);
			data.setItemAttribute(FIREWORK_EFFECTS, fireworkEffects);
		}

		fireworkMeta.setPower(power);
		data.setItemAttribute(POWER, power);

		return meta;
	}

	public static ItemMeta process(ItemMeta meta, MagicItemData data) {
		if (!(meta instanceof FireworkMeta)) return meta;

		FireworkMeta fireworkMeta = (FireworkMeta) meta;
		fireworkMeta.setPower((int) data.getItemAttribute(POWER));

		if (!data.hasItemAttribute(FIREWORK_EFFECTS)) return fireworkMeta;
		fireworkMeta.addEffects((List<FireworkEffect>) data.getItemAttribute(FIREWORK_EFFECTS));

		return fireworkMeta;
	}

	public static MagicItemData process(ItemStack itemStack, MagicItemData data) {
		if (data == null) return null;
		if (itemStack == null) return data;

		ItemMeta meta = itemStack.getItemMeta();
		if (!(meta instanceof FireworkMeta)) return data;
		FireworkMeta fireworkMeta = (FireworkMeta) meta;

		data.setItemAttribute(POWER, fireworkMeta.getPower());
		data.setItemAttribute(FIREWORK_EFFECTS, fireworkMeta.getEffects());

		return data;
	}

}
