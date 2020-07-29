package com.nisovin.magicspells.spells.passive;

import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;

import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;

import com.nisovin.magicspells.Spellbook;
import com.nisovin.magicspells.util.Util;
import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.spells.PassiveSpell;
import com.nisovin.magicspells.util.OverridePriority;

// Trigger variable option is optional
// If not defined, it will trigger regardless of entity type
// If specified, it should be a comma separated list of entity types to accept
public class RightClickEntityListener extends PassiveListener {

	Map<EntityType, List<PassiveSpell>> types = new HashMap<>();
	List<PassiveSpell> allTypes = new ArrayList<>();
	
	Map<EntityType, List<PassiveSpell>> typesOffhand = new HashMap<>();
	List<PassiveSpell> allTypesOffhand = new ArrayList<>();
	
	@Override
	public void registerSpell(PassiveSpell spell, PassiveTrigger trigger, String var) {
		
		Map<EntityType, List<PassiveSpell>> typeMapLocal;
		List<PassiveSpell> allTypesLocal;
		
		if (isMainHand(trigger)) {
			typeMapLocal = types;
			allTypesLocal = allTypes;
		} else {
			typeMapLocal = typesOffhand;
			allTypesLocal = allTypesOffhand;
		}
		
		if (var == null || var.isEmpty()) {
			allTypesLocal.add(spell);
			return;
		}

		String[] split = var.replace(" ", "").toUpperCase().split(",");
		for (String s : split) {
			EntityType t = Util.getEntityType(s);
			if (t == null) continue;
			List<PassiveSpell> list = typeMapLocal.computeIfAbsent(t, type -> new ArrayList<>());
			list.add(spell);
		}
	}
	
	@OverridePriority
	@EventHandler
	public void onRightClickEntity(PlayerInteractAtEntityEvent event) {
		if (!(event.getRightClicked() instanceof LivingEntity)) return;

		Map<EntityType, List<PassiveSpell>> typeMapLocal;
		List<PassiveSpell> allTypesLocal;
		
		if (event.getHand() == EquipmentSlot.HAND) {
			typeMapLocal = types;
			allTypesLocal = allTypes;
		} else {
			typeMapLocal = typesOffhand;
			allTypesLocal = allTypesOffhand;
		}
		
		if (!allTypesLocal.isEmpty()) {
			Spellbook spellbook = MagicSpells.getSpellbook(event.getPlayer());
			for (PassiveSpell spell : allTypesLocal) {
				if (!isCancelStateOk(spell, event.isCancelled())) continue;
				if (!spellbook.hasSpell(spell)) continue;
				boolean casted = spell.activate(event.getPlayer(), (LivingEntity)event.getRightClicked());
				if (!PassiveListener.cancelDefaultAction(spell, casted)) continue;
				event.setCancelled(true);
			}
		}

		if (!typeMapLocal.containsKey(event.getRightClicked().getType())) return;
		Spellbook spellbook = MagicSpells.getSpellbook(event.getPlayer());
		List<PassiveSpell> list = typeMapLocal.get(event.getRightClicked().getType());
		for (PassiveSpell spell : list) {
			if (!isCancelStateOk(spell, event.isCancelled())) continue;
			if (!spellbook.hasSpell(spell)) continue;
			boolean casted = spell.activate(event.getPlayer(), (LivingEntity)event.getRightClicked());
			if (!PassiveListener.cancelDefaultAction(spell, casted)) continue;
			event.setCancelled(true);
		}
	}

	public boolean isMainHand(PassiveTrigger trigger) {
		return PassiveTrigger.RIGHT_CLICK_ENTITY.contains(trigger);
	}

}
