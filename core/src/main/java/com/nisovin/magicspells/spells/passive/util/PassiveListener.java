package com.nisovin.magicspells.spells.passive.util;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.EventPriority;
import org.bukkit.entity.LivingEntity;

import org.jetbrains.annotations.NotNull;

import com.nisovin.magicspells.util.Name;
import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.util.DependsOn;
import com.nisovin.magicspells.spells.PassiveSpell;

/**
 * Annotations:
 * <ul>
 *     <li>{@link Name} (required): Holds the configuration name of the passive listener.</li>
 *     <li>{@link DependsOn} (optional): Requires listed plugins to be enabled before this passive listener is created.</li>
 * </ul>
 */
public abstract class PassiveListener implements Listener {

	protected PassiveSpell passiveSpell;

	protected EventPriority priority;

	public PassiveSpell getPassiveSpell() {
		return passiveSpell;
	}

	public void setPassiveSpell(PassiveSpell passiveSpell) {
		this.passiveSpell = passiveSpell;
	}

	public EventPriority getEventPriority() {
		return priority;
	}

	public void setEventPriority(EventPriority priority) {
		this.priority = priority;
	}

	public boolean canTrigger(LivingEntity livingEntity) {
		return canTrigger(livingEntity, true);
	}

	public boolean canTrigger(LivingEntity livingEntity, boolean ignoreGameMode) {
		if (livingEntity instanceof Player player && !MagicSpells.getSpellbook(player).hasSpell(passiveSpell)) return false;
		return passiveSpell.getTriggerList().canTarget(livingEntity, ignoreGameMode);
	}
		
	public boolean cancelDefaultAction(boolean casted) {
		if (passiveSpell == null) return true;
		if (casted && passiveSpell.cancelDefaultAction()) return true;
		if (!casted && passiveSpell.cancelDefaultActionWhenCastFails()) return true;
		return false;
	}
	
	public boolean isCancelStateOk(boolean cancelled) {
		if (passiveSpell == null) return false;
		if (passiveSpell.ignoreCancelled() && cancelled) return false;
		if (passiveSpell.requireCancelledEvent() && !cancelled) return false;
		return true;
	}
	
	public abstract void initialize(@NotNull String var);

	public void turnOff() {
		// No op
	}
	
	@Override
	public boolean equals(Object other) {
		return this == other; // Don't want to make things equal unless they are the same object
	}
	
}
