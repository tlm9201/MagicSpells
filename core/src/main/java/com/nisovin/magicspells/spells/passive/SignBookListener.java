package com.nisovin.magicspells.spells.passive;

import java.util.Set;
import java.util.List;
import java.util.Arrays;
import java.util.HashSet;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.event.player.PlayerEditBookEvent;

import com.nisovin.magicspells.util.OverridePriority;
import com.nisovin.magicspells.spells.passive.util.PassiveListener;

// Trigger variable is optional
// If not specified, it will trigger on any book
// If specified, it should be a comma separated list of page text to trigger on
public class SignBookListener extends PassiveListener {

	private final Set<String> text = new HashSet<>();

	@Override
	public void initialize(String var) {
		if (var == null || var.isEmpty()) return;

		String[] split = var.split(",");
		text.addAll(Arrays.asList(split));
	}

	@OverridePriority
	@EventHandler
	public void onBookEdit(PlayerEditBookEvent event) {
		if (!isCancelStateOk(event.isCancelled())) return;
		if (!event.isSigning()) return;

		Player player = event.getPlayer();
		if (!hasSpell(player) || !canTrigger(player)) return;

		if (text.isEmpty()) {
			boolean casted = passiveSpell.activate(player);
			if (cancelDefaultAction(casted)) event.setCancelled(true);
			return;
		}

		BookMeta meta = event.getNewBookMeta();
		List<String> pages = meta.getPages();
		for (String page : pages) {
			if (text.contains(page)) {
				boolean casted = passiveSpell.activate(player);
				if (cancelDefaultAction(casted)) event.setCancelled(true);
				return;
			}
		}
	}

}
