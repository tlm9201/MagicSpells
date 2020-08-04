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

public class FireworkHandler {

	private static final String FIREWORK_EFFECTS_CONFIG_NAME = "firework-effects";
	private static final String POWER_CONFIG_NAME = "power";

	public static ItemMeta process(ConfigurationSection config, ItemMeta meta, MagicItemData data) {
		if (!(meta instanceof FireworkMeta)) return meta;
		if (!config.contains(FIREWORK_EFFECTS_CONFIG_NAME)) return meta;
		if (!config.isList(FIREWORK_EFFECTS_CONFIG_NAME)) return meta;

		FireworkMeta fireworkMeta = (FireworkMeta) meta;

		int power = 0;
		if (config.contains(POWER_CONFIG_NAME) && config.isInt(POWER_CONFIG_NAME)) power = config.getInt(POWER_CONFIG_NAME);

		if (config.contains(FIREWORK_EFFECTS_CONFIG_NAME) && config.isList(FIREWORK_EFFECTS_CONFIG_NAME)) {
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
					DebugHandler.debugIllegalArgumentException(e);
				}
				if (fireworkType == null) continue;

				boolean trail = Boolean.parseBoolean(args[1]);
				boolean flicker = Boolean.parseBoolean(args[2]);

				Color[] colors = Util.getColorsFromString(args[3]);
				Color[] fadeColors = Util.getColorsFromString(args[4]);

				// colors cant be null
				if (colors == null) continue;
				if (fadeColors == null) fadeColors = new Color[0];

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
			data.setFireworkEffects(fireworkEffects);
		}

		fireworkMeta.setPower(power);
		data.setPower(power);

		return fireworkMeta;
	}

	public static ItemMeta process(ItemMeta meta, MagicItemData data) {
		if (data == null) return meta;
		if (!(meta instanceof FireworkMeta)) return meta;

		FireworkMeta fireworkMeta = (FireworkMeta) meta;
		fireworkMeta.setPower(data.getPower());

		if (data.getFireworkEffect() == null) return fireworkMeta;

		fireworkMeta.addEffects(data.getFireworkEffects());
		return fireworkMeta;
	}

	public static MagicItemData process(ItemStack itemStack, MagicItemData data) {
		if (data == null) return null;
		if (itemStack == null) return data;
		if (!(itemStack.getItemMeta() instanceof FireworkMeta)) return data;

		FireworkMeta meta = (FireworkMeta) itemStack.getItemMeta();
		data.setPower(meta.getPower());
		data.setFireworkEffects(meta.getEffects());
		return data;
	}

}
