package com.nisovin.magicspells.castmodifiers.conditions;

import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;

import com.nisovin.magicspells.Spell;
import com.nisovin.magicspells.util.Name;
import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.spells.targeted.TotemSpell;
import com.nisovin.magicspells.castmodifiers.conditions.util.OperatorCondition;

@Name("totemactive")
public class TotemActiveCondition extends OperatorCondition {

	private static final Pattern OPERATORS = Pattern.compile("[:=<>]");

	protected TotemSpell totem;
	protected int value;

	@Override
	public boolean initialize(@NotNull String var) {
		Spell spell = MagicSpells.getSpellByInternalName(var);
		if (spell instanceof TotemSpell totemSpell) {
			totem = totemSpell;
			moreThan = true;
			value = 0;

			return true;
		}

		Matcher matcher = OPERATORS.matcher(var);
		while (matcher.find()) {
			spell = MagicSpells.getSpellByInternalName(var.substring(0, matcher.start()));
			if (!(spell instanceof TotemSpell totemSpell)) continue;

			String number = var.substring(matcher.start());
			if (number.length() < 2 || !super.initialize(number)) continue;

			try {
				value = Integer.parseInt(number.substring(1));
			} catch (NumberFormatException e) {
				continue;
			}

			totem = totemSpell;

			return true;
		}

		return totem != null;
	}

	@Override
	public boolean check(LivingEntity caster) {
		int count = totem.getTotems().get(caster.getUniqueId()).size();
		return compare(count, value);
	}

	@Override
	public boolean check(LivingEntity caster, LivingEntity target) {
		return check(target);
	}

	@Override
	public boolean check(LivingEntity caster, Location location) {
		return false;
	}

}
