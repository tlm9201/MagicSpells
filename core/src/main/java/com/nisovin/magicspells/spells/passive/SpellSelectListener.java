package com.nisovin.magicspells.spells.passive;

import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import com.nisovin.magicspells.Spellbook;
import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.spells.PassiveSpell;
import com.nisovin.magicspells.util.OverridePriority;
import com.nisovin.magicspells.events.SpellSelectionChangedEvent;

public class SpellSelectListener extends PassiveListener {

	private List<PassiveSpell> spellsAll = new ArrayList<>();

	private Map<String, List<PassiveSpell>> spellsSpecial = new HashMap<>();

	@Override
	public void registerSpell(PassiveSpell spell, PassiveTrigger trigger, String var) {
		if (var == null || var.isEmpty()) {
			spellsAll.add(spell);
			return;
		}

		for (String spellName : var.replace(" ", "").split(",")) {
			List<PassiveSpell> spells = spellsSpecial.getOrDefault(spellName, new ArrayList<>());
			spells.add(spell);
			spellsSpecial.put(spellName, spells);
		}
	}

	@OverridePriority
	@EventHandler
	public void onSpellSelect(SpellSelectionChangedEvent event) {
		if (!(event.getCaster() instanceof Player)) return;
		Player player = (Player) event.getCaster();
		Spellbook spellbook = MagicSpells.getSpellbook(player);
		if (!spellsAll.isEmpty()) {
			for (PassiveSpell spell : spellsAll) {
				if (!spellbook.hasSpell(spell)) continue;
				spell.activate(player);
			}
		}

		String spellName = event.getSpell().getInternalName();
		if (!spellsSpecial.containsKey(spellName)) return;
		for (PassiveSpell spell : spellsSpecial.get(spellName)) {
			if (!spellbook.hasSpell(spell)) continue;
			spell.activate(player);
		}
	}

}
