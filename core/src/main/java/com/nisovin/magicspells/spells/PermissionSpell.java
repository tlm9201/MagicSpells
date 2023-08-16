package com.nisovin.magicspells.spells;

import java.util.List;

import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.util.SpellData;
import com.nisovin.magicspells.util.CastResult;
import com.nisovin.magicspells.util.MagicConfig;
import com.nisovin.magicspells.util.config.ConfigData;

public class PermissionSpell extends InstantSpell {

	private final ConfigData<Integer> duration;

	private final List<String> permissionNodes;

	public PermissionSpell(MagicConfig config, String spellName) {
		super(config, spellName);

		duration = getConfigDataInt("duration", 0);
		permissionNodes = getConfigStringList("permission-nodes", null);
	}

	@Override
	public CastResult cast(SpellData data) {
		if (permissionNodes == null) return new CastResult(PostCastAction.ALREADY_HANDLED, data);

		int duration = this.duration.get(data);
		if (duration <= 0) return new CastResult(PostCastAction.ALREADY_HANDLED, data);

		for (String node : permissionNodes)
			data.caster().addAttachment(MagicSpells.plugin, node, true, duration);

		playSpellEffects(data);
		return new CastResult(PostCastAction.HANDLE_NORMALLY, data);
	}

}
