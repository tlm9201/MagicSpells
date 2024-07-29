package com.nisovin.magicspells.spells.passive;

import java.util.Set;
import java.util.HashSet;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;

import org.jetbrains.annotations.NotNull;

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;

import com.nisovin.magicspells.util.Name;
import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.util.OverridePriority;
import com.nisovin.magicspells.util.magicitems.MagicItems;
import com.nisovin.magicspells.util.magicitems.MagicItemData;
import com.nisovin.magicspells.spells.passive.util.PassiveListener;
import com.nisovin.magicspells.util.magicitems.MagicItemDataParser;

@Name("unequip")
public class UnequipListener extends PassiveListener {

	private final Set<MagicItemData> items = new HashSet<>();

	@Override
	public void initialize(@NotNull String var) {
		if (var.isEmpty()) return;
		for (String s : var.split(MagicItemDataParser.DATA_REGEX)) {
			s = s.trim();

			MagicItemData itemData = MagicItems.getMagicItemDataFromString(s);
			if (itemData == null) {
				MagicSpells.error("Invalid magic item '" + s + "' in unequip trigger on passive spell '" + passiveSpell.getInternalName() + "'");
				continue;
			}

			items.add(itemData);
		}
	}

	@OverridePriority
	@EventHandler
	public void onUnequip(PlayerArmorChangeEvent event) {
		Player caster = event.getPlayer();
		if (!canTrigger(caster)) return;

		if (!items.isEmpty()) {
			ItemStack oldItem = event.getOldItem();
			if (oldItem.isEmpty()) return;

			MagicItemData oldData = MagicItems.getMagicItemDataFromItemStack(oldItem);
			if (oldData == null) return;

			ItemStack newItem = event.getNewItem();
			MagicItemData newData = MagicItems.getMagicItemDataFromItemStack(newItem);

			if (!contains(oldData, newData)) return;
		}

		passiveSpell.activate(caster);
	}

	private boolean contains(MagicItemData oldData, MagicItemData newData) {
		for (MagicItemData data : items)
			if (data.matches(oldData) && (newData == null || !data.matches(newData)))
				return true;

		return false;
	}

}
