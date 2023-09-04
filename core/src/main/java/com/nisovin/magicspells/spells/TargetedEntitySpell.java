package com.nisovin.magicspells.spells;

import org.bukkit.entity.LivingEntity;

import com.nisovin.magicspells.util.SpellData;
import com.nisovin.magicspells.util.CastResult;
import com.nisovin.magicspells.Spell.PostCastAction;

public interface TargetedEntitySpell {

	default CastResult castAtEntity(SpellData data) {
		boolean success = data.hasCaster() ?
			castAtEntity(data.caster(), data.target(), data.power()) :
			castAtEntity(data.target(), data.power());

		return new CastResult(success ? PostCastAction.HANDLE_NORMALLY : PostCastAction.ALREADY_HANDLED, data);
	}

	@Deprecated
	default boolean castAtEntity(LivingEntity caster, LivingEntity target, float power) {
		return castAtEntity(new SpellData(caster, target, power, null)).action() != PostCastAction.ALREADY_HANDLED;
	}

	@Deprecated
	default boolean castAtEntity(LivingEntity target, float power) {
		return castAtEntity(new SpellData(null, target, power, null)).action() != PostCastAction.ALREADY_HANDLED;
	}

}
