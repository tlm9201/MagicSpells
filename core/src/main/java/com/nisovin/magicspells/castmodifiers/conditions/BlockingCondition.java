package com.nisovin.magicspells.castmodifiers.conditions;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.entity.LivingEntity;

import com.nisovin.magicspells.castmodifiers.Condition;

public class BlockingCondition extends Condition {
	
	@Override
	public boolean initialize(String var) {
		return true;
	}
	
	@Override
	public boolean check(LivingEntity livingEntity) {
		return blocking(livingEntity);
	}
	
	@Override
	public boolean check(LivingEntity livingEntity, LivingEntity target) {
		return blocking(target);
	}
	
	@Override
	public boolean check(LivingEntity livingEntity, Location location) {
		return false;
	}

	private boolean blocking(LivingEntity livingEntity) {
		if (!(livingEntity instanceof Player pl)) return false;
		return pl.isBlocking();
	}
	
}
