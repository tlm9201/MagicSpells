package com.nisovin.magicspells.spells.passive;

import java.util.Set;
import java.util.HashSet;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.event.entity.EntityPickupItemEvent;

import com.nisovin.magicspells.Spellbook;
import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.util.OverridePriority;
import com.nisovin.magicspells.util.magicitems.MagicItem;
import com.nisovin.magicspells.util.magicitems.MagicItems;
import com.nisovin.magicspells.util.magicitems.MagicItemData;
import com.nisovin.magicspells.spells.passive.util.PassiveListener;

// Optional trigger variable that is a comma separated list of items to accept
public class PickupItemListener extends PassiveListener {

	private final Set<MagicItemData> items = new HashSet<>();
	
	@Override
	public void initialize(String var) {
		if (var == null || var.isEmpty()) return;

		String[] split = var.split("\\|");
		for (String s : split) {
			s = s.trim();
			MagicItem magicItem = MagicItems.getMagicItemFromString(s);
			MagicItemData itemData = null;
			if (magicItem != null) itemData = magicItem.getMagicItemData();
			if (itemData == null) continue;

			items.add(itemData);
		}
	}
	
	@OverridePriority
	@EventHandler
	public void onPickup(EntityPickupItemEvent event) {
		if (!(event.getEntity() instanceof Player)) return;
		Player pl = (Player) event.getEntity();
		Spellbook spellbook = MagicSpells.getSpellbook(pl);

		if (items.isEmpty()) {
			if (!isCancelStateOk(event.isCancelled())) return;
			if (!spellbook.hasSpell(passiveSpell)) return;
			boolean casted = passiveSpell.activate(pl);
			if (!cancelDefaultAction(casted)) return;
			event.setCancelled(true);
			return;
		}

		ItemStack item = event.getItem().getItemStack();
		if (item == null) return;
		MagicItemData itemData = MagicItems.getMagicItemDataFromItemStack(item);
		if (!items.contains(itemData)) return;

		if (!isCancelStateOk(event.isCancelled())) return;
		if (!spellbook.hasSpell(passiveSpell)) return;
		boolean casted = passiveSpell.activate(pl);
		if (!cancelDefaultAction(casted)) return;
		event.setCancelled(true);
	}

}
