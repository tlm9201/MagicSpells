package com.nisovin.magicspells.castmodifiers.conditions;

import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;

import com.nisovin.magicspells.Spell;
import com.nisovin.magicspells.util.Name;
import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.spells.targeted.PulserSpell;
import com.nisovin.magicspells.spells.targeted.PulserSpell.Pulser;
import com.nisovin.magicspells.castmodifiers.conditions.util.OperatorCondition;

@Name("pulseractive")
public class PulserActiveCondition extends OperatorCondition {

	private static final Pattern OPERATORS = Pattern.compile("[:=<>]");

	protected PulserSpell pulser;
	protected int value;

	@Override
	public boolean initialize(@NotNull String var) {
		Spell spell = MagicSpells.getSpellByInternalName(var);
		if (spell instanceof PulserSpell pulserSpell) {
			pulser = pulserSpell;
			moreThan = true;
			value = 0;

			return true;
		}

		Matcher matcher = OPERATORS.matcher(var);
		while (matcher.find()) {
			spell = MagicSpells.getSpellByInternalName(var.substring(0, matcher.start()));
			if (!(spell instanceof PulserSpell pulserSpell)) continue;

			String number = var.substring(matcher.start());
			if (number.length() < 2 || !super.initialize(number)) continue;

			try {
				value = Integer.parseInt(number.substring(1));
			} catch (NumberFormatException e) {
				continue;
			}

			pulser = pulserSpell;

			return true;
		}

		return pulser != null;
	}

	@Override
	public boolean check(LivingEntity caster) {
		int count = 0;

		Map<Block, Pulser> pulsers = pulser.getPulsers();
		for (Pulser pulser : pulsers.values())
			if (caster.equals(pulser.getCaster()))
				count++;

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
