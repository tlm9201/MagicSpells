package com.nisovin.magicspells.spells.targeted;

import java.util.List;
import java.util.Arrays;

import org.bukkit.entity.LivingEntity;

import com.nisovin.magicspells.util.*;
import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.spells.TargetedSpell;
import com.nisovin.magicspells.spells.TargetedEntitySpell;
import com.nisovin.magicspells.spells.targeted.cleanse.util.Cleanser;

public class CleanseSpell extends TargetedSpell implements TargetedEntitySpell {

	private List<Cleanser> cleansers;

	public CleanseSpell(MagicConfig config, String spellName) {
		super(config, spellName);
	}

	@Override
	public void initialize() {
		super.initialize();

		List<String> list = getConfigStringList("remove", Arrays.asList("fire", "hunger", "poison", "wither"));
		if (list == null) {
			MagicSpells.error("CleanseSpell '" + internalName + "' has no cleansers defined in 'remove'.");
			return;
		}

		cleansers = MagicSpells.getCleanserManager().createCleansers();
		outer:
		for (String string : list) {
			for (Cleanser cleanser : cleansers)
				if (cleanser.add(string))
					continue outer;

			MagicSpells.error("CleanseSpell '" + internalName + "' has an invalid cleanser '" + string + "' defined in 'remove'.");
		}
	}

	@Override
	public CastResult cast(SpellData data) {
		TargetInfo<LivingEntity> info = getTargetedEntity(data, getValidTargetChecker());
		if (info.noTarget()) return noTarget(info);
		return castAtEntity(info.spellData());
	}

	@Override
	public CastResult castAtEntity(SpellData data) {
		cleansers.forEach(cleanser -> cleanser.cleanse(data.target()));
		playSpellEffects(data);
		return new CastResult(PostCastAction.HANDLE_NORMALLY, data);
	}

	@Override
	public ValidTargetChecker getValidTargetChecker() {
		return entity -> {
			for (Cleanser cleanser : cleansers)
				if (cleanser.isAnyActive(entity))
					return true;
			return false;
		};
	}

}
