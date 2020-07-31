package com.nisovin.magicspells.spells.passive;

import java.util.Set;
import java.util.HashSet;

import org.bukkit.event.HandlerList;

import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.spells.PassiveSpell;

public class PassiveManager {

	Set<PassiveListener> listeners = new HashSet<>();
	Set<PassiveTrigger> triggers = new HashSet<>();

	boolean initialized = false;
	
	public void registerSpell(PassiveSpell spell, PassiveTrigger trigger, String var) {
		triggers.add(trigger);
		PassiveListener listener = trigger.getListener();
		if (listener == null) {
			MagicSpells.error("Failed to register passive spell (no listener): " + spell.getInternalName() + ", " + trigger.getName());
			return;
		}

		listeners.add(listener);
		listener.registerSpell(spell, trigger, var);
	}
	
	public void initialize() {
		if (initialized) return;
		initialized = true;
		for (PassiveListener listener : listeners) {
			listener.initialize();
		}
	}
	
	public void turnOff() {
		for (PassiveListener listener : listeners) {
			HandlerList.unregisterAll(listener);
			listener.turnOff();
		}

		for (PassiveTrigger trigger : triggers) {
			trigger.listener = null;
		}

		listeners.clear();
	}
	
}
