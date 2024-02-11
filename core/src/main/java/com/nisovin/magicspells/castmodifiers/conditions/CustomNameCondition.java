package com.nisovin.magicspells.castmodifiers.conditions;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;

import org.jetbrains.annotations.NotNull;

import com.nisovin.magicspells.util.Name;
import com.nisovin.magicspells.util.Util;
import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.util.SpellData;
import com.nisovin.magicspells.castmodifiers.Condition;

@Name("customname")
public class CustomNameCondition extends Condition {

	private boolean requireReplacement;
	private String name;

	@Override
	public boolean initialize(@NotNull String var) {
		if (var.isEmpty()) return false;

		requireReplacement = MagicSpells.requireReplacement(var);
		name = var.replace("__", " ");

		return true;
	}

	@Override
	public boolean check(LivingEntity caster) {
		return false;
	}

	@Override
	public boolean check(LivingEntity caster, LivingEntity target) {
		return checkName(caster, target);
	}

	@Override
	public boolean check(LivingEntity caster, Location location) {
		return false;
	}

	private boolean checkName(LivingEntity caster, LivingEntity target) {
		String checkedName = requireReplacement ? MagicSpells.doReplacements(name, caster, new SpellData(caster, target)) : name;
		return target.customName() != null && Util.getMiniMessage(checkedName).equals(target.customName());
	}

}
