package com.nisovin.magicspells.util.managers;

import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.entity.LivingEntity;

import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.spells.BuffSpell;
import com.nisovin.magicspells.events.BuffEndEvent;
import com.nisovin.magicspells.events.BuffStartEvent;
import com.nisovin.magicspells.util.compat.EventUtil;
import com.nisovin.magicspells.zones.NoMagicZoneManager;

public class BuffManager {

	private Map<LivingEntity, Set<BuffSpell>> activeBuffs;

	private final int interval;

	private Monitor monitor;

	public BuffManager(int interval) {
		this.interval = interval;
		activeBuffs = new ConcurrentHashMap<>();
		monitor = new Monitor();
	}

	public void addBuff(LivingEntity entity, BuffSpell spell) {
		Set<BuffSpell> buffs = activeBuffs.computeIfAbsent(entity, s -> new HashSet<>());
		// Sanity Check
		if (buffs == null) throw new IllegalStateException("buffs should not be null here");
		buffs.add(spell);

		monitor.run();
		EventUtil.call(new BuffStartEvent(entity, spell));
	}

	public void removeBuff(LivingEntity entity, BuffSpell spell) {
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
		monitor.stop();
		monitor = null;
		activeBuffs.clear();
		activeBuffs = null;
	}

	private class Monitor implements Runnable {

		private final int taskId;

		private Monitor() {
			taskId = MagicSpells.scheduleRepeatingTask(this, interval, interval);
		}

		@Override
		public void run() {
			NoMagicZoneManager zoneManager = MagicSpells.getNoMagicZoneManager();
			if (zoneManager == null) return;

			for (LivingEntity entity : activeBuffs.keySet()) {
				if (entity == null) continue;
				if (!entity.isValid()) continue;

				Set<BuffSpell> buffs = new HashSet<>(activeBuffs.get(entity));

				for (BuffSpell spell : buffs) {
					if (spell.isExpired(entity)) spell.turnOff(entity);
					if (zoneManager.willFizzle(entity, spell)) spell.turnOff(entity);
				}
			}
		}

		public void stop() {
			MagicSpells.cancelTask(taskId);
		}

	}

}
