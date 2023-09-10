package com.nisovin.magicspells.util.recipes.types;

import org.bukkit.inventory.CookingRecipe;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.configuration.ConfigurationSection;

public class CustomFurnaceRecipe extends CustomCookingRecipe {

	public CustomFurnaceRecipe(ConfigurationSection config) {
		super(config);
	}

	@Override
	protected CookingRecipe<?> buildCooking() {
		return new FurnaceRecipe(namespaceKey, result, ingredient, experience, cookingTime);
	}

}
