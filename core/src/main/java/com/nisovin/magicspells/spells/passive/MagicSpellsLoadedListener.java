package com.nisovin.magicspells.spells.passive;

import java.util.List;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.spells.PassiveSpell;
import com.nisovin.magicspells.util.OverridePriority;
import com.nisovin.magicspells.events.MagicSpellsLoadedEvent;

public class MagicSpellsLoadedListener extends PassiveListener {

	private List<PassiveSpell> spells = new ArrayList<>();

	@Override
	public void registerSpell(PassiveSpell spell, PassiveTrigger trigger, String var) {
		spells.add(spell);
	}

	@OverridePriority
	@EventHandler
	public void onLoaded(MagicSpellsLoadedEvent e) {
		if (spells.isEmpty()) return;
		for (PassiveSpell spell : spells) {
			for (Player player : Bukkit.getOnlinePlayers()) {
				if (!MagicSpells.getSpellbook(player).hasSpell(spell)) continue;
				spell.activate(player);
			}
		}
	}

}
