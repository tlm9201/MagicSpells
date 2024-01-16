package com.nisovin.magicspells.spells.targeted.cleanse;

import java.util.function.BiConsumer;
import java.util.function.BiPredicate;

import org.bukkit.entity.LivingEntity;

import org.jetbrains.annotations.NotNull;

import com.nisovin.magicspells.spells.targeted.SilenceSpell;
import com.nisovin.magicspells.spells.targeted.cleanse.util.SpellCleanser;

public class SilenceSpellCleanser extends SpellCleanser<SilenceSpell> {

	@Override
	protected @NotNull String getPrefix() {
		return "silence";
	}

	@Override
	protected @NotNull Class<SilenceSpell> getSpellClass() {
		return SilenceSpell.class;
	}

	@Override
	protected @NotNull BiPredicate<SilenceSpell, LivingEntity> getIsActive() {
		return SilenceSpell::isSilenced;
	}

	@Override
	protected @NotNull BiConsumer<SilenceSpell, LivingEntity> getCleanse() {
		return SilenceSpell::removeSilence;
	}

}
