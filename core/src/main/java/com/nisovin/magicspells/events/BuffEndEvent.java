package com.nisovin.magicspells.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.entity.LivingEntity;

import org.jetbrains.annotations.NotNull;

import com.nisovin.magicspells.spells.BuffSpell;

/**
 * This event is fired whenever a buff spell ends.
 */
public class BuffEndEvent extends Event {

	private static final HandlerList handlers = new HandlerList();

	private final LivingEntity caster;
	private final LivingEntity target;
	private final BuffSpell buffSpell;

	public BuffEndEvent(LivingEntity target, BuffSpell buffSpell) {
		this.caster = buffSpell.getLastCaster(target);
		this.target = target;
		this.buffSpell = buffSpell;
	}

	/**
	 * Gets the caster of the buff
	 * @return the caster
	 */
	public LivingEntity getCaster() {
		return caster;
	}

	/**
	 * Gets the target of the buff
	 * @return the target
	 */
	public LivingEntity getTarget() {
		return target;
	}

	/**
	 * Gets the buff spell which started
	 * @return the spell
	 */
	public BuffSpell getBuffSpell() {
		return buffSpell;
	}

	@NotNull
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

}
