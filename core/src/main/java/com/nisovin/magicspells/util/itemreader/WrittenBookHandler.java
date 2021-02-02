package com.nisovin.magicspells.util.itemreader;

import java.util.List;

import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.configuration.ConfigurationSection;

import com.nisovin.magicspells.util.Util;
import com.nisovin.magicspells.util.magicitems.MagicItemData;
import static com.nisovin.magicspells.util.magicitems.MagicItemData.MagicItemAttribute.PAGES;
import static com.nisovin.magicspells.util.magicitems.MagicItemData.MagicItemAttribute.TITLE;
import static com.nisovin.magicspells.util.magicitems.MagicItemData.MagicItemAttribute.AUTHOR;

public class WrittenBookHandler {

	private static final String AUTHOR_CONFIG_NAME = AUTHOR.toString();
	private static final String PAGES_CONFIG_NAME = PAGES.toString();
	private static final String TITLE_CONFIG_NAME = TITLE.toString();

	public static void process(ConfigurationSection config, ItemMeta meta, MagicItemData data) {
		if (!(meta instanceof BookMeta)) return;
		
		BookMeta bookMeta = (BookMeta) meta;

		if (config.isString(TITLE_CONFIG_NAME)) {
			String title = Util.colorize(config.getString(TITLE_CONFIG_NAME));

			bookMeta.setTitle(title);
			data.setAttribute(TITLE, title);
		}

		if (config.isString(AUTHOR_CONFIG_NAME)) {
			String author = Util.colorize(config.getString(AUTHOR_CONFIG_NAME));

			bookMeta.setAuthor(author);
			data.setAttribute(AUTHOR, author);
		}

		if (config.isList(PAGES_CONFIG_NAME)) {
			List<String> pages = config.getStringList(PAGES_CONFIG_NAME);
			for (int i = 0; i < pages.size(); i++) {
				pages.set(i, Util.colorize(pages.get(i)));
			}

			if (!pages.isEmpty()) {
				bookMeta.setPages(pages);
				data.setAttribute(PAGES, pages);
			}
		}
	}

	public static void processItemMeta(ItemMeta meta, MagicItemData data) {
		if (!(meta instanceof BookMeta)) return;

		BookMeta bookMeta = (BookMeta) meta;
		if (data.hasAttribute(TITLE)) bookMeta.setTitle((String) data.getAttribute(TITLE));
		if (data.hasAttribute(AUTHOR)) bookMeta.setAuthor((String) data.getAttribute(AUTHOR));
		if (data.hasAttribute(PAGES)) bookMeta.setPages((List<String>) data.getAttribute(PAGES));
	}

	public static void processMagicItemData(ItemMeta meta, MagicItemData data) {
		if (!(meta instanceof BookMeta)) return;

		BookMeta bookMeta = (BookMeta) meta;
		if (bookMeta.hasAuthor()) data.setAttribute(AUTHOR, bookMeta.getAuthor());
		if (bookMeta.hasTitle()) data.setAttribute(TITLE, bookMeta.getTitle());
		if (bookMeta.hasPages()) {
			List<String> pages = bookMeta.getPages();
			if (!pages.isEmpty()) data.setAttribute(PAGES, pages);
		}
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
