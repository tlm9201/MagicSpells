package com.nisovin.magicspells.spells.passive;

import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.HashSet;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import com.nisovin.magicspells.Spellbook;
import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.spells.PassiveSpell;
import com.nisovin.magicspells.util.OverridePriority;
import com.nisovin.magicspells.util.magicitems.MagicItem;
import com.nisovin.magicspells.util.magicitems.MagicItems;
import com.nisovin.magicspells.util.magicitems.MagicItemData;

// Optional trigger variable of a comma separated list that can contain
// Damage causes to accept or damaging weapons to accept
public class TakeDamageListener extends PassiveListener {

	private Map<DamageCause, List<PassiveSpell>> damageCauses = new HashMap<>();
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
			boolean isDamCause = false;
			for (DamageCause c : DamageCause.values()) {
				if (!s.equalsIgnoreCase(c.name())) continue;
				List<PassiveSpell> spells = damageCauses.computeIfAbsent(c, cause -> new ArrayList<>());
				spells.add(spell);
				isDamCause = true;
				break;
			}
			if (isDamCause) continue;

			MagicItem magicItem = MagicItems.getMagicItemFromString(s);
			MagicItemData itemData = null;
			if (magicItem != null) itemData = magicItem.getMagicItemData();
			if (itemData == null) continue;

			List<PassiveSpell> list = weapons.computeIfAbsent(itemData, m -> new ArrayList<>());
			list.add(spell);
			types.add(itemData.getType());
		}
	}

	@OverridePriority
	@EventHandler
	public void onDamage(EntityDamageEvent event) {
		if (!(event.getEntity() instanceof Player)) return;
		Player player = (Player)event.getEntity();
		LivingEntity attacker = getAttacker(event);
		Spellbook spellbook = null;
		
		if (!always.isEmpty()) {
			spellbook = MagicSpells.getSpellbook(player);
			for (PassiveSpell spell : always) {
				if (!isCancelStateOk(spell, event.isCancelled())) continue;
				if (spellbook.hasSpell(spell, false)) {
					boolean casted = spell.activate(player, attacker);
					if (PassiveListener.cancelDefaultAction(spell, casted)) {
						event.setCancelled(true);
					}
				}
			}
		}
		
		if (!damageCauses.isEmpty()) {
			List<PassiveSpell> causeSpells = damageCauses.get(event.getCause());
			if (causeSpells != null && !causeSpells.isEmpty()) {
				if (spellbook == null) spellbook = MagicSpells.getSpellbook(player);
				for (PassiveSpell spell : causeSpells) {
					if (!isCancelStateOk(spell, event.isCancelled())) continue;
					if (!spellbook.hasSpell(spell, false)) continue;
					boolean casted = spell.activate(player, attacker);
					if (!PassiveListener.cancelDefaultAction(spell, casted)) continue;
					event.setCancelled(true);
				}
			}
		}
		
		if (!weapons.isEmpty()) {
			if (attacker instanceof Player) {
				Player playerAttacker = (Player) attacker;
				ItemStack item = playerAttacker.getEquipment().getItemInMainHand();
				if (item != null && item.getType() != Material.AIR) {
					List<PassiveSpell> list = getSpells(item);
					if (list != null) {
						if (spellbook == null) spellbook = MagicSpells.getSpellbook(player);
						for (PassiveSpell spell : list) {
							if (!isCancelStateOk(spell, event.isCancelled())) continue;
							if (!spellbook.hasSpell(spell, false)) continue;
							boolean casted = spell.activate(player, attacker);
							if (!PassiveListener.cancelDefaultAction(spell, casted)) continue;
							event.setCancelled(true);
						}
					}
				}
			}
		}
	}
	
	private LivingEntity getAttacker(EntityDamageEvent event) {
		if (!(event instanceof EntityDamageByEntityEvent)) return null;
		Entity e = ((EntityDamageByEntityEvent) event).getDamager();
		
		if (e instanceof LivingEntity) return (LivingEntity) e;
		
		if (e instanceof Projectile && ((Projectile) e).getShooter() instanceof LivingEntity) {
			return (LivingEntity)((Projectile) e).getShooter();
		}
		
		return null;
	}
	
	private List<PassiveSpell> getSpells(ItemStack item) {
		if (!types.contains(item.getType())) return null;
		MagicItemData itemData = MagicItems.getMagicItemDataFromItemStack(item);
		if (itemData == null) return null;

		for (MagicItemData m : weapons.keySet()) {
			if (m.equals(itemData)) return weapons.get(m);
		}
		return null;
	}

}
