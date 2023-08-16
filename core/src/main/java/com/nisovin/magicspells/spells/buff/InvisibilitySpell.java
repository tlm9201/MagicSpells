package com.nisovin.magicspells.spells.buff;

import java.util.*;

import org.bukkit.Bukkit;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;

import com.nisovin.magicspells.util.Util;
import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.util.SpellData;
import com.nisovin.magicspells.spells.BuffSpell;
import com.nisovin.magicspells.util.MagicConfig;
import com.nisovin.magicspells.util.config.ConfigData;

public class InvisibilitySpell extends BuffSpell {

	private final Map<UUID, Boolean> entities;

	private final ConfigData<Double> mobRadius;

	private final ConfigData<Boolean> preventPickups;

	public InvisibilitySpell(MagicConfig config, String spellName) {
		super(config, spellName);

		mobRadius = getConfigDataDouble("mob-radius", 30);

		preventPickups = getConfigDataBoolean("prevent-pickups", true);

		entities = new HashMap<>();
	}
	
	@Override
	public void initialize() {
		super.initialize();
	}

	@Override
	public boolean castBuff(SpellData data) {
		if (!(data.target() instanceof Player target)) return false;
		makeInvisible(target, data);
		entities.put(target.getUniqueId(), preventPickups.get(data));
		return true;
	}

	@Override
	public boolean isActive(LivingEntity entity) {
		return entities.containsKey(entity.getUniqueId());
	}

	@Override
	public void turnOffBuff(LivingEntity entity) {
		entities.remove(entity.getUniqueId());
		if (!(entity instanceof Player player)) return;
		Util.forEachPlayerOnline(p -> p.showPlayer(MagicSpells.getInstance(), player));
	}

	@Override
	protected void turnOff() {
		entities.clear();
	}

	private void makeInvisible(Player player, SpellData data) {
		Util.forEachPlayerOnline(p -> p.hidePlayer(MagicSpells.getInstance(), player));

		double radius = Math.min(mobRadius.get(data), MagicSpells.getGlobalRadius());
		for (Entity entity : player.getNearbyEntities(radius, radius, radius)) {
			if (!(entity instanceof Mob mob)) continue;

			LivingEntity target = mob.getTarget();
			if (target == null || !target.equals(player)) continue;

			mob.setTarget(null);
		}
	}
	
	@EventHandler
	public void onEntityItemPickup(EntityPickupItemEvent event) {
		Boolean preventPickups = entities.get(event.getEntity().getUniqueId());
		if (preventPickups == null || !preventPickups) return;

		event.setCancelled(true);
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onEntityTarget(EntityTargetEvent event) {
		Entity target = event.getTarget();
		if (!(target instanceof LivingEntity) || !isActive((LivingEntity) target)) return;

		event.setCancelled(true);
	}
	
	@EventHandler(priority=EventPriority.MONITOR)
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();

		for (UUID id : entities.keySet()) {
			Entity entity = Bukkit.getEntity(id);
			if (!(entity instanceof Player p)) continue;

			player.hidePlayer(MagicSpells.getInstance(), p);
		}

		if (isActive(player)) Util.forEachPlayerOnline(p -> p.hidePlayer(MagicSpells.getInstance(), player));
	}

	public Map<UUID, Boolean> getEntities() {
		return entities;
	}

}
