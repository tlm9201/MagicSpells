package com.nisovin.magicspells;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.entity.LivingEntity;

import com.nisovin.magicspells.events.SpellTargetEvent;
import com.nisovin.magicspells.zones.NoMagicZoneManager;

class MagicSpellListener implements Listener {

	private NoMagicZoneManager noMagicZoneManager;
		
	MagicSpellListener(MagicSpells plugin) {
		noMagicZoneManager = MagicSpells.getNoMagicZoneManager();
	}

	@EventHandler
	public void onSpellTarget(SpellTargetEvent event) {
		// Check if target has noTarget permission / spectator gamemode / is in noMagicZone
		LivingEntity target = event.getTarget();
		Spell spell = event.getSpell();
		if (target == null) return;

		if (Perm.NOTARGET.has(target)) event.setCancelled(true);
		if (target instanceof Player && ((Player) target).getGameMode() == GameMode.SPECTATOR) event.setCancelled(true);
		if (spell != null && noMagicZoneManager != null && noMagicZoneManager.willFizzle(target, spell)) event.setCancelled(true);
	}
	
}
