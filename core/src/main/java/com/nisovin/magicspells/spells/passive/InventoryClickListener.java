package com.nisovin.magicspells.spells.passive;

import java.util.Set;
import java.util.HashSet;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;

import com.nisovin.magicspells.Spellbook;
import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.spells.PassiveSpell;
import com.nisovin.magicspells.util.OverridePriority;
import com.nisovin.magicspells.util.magicitems.MagicItem;
import com.nisovin.magicspells.util.magicitems.MagicItems;

public class InventoryClickListener extends PassiveListener {

    private Set<MagicClick> spells = new HashSet<>();

    @Override
    public void registerSpell(PassiveSpell spell, PassiveTrigger trigger, String var) {
        InventoryAction action = null;
        ItemStack itemCurrent = null;
        ItemStack itemCursor = null;
        if (var != null && !var.isEmpty()) {
            String[] splits = var.split(" ");
            if (!splits[0].equals("null")) action = InventoryAction.valueOf(splits[0].toUpperCase());
            if (splits.length > 1 && !splits[1].equals("null")) {
                MagicItem magicItem = MagicItems.getMagicItemFromString(splits[1]);
                if (magicItem != null) itemCurrent = magicItem.getItemStack();
            }
            if (splits.length > 2) {
                MagicItem magicItem = MagicItems.getMagicItemFromString(splits[2]);
                if (magicItem != null) itemCursor = magicItem.getItemStack();
            }
        }
        spells.add(new MagicClick(spell, action, itemCurrent, itemCursor));
    }

    @OverridePriority
    @EventHandler
    public void onInvClick(InventoryClickEvent event) {
        Player player = Bukkit.getPlayer(event.getWhoClicked().getUniqueId());
        if (player == null) return;
        Spellbook spellbook = MagicSpells.getSpellbook(player);
        for (MagicClick click : spells) {
            if (!spellbook.hasSpell(click.spell)) continue;
            // Valid action, but not used.
            if (click.action != null && !event.getAction().equals(click.action)) continue;
            // Valid clicked item, but not used.
            if (click.itemCurrent != null) {
                ItemStack item = event.getCurrentItem();
                if (item == null) continue;
                if (!item.isSimilar(click.itemCurrent)) continue;
            }
            // Valid cursor item, but not used.
            if (click.itemCursor != null) {
                ItemStack item = event.getCursor();
                if (item == null) continue;
                if (!item.isSimilar(click.itemCursor)) continue;
            }
            boolean casted = click.spell.activate(player);
            if (!PassiveListener.cancelDefaultAction(click.spell, casted)) continue;
            event.setCancelled(true);
        }
    }

    private static class MagicClick {

        InventoryAction action;
        PassiveSpell spell;
        ItemStack itemCurrent;
        ItemStack itemCursor;

        MagicClick(PassiveSpell spell, InventoryAction action, ItemStack itemCurrent, ItemStack itemCursor) {
            this.action = action;
            this.spell = spell;
            this.itemCurrent = itemCurrent;
            this.itemCursor = itemCursor;
        }
    }
}
