package com.nisovin.magicspells.spells.passive;

import java.util.List;
import java.util.ArrayList;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityToggleSwimEvent;

import com.nisovin.magicspells.Spellbook;
import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.spells.PassiveSpell;
import com.nisovin.magicspells.util.OverridePriority;

// No trigger variable is currently used
public class SwimListener extends PassiveListener {

	List<PassiveSpell> swim = null;
	List<PassiveSpell> stopSwim = null;
	
	@Override
	public void registerSpell(PassiveSpell spell, PassiveTrigger trigger, String var) {
		if (PassiveTrigger.START_SWIM.contains(trigger)) {
			if (swim == null) swim = new ArrayList<>();
			swim.add(spell);
		} else if (PassiveTrigger.STOP_SWIM.contains(trigger)) {
			if (stopSwim == null) stopSwim = new ArrayList<>();
			stopSwim.add(spell);
		}
	}
	
	@OverridePriority
	@EventHandler
	public void onSwim(EntityToggleSwimEvent event) {
		Entity entity = event.getEntity();
		if (!(entity instanceof Player)) return;
		Player player = (Player) entity;
		if (event.isSwimming()) {
			if (swim == null) return;
			Spellbook spellbook = MagicSpells.getSpellbook(player);
			for (PassiveSpell spell : swim) {
				if (!isCancelStateOk(spell, event.isCancelled())) continue;
				if (!spellbook.hasSpell(spell, false)) continue;
				boolean casted = spell.activate(player);
				if (!PassiveListener.cancelDefaultAction(spell, casted)) continue;
				event.setCancelled(true);
			}
			return;
		}

		if (stopSwim == null) return;
		Spellbook spellbook = MagicSpells.getSpellbook(player);
		for (PassiveSpell spell : stopSwim) {
			if (!isCancelStateOk(spell, event.isCancelled())) continue;
			if (!spellbook.hasSpell(spell, false)) continue;
			boolean casted = spell.activate(player);
			if (!PassiveListener.cancelDefaultAction(spell, casted)) continue;
			event.setCancelled(true);
		}
	}

}
