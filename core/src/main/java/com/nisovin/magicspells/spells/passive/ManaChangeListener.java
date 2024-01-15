package com.nisovin.magicspells.spells.passive;

import java.util.EnumSet;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import org.jetbrains.annotations.NotNull;

import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.util.OverridePriority;
import com.nisovin.magicspells.events.ManaChangeEvent;
import com.nisovin.magicspells.mana.ManaChangeReason;
import com.nisovin.magicspells.spells.passive.util.PassiveListener;

public class ManaChangeListener extends PassiveListener {

	private final EnumSet<ManaChangeReason> reasons = EnumSet.noneOf(ManaChangeReason.class);

	@Override
	public void initialize(@NotNull String var) {
		if (var.isEmpty()) return;
		for (String datum : var.split(",")) {
			try {
				reasons.add(ManaChangeReason.valueOf(datum.toUpperCase()));
			} catch (IllegalArgumentException e) {
				MagicSpells.error("Invalid mana change reason '" + datum + "' in manachange trigger on passive spell '" + passiveSpell.getName() + "'");
			}
		}
	}

	@OverridePriority
	@EventHandler
	public void onManaChange(ManaChangeEvent event) {
		if (!reasons.isEmpty() && !reasons.contains(event.getReason())) return;

		Player caster = event.getPlayer();
		if (!canTrigger(caster) || !hasSpell(caster)) return;

		boolean casted = passiveSpell.activate(caster);
		if (cancelDefaultAction(casted)) event.setNewAmount(event.getOldAmount());
	}

}
