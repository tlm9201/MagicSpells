package com.nisovin.magicspells.util.managers;

import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.function.Consumer;

import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;

import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.spells.BuffSpell;
import com.nisovin.magicspells.events.BuffEndEvent;
import com.nisovin.magicspells.events.BuffStartEvent;
import com.nisovin.magicspells.util.compat.EventUtil;
import com.nisovin.magicspells.zones.NoMagicZoneManager;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;

public class BuffManager {

	private Map<LivingEntity, Set<BuffSpell>> activeBuffs;

	private final int interval;

	private BuffMonitor buffMonitor;

	public BuffManager(int interval) {
		this.interval = interval;
		activeBuffs = new HashMap<>();
	}

	public void initialize() {
		buffMonitor = new BuffMonitor();
	}

	public void startBuff(LivingEntity entity, BuffSpell spell) {
		Set<BuffSpell> buffs = activeBuffs.computeIfAbsent(entity, s -> new HashSet<>());
		buffs.add(spell);
		EventUtil.call(new BuffStartEvent(entity, spell));
	}

	public void endBuff(LivingEntity entity, BuffSpell spell) {
		Set<BuffSpell> buffs = activeBuffs.get(entity);
		if (buffs == null) return;
		buffs.remove(spell);
		if (buffs.isEmpty()) activeBuffs.remove(entity);
		EventUtil.call(new BuffEndEvent(entity, spell));
	}

	public Map<LivingEntity, Set<BuffSpell>> getActiveBuffs() {
		return activeBuffs;
	}

	public Set<BuffSpell> getActiveBuffs(LivingEntity entity) {
		return activeBuffs.get(entity);
	}

	public void turnOff() {
		buffMonitor.stop();
		buffMonitor = null;
		activeBuffs.clear();
		activeBuffs = null;
	}

	private class BuffMonitor implements Consumer<ScheduledTask> {

		private final ScheduledTask task;

		private final NoMagicZoneManager zoneManager;

		private BuffMonitor() {
			task = Bukkit.getGlobalRegionScheduler().runAtFixedRate(MagicSpells.getInstance(), this, interval, interval);

			zoneManager = MagicSpells.getNoMagicZoneManager();
		}

		public void stop() {
			task.cancel();
		}

		@Override
		public void accept(ScheduledTask scheduledTask) {
			if (zoneManager == null) return;

			Iterator<LivingEntity> entityIterator = activeBuffs.keySet().iterator();
			Iterator<BuffSpell> buffIterator;
			LivingEntity entity;
			BuffSpell buff;
			while (entityIterator.hasNext()) {
				entity = entityIterator.next();
				if (entity == null) continue;
				if (!entity.isValid()) continue;

				buffIterator = activeBuffs.get(entity).iterator();

				while (buffIterator.hasNext()) {
					buff = buffIterator.next();
					if (!buff.isExpired(entity) && !zoneManager.willFizzle(entity, buff)) continue;

					buff.turnOff(entity, false);
					EventUtil.call(new BuffEndEvent(entity, buff));
					buffIterator.remove();
					if (!buffIterator.hasNext()) entityIterator.remove();
				}
			}
		}
	}

}
