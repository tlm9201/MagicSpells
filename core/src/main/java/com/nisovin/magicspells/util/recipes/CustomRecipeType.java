package com.nisovin.magicspells.util.recipes;

import java.util.function.Function;

import com.nisovin.magicspells.util.recipes.types.*;

import org.bukkit.configuration.ConfigurationSection;

public enum CustomRecipeType {

	STONECUTTING(CustomStonecuttingRecipe::new),
	FURNACE(CustomFurnaceRecipe::new),
	SMOKING(CustomSmokingRecipe::new),
	CAMPFIRE(CustomCampfireRecipe::new),
	BLASTING(CustomBlastingRecipe::new),
	SMITHING(CustomSmithingRecipe::new),
	SHAPELESS(CustomShapelessRecipe::new),
	SHAPED(CustomShapedRecipe::new),

	;

	private final Function<ConfigurationSection, CustomRecipe> builder;

	CustomRecipeType(Function<ConfigurationSection, CustomRecipe> builder) {
		this.builder = builder;
	}

	public CustomRecipe newInstance(ConfigurationSection config) {
		return builder.apply(config);
	}

}
