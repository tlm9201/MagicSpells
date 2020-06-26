package com.nisovin.magicspells.spells.passive;

import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.event.player.PlayerItemHeldEvent;

import com.nisovin.magicspells.Spellbook;
import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.spells.PassiveSpell;
import com.nisovin.magicspells.util.OverridePriority;
import com.nisovin.magicspells.util.magicitems.MagicItem;
import com.nisovin.magicspells.util.magicitems.MagicItems;
import com.nisovin.magicspells.util.magicitems.MagicItemData;

// Trigger variable is the item to trigger on
public class HotBarListener extends PassiveListener {

	private Set<Material> materials = new HashSet<>();
	private Map<MagicItemData, List<PassiveSpell>> select = new LinkedHashMap<>();
	private Map<MagicItemData, List<PassiveSpell>> deselect = new LinkedHashMap<>();
	
	@Override
	public void registerSpell(PassiveSpell spell, PassiveTrigger trigger, String var) {
		MagicItemData magicItemData = null;
		if (var != null) {
			MagicItem magicItem = MagicItems.getMagicItemFromString(var.trim());
			if (magicItem != null) magicItemData = magicItem.getMagicItemData();
		}

		if (magicItemData != null) {
			materials.add(magicItemData.getType());
			List<PassiveSpell> list = null;
			if (PassiveTrigger.HOT_BAR_SELECT.contains(trigger)) {
				list = select.computeIfAbsent(magicItemData, material -> new ArrayList<>());
			} else if (PassiveTrigger.HOT_BAR_DESELECT.contains(trigger)) {
				list = deselect.computeIfAbsent(magicItemData, material -> new ArrayList<>());
			}
			if (list != null) list.add(spell);
		}
	}
	
	@OverridePriority
	@EventHandler
	public void onPlayerScroll(PlayerItemHeldEvent event) {
		if (!deselect.isEmpty()) {
			ItemStack item = event.getPlayer().getInventory().getItem(event.getPreviousSlot());
			if (item != null && item.getType() != Material.AIR) {
				List<PassiveSpell> list = getSpells(item, deselect);
				if (list != null) {
					Spellbook spellbook = MagicSpells.getSpellbook(event.getPlayer());
					for (PassiveSpell spell : list) {
						if (!isCancelStateOk(spell, event.isCancelled())) continue;
						if (!spellbook.hasSpell(spell, false)) continue;
						boolean casted = spell.activate(event.getPlayer());
						if (!PassiveListener.cancelDefaultAction(spell, casted)) continue;
						event.setCancelled(true);
					}
				}
			}
		}
		if (!select.isEmpty()) {
			ItemStack item = event.getPlayer().getInventory().getItem(event.getNewSlot());
			if (item != null && item.getType() != Material.AIR) {
				List<PassiveSpell> list = getSpells(item, select);
				if (list != null) {
					Spellbook spellbook = MagicSpells.getSpellbook(event.getPlayer());
					for (PassiveSpell spell : list) {
						if (!isCancelStateOk(spell, event.isCancelled())) continue;
						if (!spellbook.hasSpell(spell, false)) continue;
						boolean casted = spell.activate(event.getPlayer());
						if (!PassiveListener.cancelDefaultAction(spell, casted)) continue;
						event.setCancelled(true);
					}
				}
			}
		}
	}
	
	private List<PassiveSpell> getSpells(ItemStack item, Map<MagicItemData, List<PassiveSpell>> map) {
		if (!materials.contains(item.getType())) return null;
		MagicItemData itemData = MagicItems.getMagicItemDataFromItemStack(item);
		if (itemData == null) return null;

		for (Map.Entry<MagicItemData, List<PassiveSpell>> entry : map.entrySet()) {
			if (entry.getKey().equals(itemData)) return entry.getValue();
		}
		return null;
	}

}
