package com.nisovin.magicspells.variables.meta;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.nisovin.magicspells.variables.variabletypes.MetaVariable;

public class BedCoordZVariable extends MetaVariable {

	@Override
	public double getValue(String player) {
		Player p = Bukkit.getPlayerExact(player);
		if (p == null || !p.isSleeping()) return 0D;
		return p.getBedLocation().getZ();
	}

}
