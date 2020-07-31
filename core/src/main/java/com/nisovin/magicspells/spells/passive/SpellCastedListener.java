package com.nisovin.magicspells.spells.passive;

import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.entity.LivingEntity;

import com.nisovin.magicspells.Spell;
import com.nisovin.magicspells.Spellbook;
import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.spells.PassiveSpell;
import com.nisovin.magicspells.Spell.PostCastAction;
import com.nisovin.magicspells.Spell.SpellCastState;
import com.nisovin.magicspells.util.OverridePriority;
import com.nisovin.magicspells.events.SpellCastedEvent;

// Optional trigger variable of comma separated list of internal spell names to accept
public class SpellCastedListener extends PassiveListener {

	Map<Spell, List<PassiveSpell>> spells = new HashMap<>();
	List<PassiveSpell> anySpell = new ArrayList<>();
			
	@Override
	public void registerSpell(PassiveSpell spell, PassiveTrigger trigger, String var) {
		if (var == null || var.isEmpty()) {
			anySpell.add(spell);
			return;
		}

		String[] split = var.split(",");
		for (String s : split) {
			Spell sp = MagicSpells.getSpellByInternalName(s.trim());
			if (sp == null) continue;
			List<PassiveSpell> passives = spells.computeIfAbsent(sp, p -> new ArrayList<>());
			passives.add(spell);
		}
	}
	
	@OverridePriority
	@EventHandler
	public void onSpellCast(SpellCastedEvent event) {
		LivingEntity caster = event.getCaster();
		if (!(caster instanceof Player)) return;
		if (event.getSpellCastState() != SpellCastState.NORMAL) return;
		if (event.getPostCastAction() == PostCastAction.ALREADY_HANDLED) return;

		Spellbook spellbook = MagicSpells.getSpellbook((Player) caster);
		for (PassiveSpell spell : anySpell) {
			if (spell.equals(event.getSpell())) continue;
			if (!spellbook.hasSpell(spell, false)) continue;
			spell.activate((Player) caster);
		}

		List<PassiveSpell> list = spells.get(event.getSpell());
		if (list == null) return;
		for (PassiveSpell spell : list) {
			if (spell.equals(event.getSpell())) continue;
			if (!spellbook.hasSpell(spell, false)) continue;
			spell.activate((Player) caster);
		}
	}

}
