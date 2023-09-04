package com.nisovin.magicspells.spells;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;

import com.nisovin.magicspells.util.SpellData;
import com.nisovin.magicspells.util.CastResult;
import com.nisovin.magicspells.Spell.PostCastAction;

public interface TargetedLocationSpell {

	default CastResult castAtLocation(SpellData data) {
		boolean success = data.hasCaster() ?
			castAtLocation(data.caster(), data.location(), data.power()) :
			castAtLocation(data.location(), data.power());

		return new CastResult(success ? PostCastAction.HANDLE_NORMALLY : PostCastAction.ALREADY_HANDLED, data);
	}

	@Deprecated
	default boolean castAtLocation(LivingEntity caster, Location target, float power) {
		return castAtLocation(new SpellData(caster, target, power, null)).action() != PostCastAction.ALREADY_HANDLED;
	}

	@Deprecated
	default boolean castAtLocation(Location target, float power) {
		return castAtLocation(new SpellData(null, target, power, null)).action() != PostCastAction.ALREADY_HANDLED;
	}

}
