package com.nisovin.magicspells.variables.meta;

import com.nisovin.magicspells.util.PlayerNameUtils;
import com.nisovin.magicspells.variables.MetaVariable;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class AltitudeVariable extends MetaVariable {

    @Override
    public double getValue(String player) {
        Player p = PlayerNameUtils.getPlayerExact(player);
        if(p != null) {
            Location location = p.getLocation();
            World world = location.getWorld();
            if(world != null) return location.getY() - world.getHighestBlockYAt(location);
        }
        return 0;
    }

}
