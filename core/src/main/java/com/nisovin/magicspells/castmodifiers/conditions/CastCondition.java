package com.nisovin.magicspells.castmodifiers.conditions;

import org.jetbrains.annotations.NotNull;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;

import com.nisovin.magicspells.events.*;
import com.nisovin.magicspells.Subspell;
import com.nisovin.magicspells.util.Name;
import com.nisovin.magicspells.util.SpellData;
import com.nisovin.magicspells.util.ModifierResult;
import com.nisovin.magicspells.Spell.SpellCastResult;
import com.nisovin.magicspells.castmodifiers.Condition;
import com.nisovin.magicspells.castmodifiers.IModifier;

@Name("cast")
public class CastCondition extends Condition implements IModifier {

	private Subspell spell;

	@Override
	public boolean initialize(@NotNull String var) {
		if (var.isEmpty()) return false;
		spell = new Subspell(var);
		return spell.process();
	}

	@Override
	public boolean check(LivingEntity caster) {
		return spell.subcast(new SpellData(caster)).success();
	}

	@Override
	public boolean check(LivingEntity caster, LivingEntity target) {
		return spell.subcast(new SpellData(caster, target)).success();
	}

	@Override
	public boolean check(LivingEntity caster, Location location) {
		return spell.subcast(new SpellData(caster, location)).success();
	}

	@Override
	public boolean apply(SpellCastEvent event) {
		return spell.subcast(event.getSpellData().noTargeting()).success();
	}

	@Override
	public boolean apply(ManaChangeEvent event) {
		return spell.subcast(new SpellData(event.getPlayer())).success();
	}

	@Override
	public boolean apply(SpellTargetEvent event) {
		return spell.subcast(event.getSpellData().noLocation()).success();
	}

	@Override
	public boolean apply(SpellTargetLocationEvent event) {
		return spell.subcast(event.getSpellData().noTarget()).success();
	}

	@Override
	public boolean apply(MagicSpellsGenericPlayerEvent event) {
		return spell.subcast(new SpellData(event.getPlayer())).success();
	}

	@Override
	public ModifierResult apply(LivingEntity caster, SpellData data) {
		SpellCastResult result = spell.subcast(data.noTargeting());
		return new ModifierResult(result.data, result.success());
	}

	@Override
	public ModifierResult apply(LivingEntity caster, LivingEntity target, SpellData data) {
		SpellCastResult result = spell.subcast(data.noLocation());
		return new ModifierResult(result.data, result.success());
	}

	@Override
	public ModifierResult apply(LivingEntity caster, Location target, SpellData data) {
		SpellCastResult result = spell.subcast(data.noTarget());
		return new ModifierResult(result.data, result.success());
	}

}
