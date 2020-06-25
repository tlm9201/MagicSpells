package com.nisovin.magicspells.util.itemreader;

import java.util.List;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.configuration.ConfigurationSection;

import com.nisovin.magicspells.util.Util;
import com.nisovin.magicspells.util.magicitems.MagicItemData;

public class WrittenBookHandler {

	private static final String TITLE_CONFIG_NAME = "title";
	private static final String AUTHOR_CONFIG_NAME = "author";
	private static final String PAGES_CONFIG_NAME = "pages";

	public static ItemMeta process(ConfigurationSection config, ItemMeta meta, MagicItemData data) {
		if (!(meta instanceof BookMeta)) return meta;
		
		BookMeta bookMeta = (BookMeta) meta;
		String title;
		String author;
		List<String> pages;

		if (config.contains(TITLE_CONFIG_NAME) && config.isString(TITLE_CONFIG_NAME)) {
			title = Util.colorize(config.getString(TITLE_CONFIG_NAME));
			bookMeta.setTitle(title);
			if (data != null) data.setTitle(title);
		}

		if (config.contains(AUTHOR_CONFIG_NAME) && config.isString(AUTHOR_CONFIG_NAME)) {
			author = Util.colorize(config.getString(AUTHOR_CONFIG_NAME));
			bookMeta.setAuthor(author);
			if (data != null) data.setAuthor(author);
		}

		if (config.contains(PAGES_CONFIG_NAME) && config.isList(PAGES_CONFIG_NAME)) {
			pages = config.getStringList(PAGES_CONFIG_NAME);
			for (int i = 0; i < pages.size(); i++) {
				pages.set(i, Util.colorize(pages.get(i)));
			}
			bookMeta.setPages(pages);
			if (data != null) data.setPages(pages);
		}

		return bookMeta;
	}

	public static ItemMeta process(ItemMeta meta, MagicItemData data) {
		if (data == null) return meta;
		if (!(meta instanceof BookMeta)) return meta;

		BookMeta bookMeta = (BookMeta) meta;
		String title = data.getTitle();
		String author = data.getAuthor();
		List<String> pages = data.getPages();

		if (title != null) {
			title = Util.colorize(title);
			bookMeta.setTitle(title);
		}

		if (author != null) {
			author = Util.colorize(author);
			bookMeta.setAuthor(author);
		}

		if (pages != null) {
			for (int i = 0; i < pages.size(); i++) {
				pages.set(i, Util.colorize(pages.get(i)));
			}
			bookMeta.setPages(pages);
		}

		return bookMeta;
	}

	public static MagicItemData process(ItemStack itemStack, MagicItemData data) {
		if (data == null) return null;
		if (itemStack == null) return data;
		if (!(itemStack.getItemMeta() instanceof BookMeta)) return data;

		BookMeta meta = (BookMeta) itemStack.getItemMeta();
		data.setAuthor(meta.getAuthor());
		data.setTitle(meta.getTitle());
		if (!meta.getPages().isEmpty()) data.setPages(meta.getPages());
		return data;
	}

	public static String getTitle(ItemMeta meta) {
		if (!(meta instanceof BookMeta)) return null;

		return ((BookMeta) meta).getTitle();
	}

	public static String getAuthor(ItemMeta meta) {
		if (!(meta instanceof BookMeta)) return null;

		return ((BookMeta) meta).getAuthor();
	}
	
}
