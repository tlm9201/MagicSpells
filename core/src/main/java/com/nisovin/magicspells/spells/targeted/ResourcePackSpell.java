package com.nisovin.magicspells.spells.targeted;

import org.bukkit.entity.Player;

import net.kyori.adventure.text.Component;

import com.nisovin.magicspells.util.*;
import com.nisovin.magicspells.spells.TargetedSpell;
import com.nisovin.magicspells.handlers.DebugHandler;
import com.nisovin.magicspells.util.config.ConfigData;
import com.nisovin.magicspells.spells.TargetedEntitySpell;

public class ResourcePackSpell extends TargetedSpell implements TargetedEntitySpell {

	private static final int HASH_LENGTH = 40;

	private final ConfigData<String> url;
	private final ConfigData<String> hash;
	private final ConfigData<Boolean> required;
	private final ConfigData<Component> prompt;

	public ResourcePackSpell(MagicConfig config, String spellName) {
		super(config, spellName);

		url = getConfigDataString("url", null);
		hash = getConfigDataString("hash", null);
		prompt = getConfigDataComponent("prompt", Component.empty());
		required = getConfigDataBoolean("required", false);
	}

	@Override
	public CastResult cast(SpellData data) {
		TargetInfo<Player> info = getTargetedPlayer(data);
		if (info.noTarget()) return noTarget(info);

		return castAtEntity(info.spellData());
	}

	@Override
	public CastResult castAtEntity(SpellData data) {
		if (!(data.target() instanceof Player player)) return noTarget(data);

		String url = this.url.get(data);
		if (url == null) return new CastResult(PostCastAction.ALREADY_HANDLED, data);

		String hash = this.hash.get(data);
		if (hash == null || hash.length() != HASH_LENGTH) return new CastResult(PostCastAction.ALREADY_HANDLED, data);

		try {
			player.setResourcePack(url, hash, required.get(data), prompt.get(data));
		} catch (IllegalArgumentException e) {
			DebugHandler.debugIllegalArgumentException(e);
			return new CastResult(PostCastAction.ALREADY_HANDLED, data);
		}

		playSpellEffects(data);
		return new CastResult(PostCastAction.HANDLE_NORMALLY, data);
	}

}
