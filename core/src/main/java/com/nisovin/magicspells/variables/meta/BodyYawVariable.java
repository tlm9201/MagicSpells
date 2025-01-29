package com.nisovin.magicspells.variables.meta;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.nisovin.magicspells.variables.variabletypes.MetaVariable;

public class BodyYawVariable extends MetaVariable {

	@Override
	public double getValue(String player) {
		Player p = Bukkit.getPlayerExact(player);
		return p == null ? 0 : p.getBodyYaw();
	}
	
	@Override
	public void set(String player, double amount) {
		Player p = Bukkit.getPlayerExact(player);
		if (p != null) p.setBodyYaw((float) amount);
	}

}
