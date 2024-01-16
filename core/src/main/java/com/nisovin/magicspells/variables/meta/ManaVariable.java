package com.nisovin.magicspells.variables.meta;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.mana.ManaHandler;
import com.nisovin.magicspells.mana.ManaChangeReason;
import com.nisovin.magicspells.variables.variabletypes.MetaVariable;

public class ManaVariable extends MetaVariable {

	@Override
	public double getValue(String player) {
		ManaHandler handler = MagicSpells.getManaHandler();
		if (handler == null) return 0d;

		Player p = Bukkit.getPlayerExact(player);
		if (p == null) return 0d;

		return handler.getMana(p);
	}

	@Override
	public void set(String player, double amount) {
		ManaHandler handler = MagicSpells.getManaHandler();
		if (handler == null) return;

		Player p = Bukkit.getPlayerExact(player);
		if (p == null) return;

		handler.setMana(p, (int) amount, ManaChangeReason.OTHER);
	}

}
