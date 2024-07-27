package com.nisovin.magicspells.listeners;

import org.bukkit.entity.Entity;
import org.bukkit.event.Listener;
import org.bukkit.entity.ArmorStand;
import org.bukkit.event.EventHandler;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;

import com.nisovin.magicspells.Perm;
import com.nisovin.magicspells.Spell;
import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.events.SpellTargetEvent;
import com.nisovin.magicspells.zones.NoMagicZoneManager;
import com.nisovin.magicspells.spelleffects.effecttypes.*;
import com.nisovin.magicspells.events.ParticleProjectileHitEvent;

public class MagicSpellListener implements Listener {

	private final NoMagicZoneManager noMagicZoneManager = MagicSpells.getNoMagicZoneManager();

	@EventHandler
	public void onSpellTarget(SpellTargetEvent event) {
		// Check if target has noTarget permission / is in noMagicZone / is an invisible marker armorstand
		LivingEntity target = event.getTarget();
		Spell spell = event.getSpell();
		if (target == null) return;

		if (Perm.NO_TARGET.has(target)) event.setCancelled(true);
		if (spell != null && noMagicZoneManager != null && noMagicZoneManager.willFizzle(target, spell)) event.setCancelled(true);
		if (isMSEntity(target)) event.setCancelled(true);
	}

	@EventHandler
	public void onProjectileHit(ParticleProjectileHitEvent event) {
		LivingEntity target = event.getTarget();
		if (target == null) return;
		if (isMSEntity(target)) event.setCancelled(true);
	}

	/**
	 * Remove entities in unloaded chunks.
	 * @implNote If you don't need any other listener handling done aside from this one that
	 *           {@link Entity#setPersistent(boolean)} exists. This code is only here due to
	 *           backwards compatibility for removing old entities.
	 */
	@EventHandler
	public void onChunkLoad(ChunkLoadEvent event) {
		for (Entity entity : event.getChunk().getEntities()) {
			if (!isMSEntity(entity)) continue;
			entity.remove();
		}
	}

	@EventHandler
	public void onEntityDamage(EntityDamageEvent event) {
		Entity entity = event.getEntity();
		if (!isMSEntity(entity)) return;
		event.setCancelled(true);
	}

	private boolean isMSEntity(Entity entity) {
		return entity.getScoreboardTags().contains(ArmorStandEffect.ENTITY_TAG) || entity.getScoreboardTags().contains(EntityEffect.ENTITY_TAG);
	}

	@EventHandler
	public void onFireworkDamage(EntityDamageByEntityEvent event) {
		if (!event.getDamager().getPersistentDataContainer().has(FireworksEffect.MS_FIREWORK)) return;
		event.setCancelled(true);
	}

	@EventHandler
	public void onInvPickup(InventoryPickupItemEvent event) {
		if (!event.getItem().getPersistentDataContainer().has(ItemSprayEffect.MS_ITEM_SPRAY)) return;
		event.setCancelled(true);
	}

}
