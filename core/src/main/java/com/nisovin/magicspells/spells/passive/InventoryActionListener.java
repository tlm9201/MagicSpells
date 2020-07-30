package com.nisovin.magicspells.spells.passive;

import java.util.List;
import java.util.ArrayList;

import org.bukkit.entity.Player;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

import com.nisovin.magicspells.Spellbook;
import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.spells.PassiveSpell;
import com.nisovin.magicspells.util.OverridePriority;

public class InventoryActionListener extends PassiveListener {

	List<PassiveSpell> inventoryOpen = new ArrayList<>();
	List<PassiveSpell> inventoryClose = new ArrayList<>();

	@Override
	public void registerSpell(PassiveSpell spell, PassiveTrigger trigger, String var) {
		if (var == null || var.isEmpty()) {
			inventoryOpen.add(spell);
			inventoryClose.add(spell);
			return;
		}

		String[] split = var.replace(" ", "").split(",");
		for (String s : split) {
			switch (s.toLowerCase()) {
				case "open":
					inventoryOpen.add(spell);
					break;
				case "close":
					inventoryClose.add(spell);
					break;
			}
		}
	}

	@OverridePriority
	@EventHandler
	public void onInventoryOpen(InventoryOpenEvent event) {
		HumanEntity humanEntity = event.getPlayer();
		if (!(humanEntity instanceof Player)) return;
		Player player = (Player) humanEntity;

		Spellbook spellbook = MagicSpells.getSpellbook(player);
		if (inventoryOpen.isEmpty()) return;
		for (PassiveSpell spell : inventoryOpen) {
			if (!isCancelStateOk(spell, event.isCancelled())) continue;
			if (!spellbook.hasSpell(spell, false)) continue;
			boolean casted = spell.activate(player);
			if (!PassiveListener.cancelDefaultAction(spell, casted)) continue;
			event.setCancelled(true);
		}
	}

	@OverridePriority
	@EventHandler
	public void onInventoryClose(InventoryCloseEvent event) {
		HumanEntity humanEntity = event.getPlayer();
		if (!(humanEntity instanceof Player)) return;
		Player player = (Player) humanEntity;

		Spellbook spellbook = MagicSpells.getSpellbook(player);
		if (inventoryClose.isEmpty()) return;
		for (PassiveSpell spell : inventoryClose) {
			if (!spellbook.hasSpell(spell, false)) continue;
			spell.activate(player);
		}
	}

}
