package com.nisovin.magicspells.castmodifiers.conditions;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.entity.LivingEntity;

import org.jetbrains.annotations.NotNull;

import com.nisovin.magicspells.util.Name;
import com.nisovin.magicspells.castmodifiers.Condition;

@Name("blocking")
public class BlockingCondition extends Condition {
	
	@Override
	public boolean initialize(@NotNull String var) {
		return true;
	}
	
	@Override
	public boolean check(LivingEntity caster) {
		return blocking(caster);
	}
	
	@Override
	public boolean check(LivingEntity caster, LivingEntity target) {
		return blocking(target);
	}
	
	@Override
	public boolean check(LivingEntity caster, Location location) {
		return false;
	}

	private boolean blocking(LivingEntity target) {
		if (!(target instanceof Player pl)) return false;
		return pl.isBlocking();
	}
	
}
