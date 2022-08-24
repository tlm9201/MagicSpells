package com.nisovin.magicspells.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.entity.LivingEntity;

import org.jetbrains.annotations.NotNull;

import com.nisovin.magicspells.spells.instant.PortalSpell;

/**
 * This event is fired whenever an entity leaves a portal from PortalSpell.
 */
public class PortalLeaveEvent extends Event {

	private static final HandlerList handlers = new HandlerList();

	private final LivingEntity entity;

	private final PortalSpell portalSpell;

	public PortalLeaveEvent(LivingEntity entity, PortalSpell portalSpell) {
		this.entity = entity;
		this.portalSpell = portalSpell;
	}

	/**
	 * Gets the entity who left the portal
	 * @return the entity
	 */
	public LivingEntity getEntity() {
		return entity;
	}

	/**
	 * Gets the portal spell
	 * @return the spell
	 */
	public PortalSpell getPortalSpell() {
		return portalSpell;
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
