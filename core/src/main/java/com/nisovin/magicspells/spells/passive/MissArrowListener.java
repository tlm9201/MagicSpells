package com.nisovin.magicspells.spells.passive;

import java.util.Set;
import java.util.UUID;
import java.util.HashSet;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import com.nisovin.magicspells.Spellbook;
import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.util.OverridePriority;
import com.nisovin.magicspells.util.magicitems.MagicItem;
import com.nisovin.magicspells.util.magicitems.MagicItems;
import com.nisovin.magicspells.util.magicitems.MagicItemData;
import com.nisovin.magicspells.spells.passive.util.PassiveListener;

public class MissArrowListener extends PassiveListener {

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
	public void onHitEntity(EntityDamageByEntityEvent event) {
		Player p = getPlayerAttacker(event);
		if (p == null) return;
		if (event.getDamager() instanceof Arrow && event.getDamager().hasMetadata("mal-" + p.getUniqueId() + '-' + p.getName())
				&& !event.getEntity().getMetadata("mal-" + p.getUniqueId() + '-' + p.getName()).isEmpty()) {
			((ArrowParticle) event.getDamager().getMetadata("mal-" + p.getUniqueId() + '-' + p.getName()).get(0).value()).setHitEntity(true);
		}
	}
	
	@OverridePriority
	@EventHandler
	public void onDamage(ProjectileHitEvent event) {
		Player player = getPlayerAttacker(event);
		if (player == null) return;
		if (!(event.getEntity() instanceof Arrow)) return;

		String name = player.getName();
		UUID id = player.getUniqueId();
		
		if (!event.getEntity().hasMetadata("mal-" + id + '-' + name)) return;
		if (event.getEntity().getMetadata("mal-" + id + '-' + name).isEmpty()) return;
		
		ArrowParticle arrowParticle = (ArrowParticle) event.getEntity().getMetadata("mal-" + id + '-' + name).get(0).value();

		if (arrowParticle.isHitEntity()) return;
		
		Spellbook spellbook = MagicSpells.getSpellbook(player);
		if (items.isEmpty()) {
			if (!spellbook.hasSpell(passiveSpell, false)) return;
			passiveSpell.activate(player, event.getEntity().getLocation());
			return;
		}

		ItemStack item = player.getEquipment().getItemInMainHand();
		
		if (item == null) return;
		if (item.getType().isAir()) return;

		MagicItemData itemData = MagicItems.getMagicItemDataFromItemStack(item);
		if (!items.contains(itemData)) return;

		if (!spellbook.hasSpell(passiveSpell, false)) return;
		passiveSpell.activate(player, event.getEntity().getLocation());
	}
	
	private Player getPlayerAttacker(ProjectileHitEvent event) {
		Projectile e = event.getEntity();
		if (!(e instanceof Arrow)) return null;
		if (e.getShooter() != null && e.getShooter() instanceof Player) {
			return (Player) e.getShooter();
		}
		return null;
	}

	private Player getPlayerAttacker(EntityDamageByEntityEvent event) {
		Entity e = event.getDamager();
		if (!(e instanceof Arrow)) return null;
		if (((Arrow) e).getShooter() != null && ((Arrow) e).getShooter() instanceof Player) {
			return (Player) ((Arrow) e).getShooter();
		}
		return null;
	}
	
	@EventHandler
	public void shoot(ProjectileLaunchEvent event) {
		if (event.getEntity() != null && event.getEntity().getShooter() != null
				&& event.getEntity().getShooter() instanceof Player && event.getEntity() instanceof Arrow) {
			Player p = (Player) event.getEntity().getShooter();
			ArrowParticle arrowParticle = new ArrowParticle(p);
			event.getEntity().setMetadata("mal-" + p.getUniqueId() + '-' + p.getName(), new FixedMetadataValue(MagicSpells.getInstance(), arrowParticle));
		}
	}
	
	private static class ArrowParticle {

		private Player origCaster;
		private boolean hitEntity;
		
		private ArrowParticle(Player origCaster) {
			this.origCaster = origCaster;
		}
		
		public Player getOrigCaster() {
			return origCaster;
		}
		
		public boolean isHitEntity() {
			return hitEntity;
		}
		
		public void setHitEntity(boolean hitEntity) {
			this.hitEntity = hitEntity;
		}

	}

}
