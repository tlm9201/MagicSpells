package com.nisovin.magicspells.spells.passive;

import java.util.ArrayList;
import java.util.Collection;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import com.nisovin.magicspells.Spell;
import com.nisovin.magicspells.Spellbook;
import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.spells.PassiveSpell;
import com.nisovin.magicspells.util.OverridePriority;
import com.nisovin.magicspells.events.SpellLearnEvent;
import com.nisovin.magicspells.events.SpellForgetEvent;
import com.nisovin.magicspells.spells.passive.util.PassiveListener;

// Trigger argument is required
// Must be an integer.
// The value reflects how often the trigger runs
// Where the value of the trigger variable is x
// The trigger will activate every x ticks
public class TicksListener extends PassiveListener {

	private Ticker ticker;

	@Override
	public void initialize(String var) {
		if (var == null || var.isEmpty()) return;
		try {
			int interval = Integer.parseInt(var);
			ticker = new Ticker(passiveSpell, interval);
		} catch (NumberFormatException e) {
			// ignored
		}

		for (Player player : Bukkit.getOnlinePlayers()) {
			if (!player.isValid()) continue;
			ticker.add(player);
		}
	}
	
	@Override
	public void turnOff() {
		ticker.turnOff();
	}
	
	@OverridePriority
	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		Spellbook spellbook = MagicSpells.getSpellbook(player);
		if (spellbook == null) return;
		if (!spellbook.hasSpell(passiveSpell)) return;
		ticker.add(player);
	}
	
	@OverridePriority
	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		ticker.remove(player);
	}
	
	@OverridePriority
	@EventHandler
	public void onDeath(PlayerDeathEvent event) {
		Player player = event.getEntity();
		ticker.remove(player);
	}
	
	@OverridePriority
	@EventHandler
	public void onRespawn(PlayerRespawnEvent event) {
		Player player = event.getPlayer();
		Spellbook spellbook = MagicSpells.getSpellbook(player);
		if (spellbook == null) return;
		if (!spellbook.hasSpell(passiveSpell)) return;
		ticker.add(player);
	}
	
	@OverridePriority
	@EventHandler
	public void onLearn(SpellLearnEvent event) {
		Spell spell = event.getSpell();
		if (!(spell instanceof PassiveSpell)) return;
		if (!spell.getInternalName().equals(passiveSpell.getInternalName())) return;
		ticker.add(event.getLearner());
	}
	
	@OverridePriority
	@EventHandler
	public void onForget(SpellForgetEvent event) {
		Spell spell = event.getSpell();
		if (!(spell instanceof PassiveSpell)) return;
		if (!spell.getInternalName().equals(passiveSpell.getInternalName())) return;
		ticker.remove(event.getForgetter());
	}
	
	private static class Ticker implements Runnable {

		private final Collection<Player> players;

		private final PassiveSpell passiveSpell;

		private final int taskId;
		private final String profilingKey;
		
		public Ticker(PassiveSpell passiveSpell, int interval) {
			this.passiveSpell = passiveSpell;
			taskId = MagicSpells.scheduleRepeatingTask(this, interval, interval);
			profilingKey = MagicSpells.profilingEnabled() ? "PassiveTick:" + interval : null;
			players = new ArrayList<>();
		}
		
		public void add(Player player) {
			players.add(player);
		}

		public void remove(Player player) {
			players.remove(player);
		}

		@Override
		public void run() {
			long start = System.nanoTime();

			for (Player p : new ArrayList<>(players)) {
				if (p.isOnline() && p.isValid()) passiveSpell.activate(p);
				else players.remove(p);
			}

			if (profilingKey != null) MagicSpells.addProfile(profilingKey, System.nanoTime() - start);
		}
		
		public void turnOff() {
			MagicSpells.cancelTask(taskId);
		}
		
	}
	
}
