package com.nisovin.magicspells.spells.targeted.cleanse;

import java.util.function.BiConsumer;
import java.util.function.BiPredicate;

import org.bukkit.entity.LivingEntity;

import org.jetbrains.annotations.NotNull;

import com.nisovin.magicspells.spells.targeted.DotSpell;
import com.nisovin.magicspells.spells.targeted.cleanse.util.SpellCleanser;

public class DotSpellCleanser extends SpellCleanser<DotSpell> {

	@Override
	protected @NotNull String getPrefix() {
		return "dot";
	}

	@Override
	protected @NotNull Class<DotSpell> getSpellClass() {
		return DotSpell.class;
	}

	@Override
	protected @NotNull BiPredicate<DotSpell, LivingEntity> getIsActive() {
		return DotSpell::isActive;
	}

	@Override
	protected @NotNull BiConsumer<DotSpell, LivingEntity> getCleanse() {
		return DotSpell::cancelDot;
	}
}
