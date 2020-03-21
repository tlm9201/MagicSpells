package com.nisovin.magicspells.util.itemreader;

import java.util.List;

import com.nisovin.magicspells.util.Util;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.configuration.ConfigurationSection;

public class WrittenBookHandler {

	public static ItemMeta process(ConfigurationSection config, ItemMeta meta) {
		if (!(meta instanceof BookMeta)) return meta;
		
		BookMeta bmeta = (BookMeta)meta;
		
		if (config.contains("title") && config.isString("title")) bmeta.setTitle(Util.colorize(config.getString("title")));
		if (config.contains("author") && config.isString("author")) bmeta.setAuthor(Util.colorize(config.getString("author")));
		if (config.contains("pages") && config.isList("pages")) {
			List<String> pages = config.getStringList("pages");
			for (int i = 0; i < pages.size(); i++) {
				pages.set(i, Util.colorize(pages.get(i)));
			}
			bmeta.setPages(pages);
		}
		return bmeta;
	}
	
}
