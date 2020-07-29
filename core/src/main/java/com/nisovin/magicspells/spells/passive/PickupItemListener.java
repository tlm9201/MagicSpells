package com.nisovin.magicspells.spells.passive;

import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.HashSet;
import java.util.HashMap;
import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.event.entity.EntityPickupItemEvent;

import com.nisovin.magicspells.Spellbook;
import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.spells.PassiveSpell;
import com.nisovin.magicspells.util.OverridePriority;
import com.nisovin.magicspells.util.magicitems.MagicItem;
import com.nisovin.magicspells.util.magicitems.MagicItems;
import com.nisovin.magicspells.util.magicitems.MagicItemData;

// Optional trigger variable that is a comma separated list of items to accept
public class PickupItemListener extends PassiveListener {

	private Set<Material> materials = new HashSet<>();
	private Map<MagicItemData, List<PassiveSpell>> types = new HashMap<>();
	private List<PassiveSpell> allTypes = new ArrayList<>();
	
	@Override
	public void registerSpell(PassiveSpell spell, PassiveTrigger trigger, String var) {
		if (var == null || var.isEmpty()) {
			allTypes.add(spell);
			return;
		}

		String[] split = var.split("\\|");
		for (String s : split) {
			s = s.trim();
			MagicItem magicItem = MagicItems.getMagicItemFromString(s);
			MagicItemData itemData = null;
			if (magicItem != null) itemData = magicItem.getMagicItemData();
			if (itemData == null) continue;

			List<PassiveSpell> list = types.computeIfAbsent(itemData, material -> new ArrayList<>());
			list.add(spell);
			materials.add(itemData.getType());
		}
	}
	
	@OverridePriority
	@EventHandler
	public void onPickup(EntityPickupItemEvent event) {
		if (!(event.getEntity() instanceof Player)) return;
		Player pl = (Player) event.getEntity();
		if (!allTypes.isEmpty()) {
			Spellbook spellbook = MagicSpells.getSpellbook(pl);
			for (PassiveSpell spell : allTypes) {
				if (!isCancelStateOk(spell, event.isCancelled())) continue;
				if (!spellbook.hasSpell(spell)) continue;
				boolean casted = spell.activate(pl);
				if (!PassiveListener.cancelDefaultAction(spell, casted)) continue;
				event.setCancelled(true);
			}
		}
		
		if (types.isEmpty()) return;
		List<PassiveSpell> list = getSpells(event.getItem().getItemStack());
		if (list == null) return;
		Spellbook spellbook = MagicSpells.getSpellbook(pl);

		for (PassiveSpell spell : list) {
			if (!isCancelStateOk(spell, event.isCancelled())) continue;
			if (!spellbook.hasSpell(spell)) continue;
			boolean casted = spell.activate(pl);
			if (!PassiveListener.cancelDefaultAction(spell, casted)) continue;
			event.setCancelled(true);
		}
	}
	
	private List<PassiveSpell> getSpells(ItemStack item) {
		if (!materials.contains(item.getType())) return null;
		MagicItemData itemData = MagicItems.getMagicItemDataFromItemStack(item);
		if (itemData == null) return null;

		for (Map.Entry<MagicItemData, List<PassiveSpell>> entry : types.entrySet()) {
			if (entry.getKey().equals(itemData)) return entry.getValue();
		}
		return null;
	}

}
