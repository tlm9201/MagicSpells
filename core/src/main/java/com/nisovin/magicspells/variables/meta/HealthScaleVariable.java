package com.nisovin.magicspells.variables.meta;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.nisovin.magicspells.util.SpellUtil;
import com.nisovin.magicspells.variables.variabletypes.MetaVariable;

public class HealthScaleVariable extends MetaVariable {
	
	@Override
	public double getValue(String player) {
		Player p = Bukkit.getPlayerExact(player);
		if (p != null) return p.getHealthScale();
		return 0D;
	}
	
	@Override
	public void set(String player, double amount) {
		Player p = Bukkit.getPlayerExact(player);
		if (p == null) return;

		p.setHealthScale(amount);
		SpellUtil.updateManaBar(p);
	}

}
