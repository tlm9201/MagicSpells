package com.nisovin.magicspells.util.recipes.types;

import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.StonecuttingRecipe;
import org.bukkit.configuration.ConfigurationSection;

import com.nisovin.magicspells.util.recipes.CustomRecipe;

public class CustomStonecuttingRecipe extends CustomRecipe {

	private final RecipeChoice ingredient;

	public CustomStonecuttingRecipe(ConfigurationSection config) {
		super(config);
		ingredient = resolveRecipeChoice("ingredient");
	}

	@Override
	public Recipe build() {
		StonecuttingRecipe recipe = new StonecuttingRecipe(namespaceKey, result, ingredient);
		recipe.setGroup(group);
		return recipe;
	}

}
