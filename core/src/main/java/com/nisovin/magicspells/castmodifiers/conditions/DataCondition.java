package com.nisovin.magicspells.castmodifiers.conditions;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.function.Function;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.entity.LivingEntity;
import com.nisovin.magicspells.util.data.DataLivingEntity;

import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.castmodifiers.conditions.util.OperatorCondition;

public class DataCondition extends OperatorCondition {

	private static final Pattern DATA_PATTERN = Pattern.compile("([.\\w]+)([<>=:])(.+)");

	private Function<? super LivingEntity, String> dataElement;
	private String compare;

	private boolean constantValue;
	private double value;

	private boolean doTargetedReplacement = false;
	private boolean doVarReplacement = false;

	@Override
	public boolean initialize(String var) {
		Matcher matcher = DATA_PATTERN.matcher(var);
		if (!matcher.matches()) return false;

		if (!super.initialize(matcher.group(2))) return false;

		dataElement = DataLivingEntity.getDataFunction(matcher.group(1));
		if (dataElement == null) return false;

		compare = matcher.group(3).replace("__", " ");

		try {
			value = Double.parseDouble(compare);
			constantValue = true;
		} catch (NumberFormatException e) {
			constantValue = false;
		}

		if (compare.contains("%var") || compare.contains("%playervar")) doVarReplacement = true;
		if (compare.contains("%castervar") || compare.contains("%targetvar")) doTargetedReplacement = true;

		return true;
	}

	@Override
	public boolean check(LivingEntity caster) {
		return check(caster, caster);
	}

	@Override
	public boolean check(LivingEntity caster, LivingEntity target) {
		if (dataElement == null) return false;

		String localCompare = compare;

		Player playerCaster = caster instanceof Player ? (Player) caster : null;
		if (doVarReplacement) {
			localCompare = MagicSpells.doVariableReplacements(playerCaster, localCompare);
		}

		if (doTargetedReplacement) {
			Player playerTarget = target instanceof Player ? (Player) target : null;
			localCompare = MagicSpells.doTargetedVariableReplacements(playerCaster, playerTarget, localCompare);
		}

		String data = dataElement.apply(target);
		try {
			double dataDouble = Double.parseDouble(data);
			double localDouble = constantValue ?  value : Double.parseDouble(localCompare);

			if (equals) return dataDouble == localDouble;
			if (lessThan) return dataDouble < localDouble;
			if (moreThan) return dataDouble > localDouble;
		} catch (NumberFormatException e) {
			if (equals) return Objects.equals(data, localCompare);
		}

		return false;
	}

	@Override
	public boolean check(LivingEntity caster, Location location) {
		return check(caster, caster);
	}

}
