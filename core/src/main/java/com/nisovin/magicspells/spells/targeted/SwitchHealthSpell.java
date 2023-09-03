package com.nisovin.magicspells.spells.targeted;

import org.bukkit.entity.LivingEntity;

import com.nisovin.magicspells.util.*;
import com.nisovin.magicspells.spells.TargetedSpell;
import com.nisovin.magicspells.util.config.ConfigData;
import com.nisovin.magicspells.spells.TargetedEntitySpell;

public class SwitchHealthSpell extends TargetedSpell implements TargetedEntitySpell {

	private final ConfigData<Boolean> requireLesserHealthPercent;
	private final ConfigData<Boolean> requireGreaterHealthPercent;

	public SwitchHealthSpell(MagicConfig config, String spellName) {
		super(config, spellName);

		requireLesserHealthPercent = getConfigDataBoolean("require-lesser-health-percent", false);
		requireGreaterHealthPercent = getConfigDataBoolean("require-greater-health-percent", false);
	}

	@Override
	public CastResult cast(SpellData data) {
		TargetInfo<LivingEntity> info = getTargetedEntity(data);
		if (info.noTarget()) return noTarget(info);

		return castAtEntity(info.spellData());
	}

	@Override
	public CastResult castAtEntity(SpellData data) {
		if (!data.hasCaster()) return new CastResult(PostCastAction.ALREADY_HANDLED, data);

		double casterMax = Util.getMaxHealth(data.caster());
		double targetMax = Util.getMaxHealth(data.target());

		double casterPct = data.caster().getHealth() / casterMax;
		double targetPct = data.target().getHealth() / targetMax;

		if (requireGreaterHealthPercent.get(data) && casterPct <= targetPct) return noTarget(data);
		if (requireLesserHealthPercent.get(data) && casterPct >= targetPct) return noTarget(data);

		data.caster().setHealth(targetPct * casterMax);
		data.target().setHealth(casterPct * targetMax);

		playSpellEffects(data);
		return new CastResult(PostCastAction.HANDLE_NORMALLY, data);
	}

}
