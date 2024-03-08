package com.nisovin.magicspells.events;

import org.bukkit.Location;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.entity.LivingEntity;

import org.jetbrains.annotations.NotNull;

import com.nisovin.magicspells.spells.instant.PortalSpell;

/**
 * {@link PortalSpell}
 */
public class PortalEvent extends Event implements Cancellable {

	private static final HandlerList handlers = new HandlerList();

	private Location destination;
	private final LivingEntity entity;
	private final PortalSpell portalSpell;

	private boolean cancelled = false;

	public PortalEvent(LivingEntity entity, Location destination, PortalSpell portalSpell) {
		this.entity = entity;
		this.destination = destination;
		this.portalSpell = portalSpell;
	}

	/**
	 * Gets the entity who entered the portal
	 * @return the entity
	 */
	public LivingEntity getEntity() {
		return entity;
	}

	/**
	 * Gets a clone of the portal destination
	 * @return location
	 */
	public Location getDestination() {
		return destination.clone();
	}

	/**
	 * Set the portal destination for this event
	 * @param destination new destination
	 */
	public void setDestination(Location destination) {
		this.destination = destination;
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

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean cancel) {
		cancelled = cancel;
	}

}
