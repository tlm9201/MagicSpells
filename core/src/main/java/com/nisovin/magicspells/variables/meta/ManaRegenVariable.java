package com.nisovin.magicspells.variables.meta;

import org.bukkit.entity.Player;

import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.mana.ManaHandler;
import com.nisovin.magicspells.util.PlayerNameUtils;
import com.nisovin.magicspells.variables.variabletypes.MetaVariable;

public class ManaRegenVariable extends MetaVariable {

	@Override
	public double getValue(String player) {
		ManaHandler handler = MagicSpells.getManaHandler();
		if (handler == null) return 0d;

		Player p = PlayerNameUtils.getPlayerExact(player);
		if (p == null) return 0d;

		return handler.getRegenAmount(p);
	}

	@Override
	public void set(String player, double amount) {
		ManaHandler handler = MagicSpells.getManaHandler();
		if (handler == null) return;

		Player p = PlayerNameUtils.getPlayerExact(player);
		if (p == null) return;

		handler.setRegenAmount(p, (int) amount);
	}

}
