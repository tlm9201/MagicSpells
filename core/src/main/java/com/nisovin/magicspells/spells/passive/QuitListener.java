package com.nisovin.magicspells.spells.passive;

import org.jetbrains.annotations.NotNull;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;

import com.nisovin.magicspells.util.Name;
import com.nisovin.magicspells.util.OverridePriority;
import com.nisovin.magicspells.spells.passive.util.PassiveListener;

@Name("quit")
public class QuitListener extends PassiveListener {

	@Override
	public void initialize(@NotNull String var) {
	}

	@OverridePriority
	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		Player caster = event.getPlayer();
		if (!canTrigger(caster)) return;

		passiveSpell.activate(caster);
	}

}
