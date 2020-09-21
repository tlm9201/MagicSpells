package com.nisovin.magicspells.spells.passive;

import java.util.Set;
import java.util.HashSet;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
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

public class HitArrowListener extends PassiveListener {

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
			if (cancelDefaultAction(casted)) event.setCancelled(true);

			return;
		}

		ItemStack item = player.getEquipment().getItemInMainHand();
		if (item == null) return;
		if (BlockUtils.isAir(item.getType())) return;
		MagicItemData data = MagicItems.getMagicItemDataFromItemStack(item);
		if (!items.contains(data)) return;

		if (!isCancelStateOk(event.isCancelled())) return;
		if (!spellbook.hasSpell(passiveSpell, false)) return;
		boolean casted = passiveSpell.activate(player, attacked);
		if (cancelDefaultAction(casted)) event.setCancelled(true);
	}
	
	private Player getPlayerAttacker(EntityDamageByEntityEvent event) {
		Entity e = event.getDamager();
		if (!(e instanceof Arrow)) return null;
		if (((Arrow) e).getShooter() != null && ((Arrow) e).getShooter() instanceof Player) {
			return (Player) ((Arrow) e).getShooter();
		}
		return null;
	}

}
