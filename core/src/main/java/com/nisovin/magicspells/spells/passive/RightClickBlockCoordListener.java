package com.nisovin.magicspells.spells.passive;

import java.util.Set;
import java.util.HashSet;

import org.jetbrains.annotations.NotNull;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.Event;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;

import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.util.LocationUtil;
import com.nisovin.magicspells.util.OverridePriority;
import com.nisovin.magicspells.spells.passive.util.PassiveListener;

// Trigger variable is a semicolon separated list of locations to accept
// Locations follow the format of world,x,y,z
// Where "world" is a string and x, y, and z are integers
public class RightClickBlockCoordListener extends PassiveListener {

	private final Set<Location> locations = new HashSet<>();

	@Override
	public void initialize(@NotNull String var) {
		if (var.isEmpty()) return;
		for (String string : var.split(";")) {
			Location location = LocationUtil.fromString(string);
			if (location == null) {
				MagicSpells.error("Invalid coords on rightclickblockcoord trigger for spell '" + passiveSpell.getInternalName() + "': " + string);
				continue;
			}
			locations.add(location);
		}
	}

	@OverridePriority
	@EventHandler
	public void onRightClick(PlayerInteractEvent event) {
		if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
		if (!isCancelStateOk(isCancelled(event))) return;

		Block block = event.getClickedBlock();
		if (block == null) return;

		Player caster = event.getPlayer();
		if (!canTrigger(caster)) return;

		Location location = event.getClickedBlock().getLocation();
		if (!locations.contains(location)) return;

		boolean casted = passiveSpell.activate(caster, location.add(0.5, 0.5, 0.5));
		if (cancelDefaultAction(casted)) event.setCancelled(true);
	}

	private boolean isCancelled(PlayerInteractEvent event) {
		return event.useInteractedBlock() == Event.Result.DENY;
	}

}
