package com.nisovin.magicspells.spells.targeted.cleanse;

import java.util.function.BiConsumer;
import java.util.function.BiPredicate;

import org.bukkit.entity.LivingEntity;

import org.jetbrains.annotations.NotNull;

import com.nisovin.magicspells.spells.targeted.LoopSpell;
import com.nisovin.magicspells.spells.targeted.cleanse.util.SpellCleanser;

public class LoopSpellCleanser extends SpellCleanser<LoopSpell> {

	@Override
	protected @NotNull String getPrefix() {
		return "loop";
	}

	@Override
	protected @NotNull Class<LoopSpell> getSpellClass() {
		return LoopSpell.class;
	}

	@Override
	protected @NotNull BiPredicate<LoopSpell, LivingEntity> getIsActive() {
		return LoopSpell::isActive;
	}

	@Override
	protected @NotNull BiConsumer<LoopSpell, LivingEntity> getCleanse() {
		return LoopSpell::cancelLoops;
	}

}
