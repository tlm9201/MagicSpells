package com.nisovin.magicspells.spells.targeted;

import org.bukkit.entity.Ageable;
import org.bukkit.entity.Breedable;
import org.bukkit.entity.LivingEntity;

import com.nisovin.magicspells.util.*;
import com.nisovin.magicspells.spells.TargetedSpell;
import com.nisovin.magicspells.util.config.ConfigData;
import com.nisovin.magicspells.spells.TargetedEntitySpell;

public class AgeSpell extends TargetedSpell implements TargetedEntitySpell {

	private static final ValidTargetChecker AGEABLE = entity -> entity instanceof Ageable;

	private final ConfigData<Integer> rawAge;

	private final ConfigData<Boolean> setMaturity;
	private final ConfigData<Boolean> applyAgeLock;

	public AgeSpell(MagicConfig config, String spellName) {
		super(config, spellName);

		rawAge = getConfigDataInt("age", 0);
		setMaturity = getConfigDataBoolean("set-maturity", true);
		applyAgeLock = getConfigDataBoolean("apply-age-lock", false);
	}

	@Override
	public CastResult cast(SpellData data) {
		TargetInfo<LivingEntity> info = getTargetedEntity(data, AGEABLE);
		if (info.noTarget()) return noTarget(info);
		data = info.spellData();

		applyAgeChanges((Ageable) data.target(), data);
		return new CastResult(PostCastAction.HANDLE_NORMALLY, data);
	}

	@Override
	public CastResult castAtEntity(SpellData data) {
		if (!(data.target() instanceof Ageable ageable)) return noTarget(data);

		applyAgeChanges(ageable, data);
		return new CastResult(PostCastAction.HANDLE_NORMALLY, data);
	}

	private void applyAgeChanges(Ageable target, SpellData data) {
		if (setMaturity.get(data)) target.setAge(rawAge.get(data));

		boolean applyAgeLock = this.applyAgeLock.get(data);
		if (applyAgeLock && target instanceof Breedable breedable) breedable.setAgeLock(true);

		playSpellEffects(data);
	}

}
