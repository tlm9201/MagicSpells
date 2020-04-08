package com.nisovin.magicspells.spells.passive;

import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.event.inventory.CraftItemEvent;

import com.nisovin.magicspells.util.Util;
import com.nisovin.magicspells.Spellbook;
import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.spells.PassiveSpell;
import com.nisovin.magicspells.util.OverridePriority;

public class CraftListener extends PassiveListener {

	List<PassiveSpell> spellsAll = new ArrayList<>();
	Map<ItemStack, List<PassiveSpell>> spellsSpecial = new HashMap<>();

	@Override
	public void registerSpell(PassiveSpell spell, PassiveTrigger trigger, String var) {
		if (var == null || var.isEmpty()) {
			spellsAll.add(spell);
		} else {
			for (String itemString : var.split(",")) {
				ItemStack item = Util.getItemStackFromString(itemString.trim());
				// Stop processing this item if it couldn't be created.
				if (item == null) continue;
				List<PassiveSpell> spells = spellsSpecial.getOrDefault(item, new ArrayList<>());
				spells.add(spell);
				spellsSpecial.put(item, spells);
			}
		}
	}

	@OverridePriority
	@EventHandler
	public void onCraft(CraftItemEvent event) {
		ItemStack item = event.getCurrentItem();
		if (item == null || item.getType() == Material.AIR) return;
		Player player = (Player) event.getWhoClicked();

		Spellbook spellbook = MagicSpells.getSpellbook(player);

		if (!spellsAll.isEmpty()) {
			for (PassiveSpell spell : spellsAll) {
				if (!isCancelStateOk(spell, event.isCancelled())) continue;
				if (!spellbook.hasSpell(spell)) continue;
				boolean casted = spell.activate(player);
				if (PassiveListener.cancelDefaultAction(spell, casted)) event.setCancelled(true);
			}
		}

		if (!spellsSpecial.isEmpty()) {
			for (PassiveSpell spell : spellsSpecial.get(item)) {
				if (!isCancelStateOk(spell, event.isCancelled())) continue;
				if (!spellbook.hasSpell(spell)) continue;
				boolean casted = spell.activate(player);
				if (PassiveListener.cancelDefaultAction(spell, casted)) event.setCancelled(true);
			}
		}
	}
}
