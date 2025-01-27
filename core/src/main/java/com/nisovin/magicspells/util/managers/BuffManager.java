package com.nisovin.magicspells.util.managers;

import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import com.google.common.collect.Multimaps;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.HashMultimap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.entity.LivingEntity;

import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.spells.BuffSpell;
import com.nisovin.magicspells.events.BuffEndEvent;
import com.nisovin.magicspells.events.BuffStartEvent;
import com.nisovin.magicspells.zones.NoMagicZoneManager;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;

public class BuffManager {

	private final SetMultimap<LivingEntity, BuffSpell> activeBuffs;
	private final BuffMonitor buffMonitor;
	private final int interval;

	public BuffManager(int interval) {
		this.interval = interval;

		activeBuffs = HashMultimap.create();
		buffMonitor = new BuffMonitor();
	}

	public void startBuff(LivingEntity entity, BuffSpell spell) {
		activeBuffs.put(entity, spell);
		new BuffStartEvent(entity, spell).callEvent();
	}

	public void endBuff(LivingEntity entity, BuffSpell spell) {
		activeBuffs.remove(entity, spell);
		new BuffEndEvent(entity, spell).callEvent();
	}

	public Map<LivingEntity, Set<BuffSpell>> getActiveBuffs() {
		return Multimaps.asMap(activeBuffs);
	}

	public Set<BuffSpell> getActiveBuffs(LivingEntity entity) {
		return activeBuffs.get(entity);
	}

	public void turnOff() {
		buffMonitor.stop();
		activeBuffs.clear();
	}

	private class BuffMonitor implements Consumer<ScheduledTask> {

		private final ScheduledTask task;

		private BuffMonitor() {
			task = Bukkit.getGlobalRegionScheduler().runAtFixedRate(MagicSpells.getInstance(), this, interval, interval);
		}

		public void stop() {
			task.cancel();
		}

		@Override
		public void accept(ScheduledTask scheduledTask) {
			NoMagicZoneManager zoneManager = MagicSpells.getNoMagicZoneManager();

			activeBuffs.entries().removeIf(entry -> {
				LivingEntity entity = entry.getKey();
				BuffSpell buff = entry.getValue();
				if ((entity instanceof Player || entity.isValid()) && !buff.isExpired(entity) && !zoneManager.willFizzle(entity, buff))
					return false;

				buff.turnOff(entity, false);
				new BuffEndEvent(entity, buff).callEvent();
				return true;
			});
		}

	}

}
