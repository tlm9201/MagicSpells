package com.nisovin.magicspells.util.recipes.types;

import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.configuration.ConfigurationSection;

import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.util.recipes.CustomRecipe;

public class CustomSmithingRecipe extends CustomRecipe {

	private final RecipeChoice template;
	private final RecipeChoice base;
	private final RecipeChoice addition;
	private final boolean copyNbt;

	public CustomSmithingRecipe(ConfigurationSection config) {
		super(config);
		template = resolveRecipeChoice("template");
		base = resolveRecipeChoice("base");
		addition = resolveRecipeChoice("addition");
		copyNbt = config.getBoolean("copyNbt", true);
	}

	@Override
	public Recipe build() {
		return MagicSpells.getVolatileCodeHandler().createSmithingRecipe(namespaceKey, result, template, base, addition, copyNbt);
	}

}
