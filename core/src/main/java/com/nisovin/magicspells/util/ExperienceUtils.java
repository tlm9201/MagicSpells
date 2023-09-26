package com.nisovin.magicspells.util;

import org.bukkit.entity.Player;

/**
 * See <a href="https://minecraft.wiki/w/Experience#Leveling_up">here</a> for implementation details.
 */
public class ExperienceUtils {

	public static int getTotalExperience(int level) {
		if (level < 0) throw new IllegalArgumentException("Level must be non-negative.");

		if (level <= 16) return level * level + 6 * level;
		if (level <= 31) return (int) (2.5d * level * level - 40.5d * level + 360);
		return (int) (4.5d * level * level - 162.5d * level + 2220);
	}

	public static int getExperienceToNextLevel(int level) {
		if (level < 0) throw new IllegalArgumentException("Level must be non-negative.");

		if (level <= 15) return 2 * level + 7;
		if (level <= 30) return 5 * level - 38;
		return 9 * level - 158;
	}

	public static int getLevelFromExperience(int experience) {
		if (experience < 0) throw new IllegalArgumentException("Experience must be non-negative.");

		if (experience <= 352) return (int) Math.sqrt(experience + 9) - 3;
		if (experience <= 1507) return (int) (81d / 10d + Math.sqrt(2d / 5d * (experience - 7839d / 40d)));
		return (int) (325d / 18d + Math.sqrt(2d / 9d * (experience - 54215d / 72d)));
	}

	public static int getExperience(Player player) {
		int level = player.getLevel();
		return getTotalExperience(level) + Math.round(player.getExp() * getExperienceToNextLevel(level));
	}

	public static void setExperience(Player player, int experience) {
		int level = getLevelFromExperience(experience);
		int remaining = experience - getTotalExperience(level);

		player.setLevel(level);
		player.setExp((float) remaining / getExperienceToNextLevel(level));
	}

	public static void changeExp(Player player, int amount) {
		int totalExperience = Math.min(getExperience(player) + amount, 0);
		setExperience(player, totalExperience);
	}

	public static boolean hasExp(Player player, int amount) {
		return amount <= getExperience(player);
	}

}
