package com.nisovin.magicspells.castmodifiers.conditions;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;

import com.nisovin.magicspells.castmodifiers.Condition;

public class PlayerOnlineCondition extends Condition {
	
	private String name;
	
	@Override
	public boolean initialize(String var) {
		name = var;
		return true;
	}
	
	@Override
	public boolean check(LivingEntity caster) {
		return isOnline();
	}
	
	@Override
	public boolean check(LivingEntity caster, LivingEntity target) {
		return isOnline();
	}
	
	@Override
	public boolean check(LivingEntity caster, Location location) {
		return isOnline();
	}

	private boolean isOnline() {
		return Bukkit.getPlayerExact(name) != null;
	}

}
