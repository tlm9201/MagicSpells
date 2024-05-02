package com.nisovin.magicspells.variables.meta;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.nisovin.magicspells.variables.variabletypes.MetaVariable;

public class RespawnCoordZVariable extends MetaVariable {

	@Override
	public double getValue(String player) {
		Player p = Bukkit.getPlayerExact(player);
		if (p == null) return 0D;

		Location loc = p.getRespawnLocation();
		if (loc != null) return loc.getZ();
		return p.getWorld().getSpawnLocation().getZ();
	}

	@Override
	public void set(String player, double amount) {
		Player p = Bukkit.getPlayerExact(player);
		if (p == null) return;

		Location to = p.getRespawnLocation();
		if (to == null) return;
		to.setZ(amount);
		p.setRespawnLocation(to, true);
	}

}
