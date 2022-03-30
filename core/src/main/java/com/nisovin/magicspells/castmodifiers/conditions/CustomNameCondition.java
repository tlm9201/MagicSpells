package com.nisovin.magicspells.castmodifiers.conditions;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.entity.LivingEntity;
import com.nisovin.magicspells.util.Util;

import net.kyori.adventure.text.Component;

import com.nisovin.magicspells.castmodifiers.Condition;

public class CustomNameCondition extends Condition {

	private Component name;
	private boolean isVar;

	@Override
	public boolean initialize(String var) {
		if (var == null || var.isEmpty()) return false;
		if (var.contains("%var:") || var.contains("%playervar")) isVar = true;

		name = Util.getMiniMessage(var);
		return true;
	}

	@Override
	public boolean check(LivingEntity livingEntity) {
		return false;
	}

	@Override
	public boolean check(LivingEntity livingEntity, LivingEntity target) {
		return checkName(livingEntity, target);
	}

	@Override
	public boolean check(LivingEntity livingEntity, Location location) {
		return false;
	}

	private boolean checkName(LivingEntity livingEntity, LivingEntity target) {
		if (!(livingEntity instanceof Player pl)) return false;

		String checkedName = Util.getStringFromComponent(name);

		if (isVar) checkedName = Util.doVarReplacement(pl, checkedName);
		else checkedName = checkedName.replace("__", " ");

		return target.customName() != null && Util.getMiniMessage(checkedName).equals(target.customName());
	}

}
