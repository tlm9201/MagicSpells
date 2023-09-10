package com.nisovin.magicspells.util.recipes.types;

import java.util.List;
import java.util.ArrayList;

import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.configuration.ConfigurationSection;

import com.nisovin.magicspells.util.recipes.CustomRecipe;

public class CustomShapelessRecipe extends CustomRecipe {

	private final List<RecipeChoice> ingredients = new ArrayList<>();

	public CustomShapelessRecipe(ConfigurationSection config) {
		super(config);

		String path = "ingredients";
		ConfigurationSection ingredientsConfig;
		if (config.isList(path)) {
			// Backward compatibility:
			List<?> ingredientList = config.getList(path, new ArrayList<>());
			ingredientsConfig = config.createSection(path);
			for (int i = 0; i < ingredientList.size(); i++) {
				ingredientsConfig.set(i + "", ingredientList.get(i));
			}
		}
		else ingredientsConfig = config.getConfigurationSection(path);
		if (ingredientsConfig == null) {
			error(path, "None defined.");
			return;
		}

		for (String key : ingredientsConfig.getKeys(false)) {
			RecipeChoice choice = resolveRecipeChoice(path + "." + key);
			if (choice == null) continue;
			ingredients.add(choice);
		}
	}

	@Override
	public Recipe build() {
		ShapelessRecipe recipe = new ShapelessRecipe(namespaceKey, result);
		recipe.setGroup(group);
		ingredients.forEach(recipe::addIngredient);
		return recipe;
	}

}
