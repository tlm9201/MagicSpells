package com.nisovin.magicspells.spells.targeted;

import org.bukkit.entity.Player;

import com.nisovin.magicspells.util.*;
import com.nisovin.magicspells.spells.TargetedSpell;
import com.nisovin.magicspells.util.config.ConfigData;
import com.nisovin.magicspells.spells.TargetedEntitySpell;

public class SprintSpell extends TargetedSpell implements TargetedEntitySpell {

	private final ConfigData<TargetBooleanState> targetState;

	public SprintSpell(MagicConfig config, String spellName) {
		super(config, spellName);

		targetState = getConfigDataTargetBooleanState("target-state", TargetBooleanState.TOGGLE);
	}

	@Override
	public CastResult cast(SpellData data) {
		TargetInfo<Player> info = getTargetedPlayer(data);
		if (info.noTarget()) return noTarget(info);

		return castAtEntity(info.spellData());
	}

	@Override
	public CastResult castAtEntity(SpellData data) {
		if (!(data.target() instanceof Player target)) return noTarget(data);

		TargetBooleanState targetState = this.targetState.get(data);
		target.setSprinting(targetState.getBooleanState(target.isSprinting()));

		playSpellEffects(data);
		return new CastResult(PostCastAction.HANDLE_NORMALLY, data);
	}

}
