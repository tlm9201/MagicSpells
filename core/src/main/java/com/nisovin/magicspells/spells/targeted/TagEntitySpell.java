package com.nisovin.magicspells.spells.targeted;

import java.util.Set;

import org.bukkit.entity.LivingEntity;

import com.nisovin.magicspells.util.*;
import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.spells.TargetedSpell;
import com.nisovin.magicspells.util.config.ConfigData;
import com.nisovin.magicspells.spells.TargetedEntitySpell;

public class TagEntitySpell extends TargetedSpell implements TargetedEntitySpell {

	private final ConfigData<String> operation;
	private final ConfigData<String> tag;

	public TagEntitySpell(MagicConfig config, String spellName) {
		super(config, spellName);

		tag = getConfigDataString("tag", null);
		operation = getConfigDataString("operation", "add");
	}

	@Override
	public CastResult cast(SpellData data) {
		TargetInfo<LivingEntity> info = getTargetedEntity(data);
		if (info.noTarget()) return noTarget(info);

		return castAtEntity(info.spellData());
	}

	@Override
	public CastResult castAtEntity(SpellData data) {
		String operation = this.operation.get(data).toLowerCase();
		if (operation.equals("clear")) {
			Set<String> tags = data.target().getScoreboardTags();
			tags.forEach(data.target()::removeScoreboardTag);

			playSpellEffects(data);
			return new CastResult(PostCastAction.HANDLE_NORMALLY, data);
		}

		String tag = this.tag.get(data);
		if (tag == null) return new CastResult(PostCastAction.ALREADY_HANDLED, data);

		switch (operation) {
			case "add", "insert" -> data.target().addScoreboardTag(tag);
			case "remove", "take" -> data.target().removeScoreboardTag(tag);
			default -> {
				MagicSpells.error("TagEntitySpell '" + internalName + "' has an invalid operation defined!");
				return new CastResult(PostCastAction.ALREADY_HANDLED, data);
			}
		}

		playSpellEffects(data);
		return new CastResult(PostCastAction.HANDLE_NORMALLY, data);
	}

}
