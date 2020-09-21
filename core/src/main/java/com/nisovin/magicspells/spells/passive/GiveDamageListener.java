package com.nisovin.magicspells.spells.passive;

import java.util.Set;
import java.util.HashSet;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import com.nisovin.magicspells.Spellbook;
import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.util.BlockUtils;
import com.nisovin.magicspells.util.OverridePriority;
import com.nisovin.magicspells.util.magicitems.MagicItem;
import com.nisovin.magicspells.util.magicitems.MagicItems;
import com.nisovin.magicspells.util.magicitems.MagicItemData;
import com.nisovin.magicspells.spells.passive.util.PassiveListener;

// Optional trigger variable that may contain a comma separated list
// Of weapons to trigger on
public class GiveDamageListener extends PassiveListener {

	private final Set<MagicItemData> items = new HashSet<>();

	@Override
	public void initialize(String var) {
		if (var == null || var.isEmpty()) return;

		String[] split = var.split("\\|");
		for (String s : split) {
			s = s.trim();
			MagicItem magicItem = MagicItems.getMagicItemFromString(s);
			MagicItemData itemData = null;
			if (magicItem != null) itemData = magicItem.getMagicItemData();
			if (itemData == null) continue;

			items.add(itemData);
		}
	}
	
	@OverridePriority
	@EventHandler
	public void onDamage(EntityDamageByEntityEvent event) {
		Player player = getPlayerAttacker(event);
		if (player == null || !(event.getEntity() instanceof LivingEntity)) return;
		LivingEntity attacked = (LivingEntity) event.getEntity();
		Spellbook spellbook = MagicSpells.getSpellbook(player);
		
		if (items.isEmpty()) {
			if (!isCancelStateOk(event.isCancelled())) return;
			if (!spellbook.hasSpell(passiveSpell, false)) return;
			boolean casted = passiveSpell.activate(player, attacked);
			if (!cancelDefaultAction(casted)) return;
			event.setCancelled(true);
		}
		
		ItemStack item = player.getEquipment().getItemInMainHand();
		if (item == null) return;
		if (BlockUtils.isAir(item.getType())) return;
		MagicItemData data = MagicItems.getMagicItemDataFromItemStack(item);
		if (!items.contains(data)) return;

		if (!isCancelStateOk(event.isCancelled())) return;
		if (!spellbook.hasSpell(passiveSpell, false)) return;
		boolean casted = passiveSpell.activate(player, attacked);
		if (!cancelDefaultAction(casted)) return;
		event.setCancelled(true);
	}
	
	private Player getPlayerAttacker(EntityDamageByEntityEvent event) {
		Entity e = event.getDamager();
		if (e instanceof Player) return (Player) e;
		if (e instanceof Projectile && ((Projectile) e).getShooter() instanceof Player) {
			return (Player) ((Projectile) e).getShooter();
		}
		return null;
	}

}
