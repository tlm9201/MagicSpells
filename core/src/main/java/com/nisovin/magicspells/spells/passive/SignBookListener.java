package com.nisovin.magicspells.spells.passive;

import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.event.player.PlayerEditBookEvent;

import com.nisovin.magicspells.Spellbook;
import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.spells.PassiveSpell;
import com.nisovin.magicspells.util.OverridePriority;

// Trigger variable is optional
// If not specified, it will trigger on any book
// If specified, it should be a comma separated list of page text to trigger on
public class SignBookListener extends PassiveListener {

	private List<PassiveSpell> spells = new ArrayList<>();
	private Map<String, List<PassiveSpell>> types = new HashMap<>();

	@Override
	public void registerSpell(PassiveSpell spell, PassiveTrigger trigger, String var) {
		if (var == null || var.isEmpty()) {
			spells.add(spell);
			return;
		}

		String[] split = var.split(",");
		for (String s : split) {
			List<PassiveSpell> passives = types.computeIfAbsent(s, p -> new ArrayList<>());
			passives.add(spell);
		}
	}

	@OverridePriority
	@EventHandler
	public void onBookEdit(PlayerEditBookEvent event) {
		Player player = event.getPlayer();
		BookMeta meta = event.getNewBookMeta();
		if (!meta.hasAuthor()) return;

		Spellbook spellbook = MagicSpells.getSpellbook(player);
		if (!spells.isEmpty()) {
			for (PassiveSpell spell : spells) {
				if (!spellbook.hasSpell(spell)) continue;
				spell.activate(player);
			}
		}

		for (int i = 1; i <= meta.getPageCount(); i++) {
			if (!types.containsKey(meta.getPage(i))) continue;
			List<PassiveSpell> list = types.get(meta.getPage(i));
			for (PassiveSpell spell : list) {
				if (!spellbook.hasSpell(spell)) continue;
				spell.activate(player);
				return;
			}

		}
	}

}
