package com.nisovin.magicspells.spells.passive;

import java.util.EnumSet;

import org.jetbrains.annotations.NotNull;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent.Status;

import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.util.OverridePriority;
import com.nisovin.magicspells.spells.passive.util.PassiveListener;

// Trigger variable should be set to one of the following
// loaded,successfully_loaded
// declined
// failed,failed_download
// accepted
public class ResourcePackListener extends PassiveListener {

	private final EnumSet<Status> packStatus = EnumSet.noneOf(Status.class);

	@Override
	public void initialize(@NotNull String var) {
		if (var.isEmpty()) return;
		for (String s : var.split(",")) {
			s = s.trim().toUpperCase();

			switch (s) {
				case "LOADED":
					packStatus.add(Status.SUCCESSFULLY_LOADED);
					break;
				case "FAILED":
					packStatus.add(Status.FAILED_DOWNLOAD);
					break;
				default:
					try {
						Status status = Status.valueOf(s);
						packStatus.add(status);
					} catch (IllegalArgumentException e) {
						MagicSpells.error("Invalid resource pack status '" + s + "' in resourcepack trigger on passive spell '" + passiveSpell.getInternalName() + "'");
					}
			}
		}
	}

	@OverridePriority
	@EventHandler
	public void onPlayerResourcePack(PlayerResourcePackStatusEvent event) {
		if (!packStatus.isEmpty() && !packStatus.contains(event.getStatus())) return;
		Player caster = event.getPlayer();
		if (!canTrigger(caster)) return;
		passiveSpell.activate(caster);
	}

}
