package com.nisovin.magicspells.spells;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;

import com.nisovin.magicspells.util.SpellData;
import com.nisovin.magicspells.util.CastResult;
import com.nisovin.magicspells.Spell.PostCastAction;

public interface TargetedEntityFromLocationSpell {

	default CastResult castAtEntityFromLocation(SpellData data) {
		boolean success = data.hasCaster() ?
			castAtEntityFromLocation(data.caster(), data.location(), data.target(), data.power()) :
			castAtEntityFromLocation(data.location(), data.target(), data.power());

		return new CastResult(success ? PostCastAction.HANDLE_NORMALLY : PostCastAction.ALREADY_HANDLED, data);
	}

	@Deprecated
	default boolean castAtEntityFromLocation(LivingEntity caster, Location from, LivingEntity target, float power) {
		return castAtEntityFromLocation(new SpellData(caster, target, power, null)).action() != PostCastAction.ALREADY_HANDLED;
	}

	@Deprecated
	default boolean castAtEntityFromLocation(Location from, LivingEntity target, float power) {
		return castAtEntityFromLocation(new SpellData(null, target, power, null)).action() != PostCastAction.ALREADY_HANDLED;
	}

}
