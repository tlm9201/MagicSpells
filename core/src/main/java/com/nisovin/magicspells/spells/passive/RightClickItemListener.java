package com.nisovin.magicspells.spells.passive;

import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import org.bukkit.Material;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.event.player.PlayerInteractEvent;

import com.nisovin.magicspells.util.Util;
import com.nisovin.magicspells.Spellbook;
import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.spells.PassiveSpell;
import com.nisovin.magicspells.util.OverridePriority;
import com.nisovin.magicspells.util.magicitems.MagicItem;
import com.nisovin.magicspells.util.magicitems.MagicItems;
import com.nisovin.magicspells.util.magicitems.MagicItemData;

// Trigger variable of a comma separated list of items to accept
public class RightClickItemListener extends PassiveListener {

	private Set<Material> materials = new HashSet<>();
	private Map<MagicItemData, List<PassiveSpell>> types = new LinkedHashMap<>();

	private Set<Material> materialsOffhand = new HashSet<>();
	private Map<MagicItemData, List<PassiveSpell>> typesOffhand = new LinkedHashMap<>();
	
	@Override
	public void registerSpell(PassiveSpell spell, PassiveTrigger trigger, String var) {
		if (var == null) {
			MagicSpells.error(trigger.getName() + " cannot accept a null variable");
			return;
		}

		Set<Material> materialSetAddTo;
		Map<MagicItemData, List<PassiveSpell>> typesMapAddTo;
		if (isMainHand(trigger)) {
			materialSetAddTo = materials;
			typesMapAddTo = types;
		} else {
			materialSetAddTo = materialsOffhand;
			typesMapAddTo = typesOffhand;
		}
		
		String[] split = var.split("\\|");
		for (String s : split) {
			s = s.trim();
			MagicItem magicItem = MagicItems.getMagicItemFromString(s);
			MagicItemData itemData = null;
			if (magicItem != null) itemData = magicItem.getMagicItemData();
			if (itemData == null) continue;
			if (itemData.getName() != null) itemData.setName(Util.decolorize(itemData.getName()));

			List<PassiveSpell> list = typesMapAddTo.computeIfAbsent(itemData, m -> new ArrayList<>());
			list.add(spell);
			materialSetAddTo.add(itemData.getType());
		}
	}
	
	@OverridePriority
	@EventHandler
	public void onRightClick(PlayerInteractEvent event) {
		if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
		if (!event.hasItem()) return;
		
		ItemStack item = event.getItem();
		if (item == null || item.getType() == Material.AIR) return;
		List<PassiveSpell> list = getSpells(item, event.getHand() == EquipmentSlot.HAND);
		if (list == null) return;

		Spellbook spellbook = MagicSpells.getSpellbook(event.getPlayer());
		for (PassiveSpell spell : list) {
			if (!isCancelStateOk(spell, event.isCancelled())) continue;
			if (!spellbook.hasSpell(spell, false)) continue;
			boolean casted = spell.activate(event.getPlayer());
			if (PassiveListener.cancelDefaultAction(spell, casted)) event.setCancelled(true);
		}
	}
	
	private List<PassiveSpell> getSpells(ItemStack item, boolean mainHand) {
		Set<Material> materialSet;
		Map<MagicItemData, List<PassiveSpell>> spellMap;
		if (mainHand) {
			materialSet = materials;
			spellMap = types;
		} else {
			materialSet = materialsOffhand;
			spellMap = typesOffhand;
		}
		
		if (!materialSet.contains(item.getType())) return null;
		MagicItemData itemData = MagicItems.getMagicItemDataFromItemStack(item);
		if (itemData == null) return null;

		for (Map.Entry<MagicItemData, List<PassiveSpell>> entry : spellMap.entrySet()) {
			if (entry.getKey().equals(itemData)) return entry.getValue();
		}
		return null;
	}
	
	private boolean isMainHand(PassiveTrigger trigger) {
		return PassiveTrigger.RIGHT_CLICK.contains(trigger);
	}

}
