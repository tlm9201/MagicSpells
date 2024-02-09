package com.nisovin.magicspells.castmodifiers.conditions;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.entity.LivingEntity;

import org.jetbrains.annotations.NotNull;

import com.nisovin.magicspells.util.Name;
import com.nisovin.magicspells.castmodifiers.Condition;

@Name("clientname")
public class ClientBrandNameCondition extends Condition {

	private String clientBrandName;

	@Override
	public boolean initialize(@NotNull String var) {
		if (var.isEmpty()) return false;
		clientBrandName = var;
		return true;
	}

	@Override
	public boolean check(LivingEntity caster) {
		return checkClientName(caster);
	}

	@Override
	public boolean check(LivingEntity caster, LivingEntity target) {
		return checkClientName(target);
	}

	@Override
	public boolean check(LivingEntity caster, Location location) {
		return checkClientName(caster);
	}

	private boolean checkClientName(LivingEntity target) {
		if (!(target instanceof Player pl)) return false;
		if (pl.getClientBrandName() == null) return false;
		return (pl.getClientBrandName().equalsIgnoreCase(clientBrandName));
	}

}
