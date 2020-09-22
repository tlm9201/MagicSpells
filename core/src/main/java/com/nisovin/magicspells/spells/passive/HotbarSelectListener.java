package com.nisovin.magicspells.spells.passive;

import java.util.Set;
import java.util.HashSet;

import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.event.player.PlayerItemHeldEvent;

import com.nisovin.magicspells.Spellbook;
import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.util.OverridePriority;
import com.nisovin.magicspells.util.magicitems.MagicItem;
import com.nisovin.magicspells.util.magicitems.MagicItems;
import com.nisovin.magicspells.util.magicitems.MagicItemData;
import com.nisovin.magicspells.spells.passive.util.PassiveListener;

// Trigger variable is the item to trigger on
public class HotbarSelectListener extends PassiveListener {

	private final Set<MagicItemData> items = new HashSet<>();
	
	@Override
	public void initialize(String var) {
		if (var == null || var.isEmpty()) return;
		MagicItemData itemData = null;
		MagicItem magicItem = MagicItems.getMagicItemFromString(var.trim());
		if (magicItem != null) itemData = magicItem.getMagicItemData();
		if (itemData == null) return;

		items.add(itemData);
	}
	
	@OverridePriority
	@EventHandler
	public void onPlayerScroll(PlayerItemHeldEvent event) {
		ItemStack item = event.getPlayer().getInventory().getItem(event.getNewSlot());
		if (item == null || item.getType().isAir()) return;
		MagicItemData itemData = MagicItems.getMagicItemDataFromItemStack(item);
		if (itemData == null) return;
		if (!items.isEmpty() && !items.contains(itemData)) return;

		Spellbook spellbook = MagicSpells.getSpellbook(event.getPlayer());
		if (!isCancelStateOk(event.isCancelled())) return;
		if (!spellbook.hasSpell(passiveSpell, false)) return;
		boolean casted = passiveSpell.activate(event.getPlayer());
		if (!cancelDefaultAction(casted)) return;
		event.setCancelled(true);
	}

}
