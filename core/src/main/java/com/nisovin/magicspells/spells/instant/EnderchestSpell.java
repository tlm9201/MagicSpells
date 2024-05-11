package com.nisovin.magicspells.spells.instant;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

import com.nisovin.magicspells.util.*;
import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.spells.InstantSpell;
import com.nisovin.magicspells.spells.TargetedEntitySpell;

public class EnderchestSpell extends InstantSpell implements TargetedEntitySpell {

	public EnderchestSpell(MagicConfig config, String spellName) {
		super(config, spellName);
	}

	@Override
	public CastResult cast(SpellData data) {
		if (!(data.caster() instanceof Player caster)) return new CastResult(PostCastAction.ALREADY_HANDLED, data);

		if (data.hasArgs() && data.args().length == 1 && MagicSpells.getSpellbook(caster).hasAdvancedPerm(internalName)) {
			Player target = Bukkit.getPlayer(data.args()[0]);
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

	@Override
	public List<String> tabComplete(CommandSender sender, String[] args) {
		if (args.length != 1) return null;

		if (sender instanceof Player player) {
			if (!MagicSpells.getSpellbook(player).hasAdvancedPerm(internalName)) return null;
		} else if (!(sender instanceof ConsoleCommandSender)) return null;

		return TxtUtil.tabCompletePlayerName(sender);
	}

}
