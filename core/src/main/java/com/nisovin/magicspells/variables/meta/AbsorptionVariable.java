package com.nisovin.magicspells.variables.meta;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.nisovin.magicspells.variables.variabletypes.MetaVariable;

public class AbsorptionVariable extends MetaVariable {

    @Override
    public double getValue(String p) {
        Player player = Bukkit.getPlayerExact(p);
        if (player == null) return 0D;
        return player.getAbsorptionAmount();
    }

    @Override
    public void set(String p, double amount) {
        Player player = Bukkit.getPlayerExact(p);
        if (player == null) return;
        player.setAbsorptionAmount(amount);
    }

}
