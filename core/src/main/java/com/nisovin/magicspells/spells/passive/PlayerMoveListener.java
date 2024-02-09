package com.nisovin.magicspells.spells.passive;

import org.jetbrains.annotations.NotNull;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;

import com.nisovin.magicspells.util.Name;
import com.nisovin.magicspells.util.LocationUtil;
import com.nisovin.magicspells.handlers.DebugHandler;
import com.nisovin.magicspells.util.OverridePriority;
import com.nisovin.magicspells.spells.passive.util.PassiveListener;

@Name("playermove")
public class PlayerMoveListener extends PassiveListener {

	private double tolerance = -1;

	@Override
	public void initialize(@NotNull String var) {
		if (var.isEmpty()) return;
		try {
			tolerance = Double.parseDouble(var);
		} catch (NumberFormatException e) {
			DebugHandler.debugNumberFormat(e);
		}
	}

	@OverridePriority
	@EventHandler
	public void onMove(PlayerMoveEvent event) {
		if (!isCancelStateOk(event.isCancelled())) return;

		Player caster = event.getPlayer();
		if (!canTrigger(caster)) return;
		if (tolerance >= 0 && LocationUtil.distanceLessThan(event.getFrom(), event.getTo(), tolerance)) return;

		boolean casted = passiveSpell.activate(caster);
		if (cancelDefaultAction(casted)) event.setCancelled(true);
	}

}
