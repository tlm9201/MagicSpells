package com.nisovin.magicspells.spells.targeted.cleanse.util;

import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;

import org.bukkit.entity.LivingEntity;

import org.jetbrains.annotations.NotNull;

import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.handlers.DebugHandler;
import com.nisovin.magicspells.util.ValidTargetChecker;
import com.nisovin.magicspells.spells.targeted.cleanse.*;

public class Cleansers {

	private static final List<Class<? extends Cleanser>> cleanserClasses = new ArrayList<>(Arrays.asList(
			PotionCleanser.class,
			BuffSpellCleanser.class,
			DotSpellCleanser.class,
			LevitateSpellCleanser.class,
			LoopSpellCleanser.class,
			OrbitSpellCleanser.class,
			SilenceSpellCleanser.class,
			StunSpellCleanser.class,
			TotemSpellCleanser.class,
			FireCleanser.class,
			FreezeCleanser.class
	));

	private final List<Cleanser> cleansers = new ArrayList<>();

	private ValidTargetChecker checker;

	private Cleansers() {}

	public Cleansers(List<String> toCleanse, String spellName) {
		try {
			for (Class<? extends Cleanser> clazz : cleanserClasses) {
				cleansers.add(clazz.getDeclaredConstructor().newInstance());
			}
		} catch (Exception e) {
			DebugHandler.debugGeneral(e);
		}

		checker = entity -> {
			for (Cleanser cleanser : cleansers) {
				if (cleanser.isAnyActive(entity)) {
					return true;
				}
			}
			return false;
		};

		outer:
		for (String string : toCleanse) {
			for (Cleanser cleanser : cleansers)
				if (cleanser.add(string))
					continue outer;

			MagicSpells.error("CleanseSpell '" + spellName + "' has an invalid cleanser '" + string + "' defined in 'remove'.");
		}
	}

	public ValidTargetChecker getChecker() {
		return checker;
	}

	public void cleanse(LivingEntity entity) {
		cleansers.forEach(cleanser -> cleanser.cleanse(entity));
	}

	public static void addCleanserClass(@NotNull Class<? extends Cleanser> clazz) {
		cleanserClasses.add(clazz);
	}

}
