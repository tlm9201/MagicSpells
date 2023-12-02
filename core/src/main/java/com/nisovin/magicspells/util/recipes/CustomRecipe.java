package com.nisovin.magicspells.util.recipes;

import java.util.*;
import java.lang.reflect.Field;

import com.destroystokyo.paper.MaterialTags;
import com.destroystokyo.paper.MaterialSetTag;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.configuration.ConfigurationSection;

import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.handlers.DebugHandler;
import com.nisovin.magicspells.util.ConfigReaderUtil;
import com.nisovin.magicspells.util.magicitems.MagicItem;
import com.nisovin.magicspells.util.magicitems.MagicItems;

public abstract class CustomRecipe {

	private static final Map<String, MaterialSetTag> MATERIAL_TAGS = new HashMap<>();
	static {
		for (Field field : MaterialTags.class.getDeclaredFields()) {
			try {
				if (!(field.get(null) instanceof MaterialSetTag tag)) continue;
				MATERIAL_TAGS.put(field.getName(), tag);
			}
			catch (IllegalAccessException ignored) {}
		}
	}

	protected void error(String path, String message) {
		MagicSpells.error("Error on recipe '" + config.getName() + "', option '" + path + "': " + message);
		hadError = true;
	}

	private boolean hadError = false;

	protected ConfigurationSection config;

	protected String group;
	protected ItemStack result;
	protected NamespacedKey namespaceKey;

	private CustomRecipe() {}

	public CustomRecipe(ConfigurationSection config) {
		this.config = config;

		// Recipe group
		group = config.getString("group", "");

		// Result item
		MagicItem magicItem = getMagicItem(config.get("result"));
		if (magicItem == null) {
			error("result", "Invalid magic item defined.");
			return;
		}
		result = magicItem.getItemStack().clone();

		// Result quantity
		int quantity = config.getInt("quantity", 1);
		result.setAmount(Math.max(1, quantity));

		// Namespace key
		String namespaceKeyString = config.getString("namespace-key", config.getName());
		try {
			namespaceKey = new NamespacedKey(MagicSpells.getInstance(), namespaceKeyString);
		} catch (IllegalArgumentException e) {
			error("namespace-key", "Invalid namespace key: " + namespaceKeyString);
			MagicSpells.handleException(e);
		}
	}

	public boolean hadError() {
		return hadError;
	}

	public abstract Recipe build();

	protected RecipeChoice resolveRecipeChoice(String path) {
		if (!config.isList(path)) {
			Object object = config.get(path);
			if (object instanceof String tagName && tagName.startsWith("tag:")) {
				MaterialSetTag tag = resolveMaterialTag(path, tagName);
				return tag == null ? null : new RecipeChoice.MaterialChoice(tag);
			}

			MagicItem magicItem = getMagicItem(object);
			if (magicItem == null) {
				error(path, "Invalid magic item.");
				return null;
			}
			return new RecipeChoice.ExactChoice(getLoreVariants(magicItem));
		}

		boolean isExpectingTags = false;
		List<ItemStack> items = new ArrayList<>();
		List<Material> materials = new ArrayList<>();
		List<?> list = config.getList(path, new ArrayList<>());
		for (int i = 0; i < list.size(); i++) {
			Object object = list.get(i);

			if (object instanceof String tagName && tagName.startsWith("tag:")) {
				isExpectingTags = true;
				MaterialSetTag tag = resolveMaterialTag(path, tagName);
				if (tag == null) return null;
				materials.addAll(tag.getValues());
				continue;
			}

			if (isExpectingTags) {
				error(path, "You cannot mix material tags and item-based recipe choices together.");
				return null;
			}

			MagicItem magicItem = getMagicItem(object);
			if (magicItem == null) {
				error(path, "Invalid magic item listed at index " + i);
				return null;
			}
			items.addAll(getLoreVariants(magicItem));
		}
		return isExpectingTags ?
				new RecipeChoice.MaterialChoice(materials) :
				new RecipeChoice.ExactChoice(items);
	}

	private List<ItemStack> getLoreVariants(MagicItem magicItem) {
		List<ItemStack> list = new ArrayList<>();
		ItemStack originalItem = magicItem.getItemStack().clone();
		list.add(originalItem);
		ItemStack item = originalItem.clone();

		ItemMeta meta = item.getItemMeta();
		if (meta == null) return list;
		Component displayName = meta.displayName();
		if (displayName == null) return list;
		if (displayName.hasDecoration(TextDecoration.ITALIC)) return list;
		// Remove default "false" italics.
		meta.displayName(displayName.decoration(TextDecoration.ITALIC, TextDecoration.State.NOT_SET));
		item.setItemMeta(meta);
		list.add(item);
		return list;

	}

	protected MaterialSetTag resolveMaterialTag(String path, String tagName) {
		tagName = tagName.replaceFirst("tag:", "");
		MaterialSetTag tag = MATERIAL_TAGS.get(tagName.toUpperCase());
		if (tag == null) error(path, "Invalid material tag '" + tagName + "'. Must be one of: " + String.join(", " + MATERIAL_TAGS.keySet()));
		return tag;
	}

	protected MagicItem getMagicItem(Object object) {
		if (object instanceof String string) return MagicItems.getMagicItemFromString(string);
		if (object instanceof Map<?, ?> map) {
			ConfigurationSection config = ConfigReaderUtil.mapToSection(map);
			return MagicItems.getMagicItemFromSection(config);
		}
		return null;
	}

	protected <T extends Enum<T>> T resolveEnum(Class<T> enumClass, String path, T def) {
		String received = config.getString(path);
		if (received == null) return def;
		try {
			return Enum.valueOf(enumClass, received.toUpperCase());
		}
		catch (IllegalArgumentException e) {
			// DebugHandler sends a sufficient error message.
			error(path, "");
			DebugHandler.debugBadEnumValue(enumClass, received);
		}
		return null;
	}

}
