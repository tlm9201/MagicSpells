package com.nisovin.magicspells.spells.targeted;

import java.util.List;
import java.util.Arrays;

import org.bukkit.entity.LivingEntity;

import com.nisovin.magicspells.util.*;
import com.nisovin.magicspells.spells.TargetedSpell;
import com.nisovin.magicspells.spells.TargetedEntitySpell;
import com.nisovin.magicspells.spells.targeted.cleanse.util.Cleansers;

public class CleanseSpell extends TargetedSpell implements TargetedEntitySpell {

	private Cleansers cleansers;

	private final List<String> cleanseList;

	public CleanseSpell(MagicConfig config, String spellName) {
		super(config, spellName);

		cleanseList = getConfigStringList("remove", Arrays.asList("fire", "hunger", "poison", "wither"));
	}

	@Override
	public void initialize() {
		super.initialize();

		cleansers = new Cleansers(cleanseList, internalName);
	}

	@Override
	public CastResult cast(SpellData data) {
		TargetInfo<LivingEntity> info = getTargetedEntity(data, cleansers.getChecker());
		if (info.noTarget()) return noTarget(info);
		data = info.spellData();

		return castAtEntity(data);
	}

	@Override
	public CastResult castAtEntity(SpellData data) {
		cleansers.cleanse(data.target());
		playSpellEffects(data);
		return new CastResult(PostCastAction.HANDLE_NORMALLY, data);
	}

	@Override
	public ValidTargetChecker getValidTargetChecker() {
		return cleansers.getChecker();
	}

}
