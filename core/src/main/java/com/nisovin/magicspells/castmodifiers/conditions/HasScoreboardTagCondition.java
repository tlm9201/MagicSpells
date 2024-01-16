package com.nisovin.magicspells.castmodifiers.conditions;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;

import org.jetbrains.annotations.NotNull;

import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.util.SpellData;
import com.nisovin.magicspells.castmodifiers.Condition;

public class HasScoreboardTagCondition extends Condition {

	private boolean doReplacement;
	private String tag;

	@Override
	public boolean initialize(@NotNull String var) {
		if (var.isEmpty()) return false;
		doReplacement = MagicSpells.requireReplacement(var);
		tag = var;
		return true;
	}

	@Override
	public boolean check(LivingEntity caster) {
		return checkTags(caster, caster);
	}

	@Override
	public boolean check(LivingEntity caster, LivingEntity target) {
		return checkTags(caster, target);
	}

	@Override
	public boolean check(LivingEntity caster, Location location) {
		return false;
	}

	private boolean checkTags(LivingEntity caster, LivingEntity target) {
		String localTag = doReplacement ? MagicSpells.doReplacements(tag, caster, new SpellData(caster, target)) : tag;
		return target.getScoreboardTags().contains(localTag);
	}

}
