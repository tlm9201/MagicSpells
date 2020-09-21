package com.nisovin.magicspells.spells.passive;

import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerBedLeaveEvent;

import com.nisovin.magicspells.Spellbook;
import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.util.OverridePriority;
import com.nisovin.magicspells.spells.passive.util.PassiveListener;

// No trigger variable is used here
public class LeaveBedListener extends PassiveListener {

	@Override
	public void initialize(String var) {

	}
	
	@OverridePriority
	@EventHandler
	public void onDeath(PlayerBedLeaveEvent event) {
		Spellbook spellbook = MagicSpells.getSpellbook(event.getPlayer());
		if (!spellbook.hasSpell(passiveSpell)) return;
		passiveSpell.activate(event.getPlayer());
	}

}
