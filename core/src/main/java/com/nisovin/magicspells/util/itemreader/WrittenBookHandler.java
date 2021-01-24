package com.nisovin.magicspells.util.itemreader;

import java.util.List;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.configuration.ConfigurationSection;

import com.nisovin.magicspells.util.Util;
import com.nisovin.magicspells.util.magicitems.MagicItemData;
import static com.nisovin.magicspells.util.magicitems.MagicItemData.ItemAttribute.PAGES;
import static com.nisovin.magicspells.util.magicitems.MagicItemData.ItemAttribute.TITLE;
import static com.nisovin.magicspells.util.magicitems.MagicItemData.ItemAttribute.AUTHOR;

public class WrittenBookHandler {

	private static final String AUTHOR_CONFIG_NAME = AUTHOR.toString();
	private static final String PAGES_CONFIG_NAME = PAGES.toString();
	private static final String TITLE_CONFIG_NAME = TITLE.toString();

	public static ItemMeta process(ConfigurationSection config, ItemMeta meta, MagicItemData data) {
		if (!(meta instanceof BookMeta)) return meta;
		
		BookMeta bookMeta = (BookMeta) meta;
		List<String> pages;
		String author;
		String title;

		if (config.isString(TITLE_CONFIG_NAME)) {
			title = Util.colorize(config.getString(TITLE_CONFIG_NAME));

			bookMeta.setTitle(title);
			data.setItemAttribute(TITLE, title);
		}

		if (config.isString(AUTHOR_CONFIG_NAME)) {
			author = Util.colorize(config.getString(AUTHOR_CONFIG_NAME));

			bookMeta.setAuthor(author);
			data.setItemAttribute(AUTHOR, author);
		}

		if (config.isList(PAGES_CONFIG_NAME)) {
			pages = config.getStringList(PAGES_CONFIG_NAME);
			for (int i = 0; i < pages.size(); i++) {
				pages.set(i, Util.colorize(pages.get(i)));
			}

			bookMeta.setPages(pages);
			data.setItemAttribute(PAGES, pages);
		}

		return bookMeta;
	}

	public static ItemMeta process(ItemMeta meta, MagicItemData data) {
		if (!(meta instanceof BookMeta)) return meta;

		BookMeta bookMeta = (BookMeta) meta;
		String title = (String) data.getItemAttribute(TITLE);
		String author = (String) data.getItemAttribute(AUTHOR);
		List<String> pages = (List<String>) data.getItemAttribute(PAGES);

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
		data.setItemAttribute(AUTHOR, meta.getAuthor());
		data.setItemAttribute(TITLE, meta.getTitle());
		if (!meta.getPages().isEmpty()) data.setItemAttribute(PAGES, meta.getPages());

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
