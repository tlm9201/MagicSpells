package com.nisovin.magicspells.variables.meta;

import org.bukkit.entity.Player;

import com.nisovin.magicspells.util.PlayerNameUtils;
import com.nisovin.magicspells.variables.variabletypes.MetaVariable;

public class SidewaysMovementVariable extends MetaVariable {

	@Override
	public double getValue(String p) {
		Player player = PlayerNameUtils.getPlayerExact(p);
		if (player == null) return 0D;
		return player.getSidewaysMovement();
	}

}
