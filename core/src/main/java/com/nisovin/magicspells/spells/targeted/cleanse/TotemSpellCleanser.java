package com.nisovin.magicspells.spells.targeted.cleanse;

import java.util.function.BiConsumer;
import java.util.function.BiPredicate;

import org.bukkit.entity.LivingEntity;

import org.jetbrains.annotations.NotNull;

import com.nisovin.magicspells.spells.targeted.TotemSpell;
import com.nisovin.magicspells.spells.targeted.cleanse.util.SpellCleanser;

public class TotemSpellCleanser extends SpellCleanser<TotemSpell> {

	@Override
	protected @NotNull String getPrefix() {
		return "totem";
	}

	@Override
	protected @NotNull Class<TotemSpell> getSpellClass() {
		return TotemSpell.class;
	}

	@Override
	protected @NotNull BiPredicate<TotemSpell, LivingEntity> getIsActive() {
		return TotemSpell::hasTotem;
	}

	@Override
	protected @NotNull BiConsumer<TotemSpell, LivingEntity> getCleanse() {
		return TotemSpell::removeTotems;
	}

}
