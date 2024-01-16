package com.nisovin.magicspells.spells.targeted.cleanse;

import java.util.function.BiConsumer;
import java.util.function.BiPredicate;

import org.bukkit.entity.LivingEntity;

import org.jetbrains.annotations.NotNull;

import com.nisovin.magicspells.spells.targeted.OrbitSpell;
import com.nisovin.magicspells.spells.targeted.cleanse.util.SpellCleanser;

public class OrbitSpellCleanser extends SpellCleanser<OrbitSpell> {

	@Override
	protected @NotNull String getPrefix() {
		return "orbit";
	}

	@Override
	protected @NotNull Class<OrbitSpell> getSpellClass() {
		return OrbitSpell.class;
	}

	@Override
	protected @NotNull BiPredicate<OrbitSpell, LivingEntity> getIsActive() {
		return OrbitSpell::hasOrbit;
	}

	@Override
	protected @NotNull BiConsumer<OrbitSpell, LivingEntity> getCleanse() {
		return OrbitSpell::removeOrbits;
	}

}
