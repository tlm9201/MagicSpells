package com.nisovin.magicspells.variables.meta;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.nisovin.magicspells.variables.variabletypes.MetaVariable;

public class RespawnCoordXVariable extends MetaVariable {

	@Override
	public double getValue(String player) {
		Player p = Bukkit.getPlayerExact(player);
		if (p == null) return 0D;

		Location loc = p.getRespawnLocation();
		if (loc != null) return loc.getX();
		return p.getWorld().getSpawnLocation().getX();
	}

	@Override
	public void set(String player, double amount) {
		Player p = Bukkit.getPlayerExact(player);
		if (p == null) return;

		Location to = p.getRespawnLocation();
		if (to == null) return;
		to.setX(amount);
		p.setRespawnLocation(to, true);
	}

}
