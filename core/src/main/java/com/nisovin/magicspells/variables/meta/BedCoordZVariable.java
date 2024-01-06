package com.nisovin.magicspells.variables.meta;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.nisovin.magicspells.variables.variabletypes.MetaVariable;

public class BedCoordZVariable extends MetaVariable {

	@Override
	public double getValue(String player) {
		Player p = Bukkit.getPlayerExact(player);
		if (p == null) return 0D;

		Location bedSpawnLocation = p.getBedSpawnLocation();
		if (bedSpawnLocation != null) return bedSpawnLocation.getZ();
		return p.getWorld().getSpawnLocation().getZ();
	}
	
	@Override
	public void set(String player, double amount) {
		Player p = Bukkit.getPlayerExact(player);
		if (p == null) return;

		Location to = p.getBedSpawnLocation();
		if (to == null) return;
		to.setZ(amount);
		p.setBedSpawnLocation(to, true);
	}

}
