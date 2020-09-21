package com.nisovin.magicspells.spells.passive;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;

import com.nisovin.magicspells.Spellbook;
import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.util.OverridePriority;
import com.nisovin.magicspells.spells.passive.util.PassiveListener;

// No trigger variable used here
public class DeathListener extends PassiveListener {

	@Override
	public void initialize(String var) {

	}
	
	@OverridePriority
	@EventHandler
	public void onDeath(PlayerDeathEvent event) {
		Player player = event.getEntity();
		Spellbook spellbook = MagicSpells.getSpellbook(player);
		if (!spellbook.hasSpell(passiveSpell)) return;
		passiveSpell.activate(player);
	}

}
