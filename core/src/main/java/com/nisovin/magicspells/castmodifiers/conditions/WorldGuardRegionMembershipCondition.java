package com.nisovin.magicspells.castmodifiers.conditions;

import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.castmodifiers.conditions.util.DependsOn;
import com.nisovin.magicspells.castmodifiers.conditions.util.AbstractWorldGuardCondition;

@DependsOn(plugin = "WorldGuard")
public class WorldGuardRegionMembershipCondition extends AbstractWorldGuardCondition {

	private Type type = Type.MEMBER;

	@Override
	public boolean initialize(String var) {
		if (var == null || var.isEmpty()) return true;
		try {
			type = Type.valueOf(var.toUpperCase());
		} catch (IllegalArgumentException ignored) {
			MagicSpells.error("Invalid type defined on 'worldguardmembership': " + var);
			return false;
		}
		return true;
	}

	@Override
	protected boolean check(ProtectedRegion region, LocalPlayer player) {
		return switch (type) {
			case OWNER -> region.isOwner(player);
			case MEMBER -> region.isMember(player);
			case MEMBER_ONLY -> region.isMemberOnly(player);
		};
	}

	private enum Type {
		OWNER,
		MEMBER,
		MEMBER_ONLY
	}
	
}
