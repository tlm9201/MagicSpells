package com.nisovin.magicspells.spells.targeted.cleanse;

import java.util.function.BiConsumer;
import java.util.function.BiPredicate;

import org.bukkit.entity.LivingEntity;

import org.jetbrains.annotations.NotNull;

import com.nisovin.magicspells.spells.targeted.StunSpell;
import com.nisovin.magicspells.spells.targeted.cleanse.util.SpellCleanser;

public class StunSpellCleanser extends SpellCleanser<StunSpell> {

	@Override
	protected @NotNull String getPrefix() {
		return "stun";
	}

	@Override
	protected @NotNull Class<StunSpell> getSpellClass() {
		return StunSpell.class;
	}

	@Override
	protected @NotNull BiPredicate<StunSpell, LivingEntity> getIsActive() {
		return StunSpell::isStunned;
	}

	@Override
	protected @NotNull BiConsumer<StunSpell, LivingEntity> getCleanse() {
		return StunSpell::removeStun;
	}

}
