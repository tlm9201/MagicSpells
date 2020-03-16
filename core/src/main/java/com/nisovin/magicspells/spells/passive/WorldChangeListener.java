package com.nisovin.magicspells.spells.passive;

import com.nisovin.magicspells.Spellbook;
import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.spells.PassiveSpell;
import com.nisovin.magicspells.util.OverridePriority;

import org.bukkit.World;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;

public class WorldChangeListener extends PassiveListener {

    private Map<String, List<PassiveSpell>> spellsSpecific = new HashMap<>();
    private List<PassiveSpell> spellsAll = new ArrayList<>();

    @Override
    public void registerSpell(PassiveSpell spell, PassiveTrigger trigger, String var) {
        if (var == null || var.isEmpty()) {
            spellsAll.add(spell);
        } else {
            List<PassiveSpell> spells;
            for (String world : var.split(",")) {
                world = world.trim();
                spells = spellsSpecific.getOrDefault(world, new ArrayList<>());
                spells.add(spell);
                spellsSpecific.put(var, spells);
            }
        }
    }

    @OverridePriority
    @EventHandler
    public void onWorldChange(PlayerTeleportEvent event) {
        World worldFrom = event.getFrom().getWorld();
        if(worldFrom == null) return;
        Location locTo = event.getTo();
        if(locTo == null) return;
        World worldTo = locTo.getWorld();
        if(worldTo == null) return;
        if(worldFrom.equals(worldTo)) return;

        Spellbook spellbook;
        if (!spellsAll.isEmpty()) {
            spellbook = MagicSpells.getSpellbook(event.getPlayer());
            for (PassiveSpell spell : spellsAll) {
                if (!isCancelStateOk(spell, event.isCancelled())) continue;
                if (!spellbook.hasSpell(spell)) continue;
                boolean casted = spell.activate(event.getPlayer());
                if (PassiveListener.cancelDefaultAction(spell, casted)) event.setCancelled(true);
            }
        }

        if (!spellsSpecific.containsKey(worldTo.getName())) return;
        spellbook = MagicSpells.getSpellbook(event.getPlayer());
        for (PassiveSpell spell : spellsSpecific.get(worldTo.getName())) {
            if (!isCancelStateOk(spell, event.isCancelled())) continue;
            if (!spellbook.hasSpell(spell)) continue;
            boolean casted = spell.activate(event.getPlayer());
            if (PassiveListener.cancelDefaultAction(spell, casted)) event.setCancelled(true);
        }
    }
}
