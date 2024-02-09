package com.nisovin.magicspells.castmodifiers.conditions;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;

import org.jetbrains.annotations.NotNull;

import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.GriefPrevention;

import com.nisovin.magicspells.util.DependsOn;
import com.nisovin.magicspells.castmodifiers.Condition;

@DependsOn("GriefPrevention")
public class GriefPreventionIsOwnerCondition extends Condition {

	@Override
	public boolean initialize(@NotNull String var) {
		return true;
	}

	@Override
	public boolean check(LivingEntity caster) {
		return checkClaim(caster, caster.getLocation());
	}

	@Override
	public boolean check(LivingEntity caster, LivingEntity target) {
		return checkClaim(target, target.getLocation());
	}

	@Override
	public boolean check(LivingEntity caster, Location location) {
		return checkClaim(caster, location);
	}

	private boolean checkClaim(LivingEntity target, Location location) {
		if (target == null) return false;
		Claim currentClaim = GriefPrevention.instance.dataStore.getClaimAt(location, false, null);
		if (currentClaim == null) return false;
		return (target.getUniqueId().equals(currentClaim.ownerID));
	}

}
