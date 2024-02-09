package com.nisovin.magicspells.spells.passive;

import org.jetbrains.annotations.NotNull;

import org.bukkit.event.EventHandler;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityShootBowEvent;

import com.nisovin.magicspells.util.Name;
import com.nisovin.magicspells.util.OverridePriority;
import com.nisovin.magicspells.spells.passive.util.PassiveListener;

@Name("shoot")
public class ShootListener extends PassiveListener {

	@Override
	public void initialize(@NotNull String var) {
	}

	@OverridePriority
	@EventHandler
	public void onShoot(final EntityShootBowEvent event) {
		if (!isCancelStateOk(event.isCancelled())) return;

		LivingEntity caster = event.getEntity();
		if (!canTrigger(caster)) return;

		boolean casted = passiveSpell.activate(caster, event.getForce());
		if (cancelDefaultAction(casted)) {
			event.setCancelled(true);
			event.getProjectile().remove();
		}
	}

}
