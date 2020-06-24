package com.nisovin.magicspells.util.itemreader;

import java.util.List;

import org.bukkit.DyeColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.block.banner.Pattern;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.block.banner.PatternType;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.configuration.ConfigurationSection;

import com.nisovin.magicspells.util.magicitems.MagicItemData;

public class BannerHandler {

	private static final String CONFIG_NAME = "patterns";

	public static ItemMeta process(ConfigurationSection config, ItemMeta meta, MagicItemData data) {
		if (!(meta instanceof BannerMeta)) return meta;
		if (!config.contains(CONFIG_NAME)) return meta;
		if (!config.isList(CONFIG_NAME)) return meta;

		BannerMeta bannerMeta = (BannerMeta) meta;

		// patternType dyeColor
		List<String> strPatterns = config.getStringList(CONFIG_NAME);
		for (String str : strPatterns) {
			String[] args = str.split(" ");
			if (args.length < 2) continue;

			PatternType patternType = PatternType.getByIdentifier(args[0].toLowerCase());
			if (patternType == null) patternType = PatternType.valueOf(args[0].toUpperCase());
			if (patternType == null) continue;

			DyeColor dyeColor = DyeColor.valueOf(args[1].toUpperCase());
			if (dyeColor == null) dyeColor = DyeColor.WHITE;

			Pattern pattern = new Pattern(dyeColor, patternType);
			bannerMeta.addPattern(pattern);
		}

		if (!bannerMeta.getPatterns().isEmpty()) data.setPatterns(bannerMeta.getPatterns());
		return bannerMeta;
	}

	public static ItemMeta process(ItemMeta meta, MagicItemData data) {
		if (data == null) return meta;
		if (!(meta instanceof BannerMeta)) return meta;

		BannerMeta bannerMeta = (BannerMeta) meta;
		if (data.getPatterns() == null) return meta;

		((BannerMeta) meta).setPatterns(data.getPatterns());
		return bannerMeta;
	}

	public static MagicItemData process(ItemStack itemStack, MagicItemData data) {
		if (data == null) return null;
		if (itemStack == null) return data;
		if (!(itemStack.getItemMeta() instanceof BannerMeta)) return data;

		BannerMeta meta = (BannerMeta) itemStack.getItemMeta();
		if (!meta.getPatterns().isEmpty()) data.setPatterns(meta.getPatterns());
		return data;
	}
	
}
