package com.nisovin.magicspells.spells.passive;

import java.util.List;
import java.util.ArrayList;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerGameModeChangeEvent;

import com.nisovin.magicspells.Spellbook;
import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.spells.PassiveSpell;
import com.nisovin.magicspells.util.OverridePriority;

public class GameModeChangeListener extends PassiveListener {

	List<PassiveSpell> spellsCreative = new ArrayList<>();
	List<PassiveSpell> spellsSurvival = new ArrayList<>();
	List<PassiveSpell> spellsAdventure = new ArrayList<>();
	List<PassiveSpell> spellsSpectator = new ArrayList<>();

	@Override
	public void registerSpell(PassiveSpell spell, PassiveTrigger trigger, String var) {
		if (var == null || var.isEmpty()) {
			spellsCreative.add(spell);
			spellsSurvival.add(spell);
			spellsAdventure.add(spell);
			spellsSpectator.add(spell);
			return;
		}

		switch (var.toLowerCase()) {
			case "creative":
				spellsCreative.add(spell);
				break;
			case "survival":
				spellsSurvival.add(spell);
				break;
			case "adventure":
				spellsAdventure.add(spell);
				break;
			case "spectator":
				spellsSpectator.add(spell);
				break;
		}
	}

	@OverridePriority
	@EventHandler
	public void onGameModeChange(PlayerGameModeChangeEvent event) {
		Player player = event.getPlayer();
		Spellbook spellbook = MagicSpells.getSpellbook(player);
		List<PassiveSpell> spells = new ArrayList<>();
		switch (event.getNewGameMode().name().toLowerCase()) {
			case "creative":
				spells = spellsCreative;
				break;
			case "survival":
				spells = spellsSurvival;
				break;
			case "adventure":
				spells = spellsAdventure;
				break;
			case "spectator":
				spells = spellsSpectator;
				break;
		}

		for (PassiveSpell spell : spells) {
			if (!spellbook.hasSpell(spell)) continue;
			boolean casted = spell.activate(player);
			if (PassiveListener.cancelDefaultAction(spell, casted)) event.setCancelled(true);
		}
	}

}
