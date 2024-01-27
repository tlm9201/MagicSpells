package com.nisovin.magicspells.castmodifiers.conditions;


import com.nisovin.magicspells.castmodifiers.Condition;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class ClientNameCondition extends Condition {

	private String clientName;
	@Override
	public boolean initialize(@NotNull String var) {
		if (var.isEmpty()) return false;
		clientName = var;
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
		return (pl.getClientBrandName().equalsIgnoreCase(clientName));
	}
}
