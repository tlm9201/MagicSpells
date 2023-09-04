package com.nisovin.magicspells.spells.targeted;

import org.bukkit.entity.LivingEntity;

import com.nisovin.magicspells.util.*;
import com.nisovin.magicspells.spells.TargetedSpell;
import com.nisovin.magicspells.util.config.ConfigData;
import com.nisovin.magicspells.spells.TargetedEntitySpell;

public class CollisionSpell extends TargetedSpell implements TargetedEntitySpell {

	private final ConfigData<TargetBooleanState> targetBooleanState;

	public CollisionSpell(MagicConfig config, String spellName) {
		super(config, spellName);

		targetBooleanState = getConfigDataTargetBooleanState("target-state", TargetBooleanState.TOGGLE);
	}

	@Override
	public CastResult cast(SpellData data) {
		TargetInfo<LivingEntity> info = getTargetedEntity(data);
		if (info.noTarget()) return noTarget(info);

		return castAtEntity(info.spellData());
	}

	@Override
	public CastResult castAtEntity(SpellData data) {
		LivingEntity target = data.target();

		TargetBooleanState targetBooleanState = this.targetBooleanState.get(data);
		target.setCollidable(targetBooleanState.getBooleanState(target.isCollidable()));

		playSpellEffects(data);
		return new CastResult(PostCastAction.HANDLE_NORMALLY, data);
	}

}
