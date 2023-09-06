package com.nisovin.magicspells.util.recipes.types;

import org.bukkit.inventory.CookingRecipe;
import org.bukkit.inventory.BlastingRecipe;
import org.bukkit.configuration.ConfigurationSection;

public class CustomBlastingRecipe extends CustomCookingRecipe {

	public CustomBlastingRecipe(ConfigurationSection config) {
		super(config);
	}

	@Override
	protected CookingRecipe<?> buildCooking() {
		return new BlastingRecipe(namespaceKey, result, ingredient, experience, cookingTime);
	}

}
