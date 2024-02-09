package com.nisovin.magicspells.spells.passive;

import java.util.Set;
import java.util.HashSet;

import org.jetbrains.annotations.NotNull;

import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryCloseEvent;

import com.nisovin.magicspells.util.Name;
import com.nisovin.magicspells.util.Util;
import com.nisovin.magicspells.util.OverridePriority;
import com.nisovin.magicspells.spells.passive.util.PassiveListener;

// Optional trigger variable that may contain a comma separated list of inventory names to trigger on
@Name("inventoryclose")
public class InventoryCloseListener extends PassiveListener {

	private final Set<String> inventoryNames = new HashSet<>();

	@Override
	public void initialize(@NotNull String var) {
		if (var.isEmpty()) return;
		for (String s : var.split(",")) {
			inventoryNames.add(s.trim());
		}
	}

	@OverridePriority
	@EventHandler
	public void onInventoryClose(InventoryCloseEvent event) {
		if (!inventoryNames.contains(Util.getStringFromComponent(event.getView().title()))) return;

		HumanEntity caster = event.getPlayer();
		if (!canTrigger(caster)) return;

		passiveSpell.activate(caster);
	}

}
