package com.nisovin.magicspells.spells.passive;

import java.util.Set;
import java.util.Map;
import java.util.List;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import com.nisovin.magicspells.Spellbook;
import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.spells.PassiveSpell;
import com.nisovin.magicspells.util.OverridePriority;
import com.nisovin.magicspells.util.magicitems.MagicItem;
import com.nisovin.magicspells.util.magicitems.MagicItems;
import com.nisovin.magicspells.util.magicitems.MagicItemData;

// Optional trigger variable that may contain a comma separated list
// Of weapons to trigger on
public class GiveDamageListener extends PassiveListener {

	private Set<Material> types = new HashSet<>();
	private Map<MagicItemData, List<PassiveSpell>> weapons = new LinkedHashMap<>();
	private List<PassiveSpell> always = new ArrayList<>();
	
	@Override
	public void registerSpell(PassiveSpell spell, PassiveTrigger trigger, String var) {
		if (var == null || var.isEmpty()) {
			always.add(spell);
			return;
		}
		String[] split = var.split("\\|");
		for (String s : split) {
			s = s.trim();
			MagicItem magicItem = MagicItems.getMagicItemFromString(s);
			MagicItemData magicItemData = null;
			if (magicItem != null) magicItemData = magicItem.getMagicItemData();
			if (magicItemData == null) continue;

			List<PassiveSpell> list = weapons.computeIfAbsent(magicItemData, material -> new ArrayList<>());
			list.add(spell);
			types.add(magicItemData.getType());
		}
	}
	
	@OverridePriority
	@EventHandler
	public void onDamage(EntityDamageByEntityEvent event) {
		Player player = getPlayerAttacker(event);
		if (player == null || !(event.getEntity() instanceof LivingEntity)) return;
		LivingEntity attacked = (LivingEntity) event.getEntity();
		Spellbook spellbook = null;
		
		if (!always.isEmpty()) {
			spellbook = MagicSpells.getSpellbook(player);
			for (PassiveSpell spell : always) {
				if (!isCancelStateOk(spell, event.isCancelled())) continue;
				if (!spellbook.hasSpell(spell, false)) continue;
				boolean casted = spell.activate(player, attacked);
				if (!PassiveListener.cancelDefaultAction(spell, casted)) continue;
				event.setCancelled(true);
			}
		}
		
		if (!weapons.isEmpty()) {
			ItemStack item = player.getEquipment().getItemInMainHand();
			if (item != null && item.getType() != Material.AIR) {
				List<PassiveSpell> list = getSpells(item);
				if (list != null) {
					if (spellbook == null) spellbook = MagicSpells.getSpellbook(player);
					for (PassiveSpell spell : list) {
						if (!isCancelStateOk(spell, event.isCancelled())) continue;
						if (!spellbook.hasSpell(spell, false)) continue;
						boolean casted = spell.activate(player, attacked);
						if (!PassiveListener.cancelDefaultAction(spell, casted)) continue;
						event.setCancelled(true);
					}
				}
			}
		}
	}
	
	private Player getPlayerAttacker(EntityDamageByEntityEvent event) {
		Entity e = event.getDamager();
		if (e instanceof Player) return (Player) e;
		if (e instanceof Projectile && ((Projectile) e).getShooter() instanceof Player) {
			return (Player)((Projectile) e).getShooter();
		}
		return null;
	}
	
	private List<PassiveSpell> getSpells(ItemStack item) {
		if (!types.contains(item.getType())) return null;
		MagicItemData itemData = MagicItems.getMagicItemDataFromItemStack(item);
		if (itemData == null) return null;

		for (Map.Entry<MagicItemData, List<PassiveSpell>> entry : weapons.entrySet()) {
			if (entry.getKey().equals(itemData)) return entry.getValue();
		}
		return null;
	}

}
