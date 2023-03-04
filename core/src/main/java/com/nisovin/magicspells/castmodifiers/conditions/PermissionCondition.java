package com.nisovin.magicspells.castmodifiers.conditions;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;

import com.nisovin.magicspells.castmodifiers.Condition;

public class PermissionCondition extends Condition {

	private String perm;

	@Override
	public boolean initialize(String var) {
		perm = var;
		return true;
	}
	
	@Override
	public boolean check(LivingEntity livingEntity) {
		return hasPermission(livingEntity);
	}
	
	@Override
	public boolean check(LivingEntity livingEntity, LivingEntity target) {
		return hasPermission(target);
	}
	
	@Override
	public boolean check(LivingEntity livingEntity, Location location) {
		return false;
	}

	private boolean hasPermission(LivingEntity livingEntity) {
		return livingEntity.hasPermission(perm);
	}

}
