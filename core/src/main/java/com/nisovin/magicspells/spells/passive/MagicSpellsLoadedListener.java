package com.nisovin.magicspells.spells.passive;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.util.OverridePriority;
import com.nisovin.magicspells.events.MagicSpellsLoadedEvent;
import com.nisovin.magicspells.spells.passive.util.PassiveListener;

public class MagicSpellsLoadedListener extends PassiveListener {

	@Override
	public void initialize(String var) {

	}

	@OverridePriority
	@EventHandler
	public void onLoaded(MagicSpellsLoadedEvent e) {
		for (Player player : Bukkit.getOnlinePlayers()) {
			if (!MagicSpells.getSpellbook(player).hasSpell(passiveSpell)) continue;
			passiveSpell.activate(player);
		}
	}

}
