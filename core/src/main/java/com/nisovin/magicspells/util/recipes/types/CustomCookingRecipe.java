package com.nisovin.magicspells.util.recipes.types;

import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.CookingRecipe;
import org.bukkit.configuration.ConfigurationSection;

import com.nisovin.magicspells.util.recipes.CustomRecipe;

public abstract class CustomCookingRecipe extends CustomRecipe {

	protected final RecipeChoice ingredient;
	protected final float experience;
	protected final int cookingTime;

	public CustomCookingRecipe(ConfigurationSection config) {
		super(config);
		ingredient = resolveRecipeChoice("ingredient");
		experience = (float) config.getDouble("experience", 0);
		cookingTime = config.getInt("cooking-time", 0);
	}

	@Override
	public Recipe build() {
		CookingRecipe<?> recipe = buildCooking();
		recipe.setGroup(group);
		return recipe;
	}

	protected abstract CookingRecipe<?> buildCooking();

}
