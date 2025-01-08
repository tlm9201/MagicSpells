package com.nisovin.magicspells.spells.targeted.cleanse.util;

import org.jetbrains.annotations.NotNull;

import com.nisovin.magicspells.MagicSpells;

/**
 * @deprecated See {@link MagicSpells#getCleanserManager()}
 */
@Deprecated(forRemoval = true)
public class Cleansers {

	private Cleansers() {}

	/**
	 * @deprecated See {@link MagicSpells#getCleanserManager()} and {@link CleanserManager#addCleanser(Class)}
	 */
	@Deprecated(forRemoval = true)
	public static void addCleanserClass(@NotNull Class<? extends Cleanser> clazz) {
		MagicSpells.getCleanserManager().addCleanser(clazz);
	}

}
