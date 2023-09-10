package com.nisovin.magicspells.util.recipes.types;

import org.bukkit.inventory.CookingRecipe;
import org.bukkit.inventory.CampfireRecipe;
import org.bukkit.configuration.ConfigurationSection;

public class CustomCampfireRecipe extends CustomCookingRecipe {

	public CustomCampfireRecipe(ConfigurationSection config) {
		super(config);
	}

	@Override
	protected CookingRecipe<?> buildCooking() {
		return new CampfireRecipe(namespaceKey, result, ingredient, experience, cookingTime);
	}

}
