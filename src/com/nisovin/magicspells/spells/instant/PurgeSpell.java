package com.nisovin.magicspells.spells.instant;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.events.SpellTargetEvent;
import com.nisovin.magicspells.spelleffects.EffectPosition;
import com.nisovin.magicspells.spells.InstantSpell;
import com.nisovin.magicspells.spells.TargetedLocationSpell;
import com.nisovin.magicspells.util.BoundingBox;
import com.nisovin.magicspells.util.EventUtil;
import com.nisovin.magicspells.util.MagicConfig;
import com.nisovin.magicspells.util.Util;

public class PurgeSpell extends InstantSpell implements TargetedLocationSpell {
	
	private int radius;
	private List<EntityType> entities;
	
	public PurgeSpell(MagicConfig config, String spellName) {
		super(config, spellName);
		
		radius = getConfigInt("range", 15);
		
		List<String> list = getConfigStringList("entities", null);
		if (list != null && !list.isEmpty()) {
			entities = new ArrayList<>();
			for (String s : list) {
				EntityType t = Util.getEntityType(s);
				if (t != null) {
					entities.add(t);
				} else {
					MagicSpells.error("Invalid entity on Purge Spell " + spellName + ": " + s);
				}
			}
			if (entities.isEmpty()) entities = null;
		}
	}

	@Override
	public PostCastAction castSpell(Player player, SpellCastState state, float power, String[] args) {
		if (state == SpellCastState.NORMAL) {
			int range = Math.round(this.radius * power);
			List<Entity> entities = player.getNearbyEntities(range, range, range);
			boolean killed = false;
			for (Entity entity : entities) {
				// TODO verify that entities cannot enter this loop as null
				// TODO remove the redundant null check otherwise
				if (entity instanceof LivingEntity && !(entity instanceof Player) && (entities == null || entities.contains(entity.getType())) && validTargetList.canTarget(player, entity)) {
					((LivingEntity)entity).setHealth(0);
					killed = true;
					playSpellEffects(EffectPosition.TARGET, entity);
				}
			}
			if (killed) {
				playSpellEffects(EffectPosition.CASTER, player);
			} else {
				return PostCastAction.ALREADY_HANDLED;
			}
		}
		return PostCastAction.HANDLE_NORMALLY;
	}

	@Override
	public boolean castAtLocation(Player caster, Location target, float power) {
		boolean success = false;
		BoundingBox box = new BoundingBox(target, radius * power);
		for (Entity e : target.getWorld().getEntities()) {
			if (!box.contains(e)) continue;
			if (entities != null && !entities.contains(e.getType())) continue;
			if (e instanceof LivingEntity) {
				if (caster != null) {
					if (!validTargetList.canTarget(caster, e)) continue;
				} else {
					if (!validTargetList.canTarget(e)) continue;
				}
				SpellTargetEvent event = new SpellTargetEvent(this, caster, (LivingEntity)e, power);
				EventUtil.call(event);
				if (event.isCancelled()) continue;
				success = true;
				((LivingEntity)e).setHealth(0);
				playSpellEffects(EffectPosition.TARGET, e.getLocation());
			} else {
				success = true;
				e.remove();
				playSpellEffects(EffectPosition.TARGET, e.getLocation());
			}
		}
		return success;
	}

	@Override
	public boolean castAtLocation(Location target, float power) {
		return castAtLocation(null, target, power);
	}	
	
}
