package com.nisovin.magicspells.spells.targeted;

import org.bukkit.entity.LivingEntity;

import com.nisovin.magicspells.util.*;
import com.nisovin.magicspells.spells.TargetedSpell;
import com.nisovin.magicspells.util.config.ConfigData;
import com.nisovin.magicspells.spells.TargetedEntitySpell;

public class SneakSpell extends TargetedSpell implements TargetedEntitySpell {

	private final ConfigData<TargetBooleanState> targetState;

	public SneakSpell(MagicConfig config, String spellName) {
		super(config, spellName);

		targetState = getConfigDataTargetBooleanState("target-state", TargetBooleanState.TOGGLE);
	}

	@Override
	public CastResult cast(SpellData data) {
		TargetInfo<LivingEntity> info = getTargetedEntity(data);
		if (info.noTarget()) return noTarget(info);

		return castAtEntity(info.spellData());
	}

	@Override
	public CastResult castAtEntity(SpellData data) {
		TargetBooleanState targetState = this.targetState.get(data);
		data.target().setSneaking(targetState.getBooleanState(data.target().isSneaking()));

		playSpellEffects(data);
		return new CastResult(PostCastAction.HANDLE_NORMALLY, data);
	}

}
