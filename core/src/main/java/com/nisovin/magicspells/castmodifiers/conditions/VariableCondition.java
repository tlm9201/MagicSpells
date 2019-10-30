package com.nisovin.magicspells.castmodifiers.conditions;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.entity.LivingEntity;

import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.DebugHandler;
import com.nisovin.magicspells.castmodifiers.Condition;

public class VariableCondition extends Condition {

	private static Pattern variableNamePattern = Pattern.compile("[0-9a-zA-Z_]+");

	private String variable;
	private double value = 0;

	private boolean equals;
	private boolean moreThan;
	private boolean lessThan;

	@Override
	public boolean setVar(String var) {
		Matcher matcher = variableNamePattern.matcher(var);
		String variableName = null;
		char operator;
		String number;

		while (matcher.find()) {
			String argText = matcher.group();
			variableName = argText;
			break;
		}

		if (variableName == null) return false;

		operator = var.substring(variableName.length()).charAt(0);
		number = var.substring(variableName.length()).substring(1);

		if (number == null) return false;

		switch (operator) {
			case '=':
			case ':':
				equals = true;
				break;
			case '>':
				moreThan = true;
				break;
			case '<':
				lessThan = true;
				break;
			default:
				return false;
		}

		try {
			variable = variableName;
			value = Double.parseDouble(number);
			return true;
		} catch (NumberFormatException e) {
			DebugHandler.debugNumberFormat(e);
			return false;
		}
	}

	@Override
	public boolean check(LivingEntity livingEntity) {
		return variable(livingEntity);
	}

	@Override
	public boolean check(LivingEntity livingEntity, LivingEntity target) {
		return variable(target);
	}

	@Override
	public boolean check(LivingEntity livingEntity, Location location) {
		return variable(livingEntity);
	}

	private boolean variable(LivingEntity livingEntity) {
		if (!(livingEntity instanceof Player)) return false;
		if (equals) return MagicSpells.getVariableManager().getValue(variable, (Player) livingEntity) == value;
		else if (moreThan) return MagicSpells.getVariableManager().getValue(variable, (Player) livingEntity) > value;
		else if (lessThan) return MagicSpells.getVariableManager().getValue(variable, (Player) livingEntity) < value;
		return false;
	}

}
