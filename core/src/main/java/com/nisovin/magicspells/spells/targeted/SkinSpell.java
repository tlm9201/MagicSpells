package com.nisovin.magicspells.spells.targeted;

import org.bukkit.entity.Player;

import com.nisovin.magicspells.util.*;
import com.nisovin.magicspells.spells.TargetedSpell;
import com.nisovin.magicspells.util.config.ConfigData;
import com.nisovin.magicspells.spells.TargetedEntitySpell;

public class SkinSpell extends TargetedSpell implements TargetedEntitySpell {

	private ConfigData<String> texture;
	private ConfigData<String> signature;

	public SkinSpell(MagicConfig config, String spellName) {
		super(config, spellName);

		texture = getConfigDataString("texture", null);
		signature = getConfigDataString("signature", null);
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

		String texture = this.texture.get(data);
		if (texture == null) return new CastResult(PostCastAction.ALREADY_HANDLED, data);

		Util.setSkin(target, texture, signature.get(data));
		playSpellEffects(data);

		return new CastResult(PostCastAction.HANDLE_NORMALLY, data);
	}

}
