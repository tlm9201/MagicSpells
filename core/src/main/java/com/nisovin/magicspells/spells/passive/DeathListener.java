package com.nisovin.magicspells.spells.passive;

import org.jetbrains.annotations.NotNull;

import org.bukkit.event.EventHandler;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDeathEvent;

import com.nisovin.magicspells.util.Name;
import com.nisovin.magicspells.util.OverridePriority;
import com.nisovin.magicspells.spells.passive.util.PassiveListener;

@Name("death")
public class DeathListener extends PassiveListener {

	@Override
	public void initialize(@NotNull String var) {
	}

	@OverridePriority
	@EventHandler
	public void onDeath(EntityDeathEvent event) {
		LivingEntity entity = event.getEntity();
		if (!canTrigger(entity)) return;

		boolean casted = passiveSpell.activate(entity);
		if (cancelDefaultAction(casted)) event.setCancelled(true);
	}

}
