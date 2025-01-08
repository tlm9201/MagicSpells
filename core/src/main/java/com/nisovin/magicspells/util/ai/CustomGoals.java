package com.nisovin.magicspells.util.ai;

import com.nisovin.magicspells.MagicSpells;

/**
 * @deprecated See {@link MagicSpells#getCustomGoalsManager()}
 */
@Deprecated(forRemoval = true)
public class CustomGoals {

	/**
	 * @deprecated See {@link MagicSpells#getCustomGoalsManager()} and {@link CustomGoalsManager#addGoal(Class)}
	 */
	@Deprecated(forRemoval = true)
	public static void addGoal(Class<? extends CustomGoal> goal) {
		MagicSpells.getCustomGoalsManager().addGoal(goal);
	}

}
