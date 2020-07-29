package com.nisovin.magicspells.spells.passive;

import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.player.PlayerFishEvent;

import com.nisovin.magicspells.util.Util;
import com.nisovin.magicspells.Spellbook;
import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.spells.PassiveSpell;
import com.nisovin.magicspells.util.OverridePriority;

// Trigger variable is optional
// If not specified, it triggers in all forms
// The trigger variable may be a comma separated list containing any of the following
// ground, fish, fail, <entity type>
public class FishListener extends PassiveListener {

	Map<EntityType, List<PassiveSpell>> types = new HashMap<>();
	List<PassiveSpell> ground = new ArrayList<>();
	List<PassiveSpell> fish = new ArrayList<>();
	List<PassiveSpell> fail = new ArrayList<>();
	List<PassiveSpell> allTypes = new ArrayList<>();
	
	@Override
	public void registerSpell(PassiveSpell spell, PassiveTrigger trigger, String var) {
		if (var == null || var.isEmpty()) {
			allTypes.add(spell);
			return;
		}

		String[] split = var.replace(" ", "").toUpperCase().split(",");
		for (String s : split) {
			switch (s.toLowerCase()) {
				case "ground":
					ground.add(spell);
					break;
				case "fish":
					fish.add(spell);
					break;
				case "fail":
					fail.add(spell);
					break;
				default:
					EntityType t = Util.getEntityType(s);
					if (t == null) return;
					List<PassiveSpell> list = types.computeIfAbsent(t, type -> new ArrayList<>());
					list.add(spell);
					break;
			}
		}
	}
	
	@OverridePriority
	@EventHandler
	public void onFish(PlayerFishEvent event) {
		PlayerFishEvent.State state = event.getState();
		Player player = event.getPlayer();
		Spellbook spellbook = MagicSpells.getSpellbook(player);

		if (!allTypes.isEmpty()) {
			Entity entity = event.getCaught();
			for (PassiveSpell spell : allTypes) {
				if (!isCancelStateOk(spell, event.isCancelled())) continue;
				if (!spellbook.hasSpell(spell)) continue;
				boolean casted = spell.activate(player, entity instanceof LivingEntity ? (LivingEntity)entity : null);
				if (!PassiveListener.cancelDefaultAction(spell, casted)) continue;
				event.setCancelled(true);
			}
		}

		switch (state) {
			case IN_GROUND:
				if (ground.isEmpty()) return;
				for (PassiveSpell spell : ground) {
					if (!isCancelStateOk(spell, event.isCancelled())) continue;
					if (!spellbook.hasSpell(spell)) continue;
					boolean casted = spell.activate(player, event.getHook().getLocation());
					if (!PassiveListener.cancelDefaultAction(spell, casted)) continue;
					event.setCancelled(true);
				}
				break;
			case CAUGHT_FISH:
				if (fish.isEmpty()) return;
				for (PassiveSpell spell : fish) {
					if (!isCancelStateOk(spell, event.isCancelled())) continue;
					if (!spellbook.hasSpell(spell)) continue;
					boolean casted = spell.activate(player, event.getHook().getLocation());
					if (!PassiveListener.cancelDefaultAction(spell, casted)) continue;
					event.setCancelled(true);
				}
				break;
			case FAILED_ATTEMPT:
				if (fail.isEmpty()) return;
				for (PassiveSpell spell : fail) {
					if (!isCancelStateOk(spell, event.isCancelled())) continue;
					if (!spellbook.hasSpell(spell)) continue;
					boolean casted = spell.activate(player, event.getHook().getLocation());
					if (!PassiveListener.cancelDefaultAction(spell, casted)) continue;
					event.setCancelled(true);
				}
				break;
			case CAUGHT_ENTITY:
				if (types.isEmpty()) return;
				Entity entity = event.getCaught();

				if (entity == null) return;
				if (!types.containsKey(entity.getType())) return;
				for (PassiveSpell spell : fail) {
					if (!isCancelStateOk(spell, event.isCancelled())) continue;
					if (!spellbook.hasSpell(spell)) continue;
					boolean casted = spell.activate(player, entity instanceof LivingEntity ? (LivingEntity)entity : null);
					if (!PassiveListener.cancelDefaultAction(spell, casted)) continue;
					event.setCancelled(true);
				}
				break;
		}
	}
	
}
