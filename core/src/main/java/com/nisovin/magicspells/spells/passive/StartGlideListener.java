package com.nisovin.magicspells.spells.passive;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityToggleGlideEvent;

import com.nisovin.magicspells.Spellbook;
import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.util.OverridePriority;
import com.nisovin.magicspells.spells.passive.util.PassiveListener;

// No trigger variable is currently used
public class StartGlideListener extends PassiveListener {
	
	@Override
	public void initialize(String var) {

	}
	
	@OverridePriority
	@EventHandler
	public void onGlide(EntityToggleGlideEvent event) {
		Entity entity = event.getEntity();
		if (!(entity instanceof Player)) return;
		Player player = (Player) entity;
		if (!event.isGliding()) return;

		Spellbook spellbook = MagicSpells.getSpellbook(player);
		if (!isCancelStateOk(event.isCancelled())) return;
		if (!spellbook.hasSpell(passiveSpell, false)) return;
		boolean casted = passiveSpell.activate(player);
		if (!cancelDefaultAction(casted)) return;
		event.setCancelled(true);
	}

}
