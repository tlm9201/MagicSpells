package com.nisovin.magicspells.util.recipes.types;

import java.util.Map;
import java.util.List;
import java.util.HashMap;

import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.configuration.ConfigurationSection;

import com.nisovin.magicspells.util.recipes.CustomRecipe;

public class CustomShapedRecipe extends CustomRecipe {

	private final List<String> shape;
	private final Map<Character, RecipeChoice> ingredients = new HashMap<>();

	public CustomShapedRecipe(ConfigurationSection config) {
		super(config);
		shape = config.getStringList("shape");

		ConfigurationSection ingredientConfig = config.getConfigurationSection("ingredients");
		if (ingredientConfig == null) {
			error("ingredients", "None defined.");
			return;
		}
		for (String key : ingredientConfig.getKeys(false)) {
			if (key.length() != 1) {
				error("ingredients", "Key '" + key + "' should be only one character.");
				return;
			}
			RecipeChoice choice = resolveRecipeChoice("ingredients." + key);
			if (choice == null) return;
			ingredients.put(key.charAt(0), choice);
		}
	}

	@Override
	public Recipe build() {
		ShapedRecipe recipe = new ShapedRecipe(namespaceKey, result);
		recipe.setGroup(group);
		recipe.shape(shape.toArray(new String[0]));
		ingredients.forEach(recipe::setIngredient);
		return recipe;
	}

}
