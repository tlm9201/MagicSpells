package com.nisovin.magicspells.util.itemreader;

import java.util.List;

import com.nisovin.magicspells.handlers.DebugHandler;
import org.bukkit.DyeColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.block.banner.Pattern;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.block.banner.PatternType;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.configuration.ConfigurationSection;

import com.nisovin.magicspells.util.magicitems.MagicItemData;
import static com.nisovin.magicspells.util.magicitems.MagicItemData.ItemAttribute.PATTERNS;

public class BannerHandler {

	private static final String CONFIG_NAME = PATTERNS.toString();

	public static ItemMeta process(ConfigurationSection config, ItemMeta meta, MagicItemData data) {
		if (!(meta instanceof BannerMeta)) return meta;
		if (!config.isList(CONFIG_NAME)) return meta;

		BannerMeta bannerMeta = (BannerMeta) meta;

		// patternType dyeColor
		List<String> strPatterns = config.getStringList(CONFIG_NAME);
		for (String str : strPatterns) {
			String[] args = str.split(" ");
			if (args.length < 2) continue;

			PatternType patternType = PatternType.getByIdentifier(args[0].toLowerCase());
			try {
				if (patternType == null) patternType = PatternType.valueOf(args[0].toUpperCase());
			} catch (IllegalArgumentException e) {
				DebugHandler.debugBadEnumValue(PatternType.class, args[0].toUpperCase());
				continue;
			}

			DyeColor dyeColor;
			try {
				dyeColor = DyeColor.valueOf(args[1].toUpperCase());
			} catch (IllegalArgumentException e) {
				DebugHandler.debugBadEnumValue(DyeColor.class, args[1].toUpperCase());
				continue;
			}

			Pattern pattern = new Pattern(dyeColor, patternType);
			bannerMeta.addPattern(pattern);
		}

		if (!bannerMeta.getPatterns().isEmpty()) data.setItemAttribute(PATTERNS, bannerMeta.getPatterns());
		return bannerMeta;
	}

	public static ItemMeta process(ItemMeta meta, MagicItemData data) {
		if (!(meta instanceof BannerMeta)) return meta;
		if (!data.hasItemAttribute(PATTERNS)) return meta;

		List<Pattern> patterns = (List<Pattern>) data.getItemAttribute(PATTERNS);
		BannerMeta bannerMeta = (BannerMeta) meta;
		((BannerMeta) meta).setPatterns(patterns);

		return bannerMeta;
	}

	public static MagicItemData process(ItemStack itemStack, MagicItemData data) {
		if (data == null) return null;
		if (itemStack == null) return data;

		ItemMeta meta = itemStack.getItemMeta();
		if (!(meta instanceof BannerMeta)) return data;

		List<Pattern> patterns = ((BannerMeta) itemStack.getItemMeta()).getPatterns();
		if (!patterns.isEmpty()) data.setItemAttribute(PATTERNS, patterns);

		return data;
	}
	
}
