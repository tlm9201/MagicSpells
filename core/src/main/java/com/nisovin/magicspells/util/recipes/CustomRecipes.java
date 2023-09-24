package com.nisovin.magicspells.util.recipes;

import java.util.Map;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Recipe;
import org.bukkit.configuration.ConfigurationSection;

import com.nisovin.magicspells.util.Util;
import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.handlers.DebugHandler;

public class CustomRecipes {

	private static final Map<NamespacedKey, Recipe> recipes = new HashMap<>();

	public static void create(ConfigurationSection config) {
		CustomRecipeType type = null;
		String typeName = config.getString("type", "none");
		try {
			type = CustomRecipeType.valueOf(typeName.toUpperCase());
		} catch (IllegalArgumentException ignored) {}
		if (type == null) {
			MagicSpells.error("Recipe '" + config.getName() + "' has an invalid 'type' defined: " + typeName);
			DebugHandler.debugBadEnumValue(CustomRecipeType.class, typeName);
			return;
		}

		CustomRecipe customRecipe = type.newInstance(config);
		if (customRecipe.hadError()) return;

		// Handle Preconditions
		try {
			Recipe recipe = customRecipe.build();
			recipes.put(customRecipe.namespaceKey, recipe);
			Bukkit.addRecipe(recipe);
			Util.forEachPlayerOnline(player -> player.discoverRecipe(customRecipe.namespaceKey));
		}
		catch (IllegalArgumentException e) {
			MagicSpells.error("Error on recipe '" + config.getName() + "': " + e.getMessage());
		}
	}

	public static Map<NamespacedKey, Recipe> getRecipes() {
		return recipes;
	}

	public static void clearRecipes() {
		Util.forEachPlayerOnline(player -> player.undiscoverRecipes(recipes.keySet()));
		recipes.keySet().forEach(Bukkit::removeRecipe);
		recipes.clear();
		// TODO: This is api added in PaperMC 1.20+
		// Bukkit.updateRecipes();
	}

}
