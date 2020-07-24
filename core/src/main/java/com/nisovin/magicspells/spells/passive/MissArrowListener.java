package com.nisovin.magicspells.spells.passive;

import java.util.Set;
import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.HashSet;
import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import com.nisovin.magicspells.Spellbook;
import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.spells.PassiveSpell;
import com.nisovin.magicspells.util.OverridePriority;
import com.nisovin.magicspells.util.magicitems.MagicItem;
import com.nisovin.magicspells.util.magicitems.MagicItems;
import com.nisovin.magicspells.util.magicitems.MagicItemData;

public class MissArrowListener extends PassiveListener {
	
	private Set<Material> materials = new HashSet<>();
	private Map<MagicItemData, List<PassiveSpell>> types = new HashMap<>();
	private List<PassiveSpell> allTypes = new ArrayList<>();
	
	@Override
	public void registerSpell(PassiveSpell spell, PassiveTrigger trigger, String var) {
		if (var == null || var.isEmpty()) {
			allTypes.add(spell);
			return;
		}
		String[] split = var.split("\\|");
		for (String s : split) {
			s = s.trim();
			MagicItem magicItem = MagicItems.getMagicItemFromString(s);
			MagicItemData itemData = null;
			if (magicItem != null) itemData = magicItem.getMagicItemData();
			if (itemData == null) continue;

			List<PassiveSpell> list = types.computeIfAbsent(itemData, material -> new ArrayList<>());
			list.add(spell);
			materials.add(itemData.getType());
		}
	}
	
	private Player getPlayerAttacker(EntityDamageByEntityEvent event) {
		Entity e = event.getDamager();
		
		if (!(e instanceof Arrow)) return null;
		
		if (((Arrow) e).getShooter() != null && ((Arrow) e).getShooter() instanceof Player) {
			return (Player) ((Arrow) e).getShooter();
		}
		
		return null;
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
		
		if (!event.getEntity().hasMetadata("mal-" + player.getUniqueId() + '-' + player.getName())) return;
		if (event.getEntity().getMetadata("mal-" + player.getUniqueId() + '-' + player.getName()).isEmpty()) return;
		
		ArrowParticle arrowParticle = (ArrowParticle) event.getEntity().getMetadata("mal-" + player.getUniqueId() + '-' + player.getName()).get(0).value();

		if (arrowParticle.isHitEntity()) return;
		
		Spellbook spellbook = null;
		if (!allTypes.isEmpty()) {
			spellbook = MagicSpells.getSpellbook(player);
			for (PassiveSpell spell : allTypes) {
				if (!spellbook.hasSpell(spell, false)) continue;
				spell.activate(player, event.getEntity().getLocation());
			}
		}
		
		if (types.isEmpty()) return;

		ItemStack item = player.getEquipment().getItemInMainHand();
		
		if (item == null) return;
		if (item.getType() == Material.AIR) return;
		
		List<PassiveSpell> list = getSpells(item);
		
		if (list == null) return;
		
		if (spellbook == null) {
			spellbook = MagicSpells.getSpellbook(player);
		}
		for (PassiveSpell spell : list) {
			if (!spellbook.hasSpell(spell, false)) continue;
			spell.activate(player, event.getEntity().getLocation());
		}
	}
	
	private Player getPlayerAttacker(ProjectileHitEvent event) {
		Projectile e = event.getEntity();
		
		if (!(e instanceof Arrow)) return null;
		
		if (((Arrow) e).getShooter() != null && ((Arrow) e).getShooter() instanceof Player) {
			return (Player) ((Arrow) e).getShooter();
		}
		
		return null;
	}
	
	private List<PassiveSpell> getSpells(ItemStack item) {
		if (!materials.contains(item.getType())) return null;
		MagicItemData itemData = MagicItems.getMagicItemDataFromItemStack(item);
		if (itemData == null) return null;

		for (Map.Entry<MagicItemData, List<PassiveSpell>> entry : types.entrySet()) {
			if (entry.getKey().equals(itemData)) return entry.getValue();
		}
		return null;
	}
	
	@EventHandler
	public void shoot(ProjectileLaunchEvent event) {
		if (event.getEntity() != null && event.getEntity().getShooter() != null
				&& event.getEntity().getShooter() instanceof Player && event.getEntity() instanceof Arrow) {
			Player p = (Player) event.getEntity().getShooter();
			ArrowParticle arrowParticle = new ArrowParticle((Arrow) event.getEntity(), p);
			event.getEntity().setMetadata("mal-" + p.getUniqueId() + '-' + p.getName(), new FixedMetadataValue(MagicSpells.getInstance(), arrowParticle));
		}
	}
	
	class ArrowParticle {
		private Player origCaster;
		Arrow arrow;
		private boolean hitEntity;
		
		ArrowParticle(Arrow arrow, Player origCaster) {
			this.arrow = arrow;
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
