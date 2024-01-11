package com.nisovin.magicspells.spells.targeted.cleanse;

import java.util.function.BiConsumer;
import java.util.function.BiPredicate;

import org.bukkit.entity.LivingEntity;

import org.jetbrains.annotations.NotNull;

import com.nisovin.magicspells.spells.targeted.LevitateSpell;
import com.nisovin.magicspells.spells.targeted.cleanse.util.SpellCleanser;

public class LevitateSpellCleanser extends SpellCleanser<LevitateSpell> {

	@Override
	protected @NotNull String getPrefix() {
		return "levitate";
	}

	@Override
	protected @NotNull Class<LevitateSpell> getSpellClass() {
		return LevitateSpell.class;
	}

	@Override
	protected @NotNull BiPredicate<LevitateSpell, LivingEntity> getIsActive() {
		return LevitateSpell::isBeingLevitated;
	}

	@Override
	protected @NotNull BiConsumer<LevitateSpell, LivingEntity> getCleanse() {
		return LevitateSpell::removeLevitate;
	}

}
