package com.nisovin.magicspells.spells.passive;

import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.FoodLevelChangeEvent;

import com.nisovin.magicspells.util.OverridePriority;
import com.nisovin.magicspells.spells.passive.util.PassiveListener;

public class FoodLevelChangeListener extends PassiveListener {

	@Override
	public void initialize(String var) {

	}

	@OverridePriority
	@EventHandler
	public void onFoodLevelChange(FoodLevelChangeEvent event) {
		if (!isCancelStateOk(event.isCancelled())) return;

		HumanEntity caster = event.getEntity();
		if (!canTrigger(caster) || !hasSpell(caster)) return;

		boolean casted = passiveSpell.activate(caster);
		if (cancelDefaultAction(casted)) event.setCancelled(true);
	}

}
