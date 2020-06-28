package com.nisovin.magicspells.spells.instant;

import java.util.List;
import java.util.ArrayList;

import org.bukkit.entity.Player;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.PlayerInventory;

import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.util.MagicConfig;
import com.nisovin.magicspells.spells.InstantSpell;
import com.nisovin.magicspells.util.magicitems.MagicItem;
import com.nisovin.magicspells.util.magicitems.MagicItems;
import com.nisovin.magicspells.util.magicitems.MagicItemData;

public class UnconjureSpell extends InstantSpell {

	private List<String> itemNames;
	private List<UnconjuredItem> items;

	public UnconjureSpell(MagicConfig config, String spellName) {
		super(config, spellName);

		itemNames = getConfigStringList("items", null);
	}

	@Override
	public void initialize() {
		super.initialize();
		items = new ArrayList<>();
		if (itemNames == null) return;
		for (String itemString : itemNames) {
			UnconjuredItem unconjuredItem = new UnconjuredItem(itemString);
			if (unconjuredItem.item == null) {
				MagicSpells.error("UnconjureSpell '" + internalName + "' has an invalid item specified: " + itemString);
				continue;
			}
			items.add(unconjuredItem);
		}
	}

	@Override
	public PostCastAction castSpell(LivingEntity livingEntity, SpellCastState state, float power, String[] args) {
		if (state == SpellCastState.NORMAL && livingEntity instanceof Player) {
			Player player = (Player) livingEntity;
			PlayerInventory inventory = player.getInventory();

			ItemStack[] contents = new ItemStack[1];

			// Search for the hovering item first.
			InventoryView invView = player.getOpenInventory();
			boolean stop = false;
			if (invView.getCursor() != null) {
				contents[0] = invView.getCursor();
				stop = filterItems(contents);
				invView.setCursor(contents[0]);
			}
			if (stop) return PostCastAction.ALREADY_HANDLED;

			contents = inventory.getContents();
			stop = filterItems(contents);
			inventory.setContents(contents);
			if (stop) return PostCastAction.ALREADY_HANDLED;

			contents = inventory.getArmorContents();
			filterItems(contents);
			inventory.setArmorContents(contents);
		}
		return PostCastAction.HANDLE_NORMALLY;
	}

	private boolean filterItems(ItemStack[] oldItems) {
		boolean stop = false;
		for (UnconjuredItem unconjuredItem : items) {
			if (unconjuredItem.item == null) continue;
			MagicItemData unconjuredItemData = MagicItems.getMagicItemDataFromItemStack(unconjuredItem.item);
			if (unconjuredItemData == null) continue;

			// Only look for an ItemStack with specified quantity.
			if (unconjuredItem.hasSpecialQuantity) {
				for (int i = 0; i < oldItems.length; i++) {
					if (oldItems[i] == null) continue;
					MagicItemData oldItemData = MagicItems.getMagicItemDataFromItemStack(oldItems[i]);
					if (oldItemData == null) continue;
					if (!unconjuredItemData.equals(oldItemData)) continue;
					if (unconjuredItem.item.getAmount() != oldItems[i].getAmount()) continue;
					oldItems[i] = null;
					// True is only returned if the search is for an item with specific
					// quantity. If the item is found, this won't search for the item in
					// other inventory places.
					stop = true;
					break;
				}
			}
			// Look for all items that match the ItemStack - ignore quantity.
			else {
				for (int i = 0; i < oldItems.length; i++) {
					if (oldItems[i] == null) continue;
					MagicItemData oldItemData = MagicItems.getMagicItemDataFromItemStack(oldItems[i]);
					if (oldItemData == null) continue;
					if (unconjuredItemData.equals(oldItemData)) oldItems[i] = null;
				}
			}
		}
		return stop;
	}

	private static class UnconjuredItem {

		ItemStack item;
		boolean hasSpecialQuantity = false;

		public UnconjuredItem(String itemString) {
			String[] splits = itemString.split(" ");
			MagicItem magicItem = MagicItems.getMagicItemFromString(splits[0]);
			if (magicItem == null) return;

			item = magicItem.getItemStack();
			// If it's null, then stop. The item won't make
			// it to the list anyway.
			if (item == null) return;

			if (splits.length == 1) return;
			item.setAmount(Integer.parseInt(splits[1]));
			hasSpecialQuantity = true;
		}

	}

}
