package com.nisovin.magicspells.castmodifiers.conditions;

import org.bukkit.Location;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.LivingEntity;

import org.jetbrains.annotations.NotNull;

import com.nisovin.magicspells.util.Name;
import com.nisovin.magicspells.castmodifiers.Condition;

@Name("age")
public class AgeCondition extends Condition {
	
	private boolean passBaby = false;
	private boolean passAdult = false;
	
	@Override
	public boolean initialize(@NotNull String var) {
		passBaby = var.isEmpty() || var.equalsIgnoreCase("baby");
		passAdult = var.isEmpty() || var.equalsIgnoreCase("adult");
		return true;
	}

	@Override
	public boolean check(LivingEntity caster) {
		return age(caster);
	}

	@Override
	public boolean check(LivingEntity caster, LivingEntity target) {
		return age(target);
	}

	@Override
	public boolean check(LivingEntity caster, Location location) {
		return false;
	}

	private boolean age(LivingEntity target) {
		if (!(target instanceof Ageable age)) return false;
		boolean adult = age.isAdult();
		return adult ? passAdult : passBaby;
	}

}
