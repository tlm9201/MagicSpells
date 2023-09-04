package com.nisovin.magicspells.spells.instant;

import org.bukkit.entity.Player;

import com.nisovin.magicspells.util.*;
import com.nisovin.magicspells.spells.InstantSpell;
import com.nisovin.magicspells.spells.TargetedEntitySpell;

public class EnderchestSpell extends InstantSpell implements TargetedEntitySpell {

	public EnderchestSpell(MagicConfig config, String spellName) {
		super(config, spellName);
	}

	@Override
	public CastResult cast(SpellData data) {
		if (!(data.caster() instanceof Player caster)) return new CastResult(PostCastAction.ALREADY_HANDLED, data);

		if (data.hasArgs() && data.args().length == 1 && caster.hasPermission("magicspells.advanced." + internalName)) {
			Player target = PlayerNameUtils.getPlayer(data.args()[0]);
			if (target == null) {
				sendMessage(caster, "Invalid player target.");
				return new CastResult(PostCastAction.ALREADY_HANDLED, data);
			}

			data = data.target(target);
			caster.openInventory(target.getEnderChest());
		} else caster.openInventory(caster.getEnderChest());

		playSpellEffects(data);
		return new CastResult(PostCastAction.HANDLE_NORMALLY, data);
	}

	@Override
	public CastResult castAtEntity(SpellData data) {
		if (!(data.caster() instanceof Player caster) || !(data.target() instanceof Player target))
			return new CastResult(PostCastAction.ALREADY_HANDLED, data);

		caster.openInventory(target.getEnderChest());
		playSpellEffects(data);

		return new CastResult(PostCastAction.HANDLE_NORMALLY, data);
	}

}
