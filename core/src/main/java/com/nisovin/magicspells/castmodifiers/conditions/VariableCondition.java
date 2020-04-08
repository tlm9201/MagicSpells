package com.nisovin.magicspells.castmodifiers.conditions;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.entity.LivingEntity;

import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.DebugHandler;

public class VariableCondition extends OperatorCondition {

	private static Pattern VARIABLE_NAME_PATTERN = Pattern.compile("[0-9a-zA-Z_]+");

	private String variable;
	private double value = 0;

	@Override
	public boolean setVar(String var) {
		Matcher matcher = VARIABLE_NAME_PATTERN.matcher(var);
		String variableName;

		if (matcher.find()) variableName = matcher.group();
		else return false;

		String number = var.substring(variableName.length());
		super.setVar(number);

		try {
			variable = variableName;
			value = Double.parseDouble(number.substring(1));
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
