package com.nisovin.magicspells.spells.targeted;

import java.util.Set;
import java.util.List;

import org.bukkit.entity.LivingEntity;

import com.nisovin.magicspells.util.*;
import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.spells.TargetedSpell;
import com.nisovin.magicspells.util.config.ConfigData;
import com.nisovin.magicspells.spells.TargetedEntitySpell;
import com.nisovin.magicspells.util.managers.AttributeManager;

public class EntityEditSpell extends TargetedSpell implements TargetedEntitySpell {

	private Set<AttributeManager.AttributeInfo> attributes;

	private ConfigData<Boolean> toggle;

	public EntityEditSpell(MagicConfig config, String spellName) {
		super(config, spellName);

		// Attributes
		// - [AttributeName] [Number] [Operation]
		List<String> attributeList = getConfigStringList("attributes", null);
		if (attributeList != null && !attributeList.isEmpty())
			attributes = MagicSpells.getAttributeManager().getAttributes(attributeList);

		toggle = getConfigDataBoolean("toggle", false);
	}

	@Override
	public CastResult cast(SpellData data) {
		TargetInfo<LivingEntity> info = getTargetedEntity(data);
		if (info.noTarget()) return noTarget(info);

		return castAtEntity(info.spellData());
	}

	@Override
	public CastResult castAtEntity(SpellData data) {
		if (attributes == null) return new CastResult(PostCastAction.ALREADY_HANDLED, data);

		LivingEntity target = data.target();

		if (toggle.get(data)) {
			boolean apply = false;
			for (AttributeManager.AttributeInfo info : attributes)
				apply = MagicSpells.getAttributeManager().hasEntityAttribute(target, info);

			if (!apply) MagicSpells.getAttributeManager().addEntityAttributes(target, attributes);
			else MagicSpells.getAttributeManager().clearEntityAttributeModifiers(target, attributes);

			playSpellEffects(data);
			return new CastResult(PostCastAction.HANDLE_NORMALLY, data);
		}

		for (AttributeManager.AttributeInfo info : attributes) {
			if (MagicSpells.getAttributeManager().hasEntityAttribute(target, info)) continue;
			MagicSpells.getAttributeManager().addEntityAttribute(target, info);
		}

		playSpellEffects(data);
		return new CastResult(PostCastAction.HANDLE_NORMALLY, data);
	}

}
