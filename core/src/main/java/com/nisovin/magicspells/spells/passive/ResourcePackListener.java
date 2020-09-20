package com.nisovin.magicspells.spells.passive;

import java.util.List;
import java.util.ArrayList;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent.Status;

import com.nisovin.magicspells.spells.PassiveSpell;
import com.nisovin.magicspells.util.OverridePriority;

// Trigger variable should be set to one of the following
// loaded,successfully_loaded
// declined
// failed,failed_download
// accepted
public class ResourcePackListener extends PassiveListener {

	List<PassiveSpell> spellsLoaded = new ArrayList<>();
	List<PassiveSpell> spellsDeclined = new ArrayList<>();
	List<PassiveSpell> spellsFailed = new ArrayList<>();
	List<PassiveSpell> spellsAccepted = new ArrayList<>();
	
	@Override
	public void registerSpell(PassiveSpell spell, PassiveTrigger trigger, String var) {
		if (var == null) return;
		switch (var.toLowerCase()) {
			case "successfully_loaded":
			case "loaded":
				spellsLoaded.add(spell);
				break;
			case "declined":
				spellsDeclined.add(spell);
				break;
			case "failed_download":
			case "failed":
				spellsFailed.add(spell);
				break;
			case "accepted":
				spellsAccepted.add(spell);
				break;
		}
	}

	@OverridePriority
	@EventHandler
	public void onPlayerResourcePack(PlayerResourcePackStatusEvent event) {
		Player player = event.getPlayer();
		Status status = event.getStatus();
		switch (status) {
			case SUCCESSFULLY_LOADED:
				activate(player, spellsLoaded);
				break;
			case DECLINED:
				activate(player, spellsDeclined);
				break;
			case FAILED_DOWNLOAD:
				activate(player, spellsFailed);
				break;
			case ACCEPTED:
				activate(player, spellsAccepted);
				break;
		}
	}

	private void activate(Player player, List<PassiveSpell> spells) {
		for (PassiveSpell spell : spells) {
			spell.activate(player);
		}
	}

	@Override
	public void turnOff() {
		spellsLoaded.clear();
		spellsDeclined.clear();
		spellsFailed.clear();
		spellsAccepted.clear();
	}

}
