package com.nisovin.magicspells.spells.targeted.cleanse;

import java.util.function.BiConsumer;
import java.util.function.BiPredicate;

import org.jetbrains.annotations.NotNull;

import org.bukkit.entity.LivingEntity;

import com.nisovin.magicspells.spells.BuffSpell;
import com.nisovin.magicspells.spells.targeted.cleanse.util.SpellCleanser;

public class BuffSpellCleanser extends SpellCleanser<BuffSpell> {

	@Override
	protected @NotNull String getPrefix() {
		return "buff";
	}

	@Override
	protected @NotNull Class<BuffSpell> getSpellClass() {
		return BuffSpell.class;
	}

	@Override
	protected @NotNull BiPredicate<BuffSpell, LivingEntity> getIsActive() {
		return BuffSpell::isActive;
	}

	@Override
	protected @NotNull BiConsumer<BuffSpell, LivingEntity> getCleanse() {
		return BuffSpell::turnOff;
	}

}
