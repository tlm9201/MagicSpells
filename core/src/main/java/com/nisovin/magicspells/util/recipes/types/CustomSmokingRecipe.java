package com.nisovin.magicspells.util.recipes.types;

import org.bukkit.inventory.CookingRecipe;
import org.bukkit.inventory.SmokingRecipe;
import org.bukkit.configuration.ConfigurationSection;

public class CustomSmokingRecipe extends CustomCookingRecipe {

	public CustomSmokingRecipe(ConfigurationSection config) {
		super(config);
	}

	@Override
	protected CookingRecipe<?> buildCooking() {
		return new SmokingRecipe(namespaceKey, result, ingredient, experience, cookingTime);
	}

}
