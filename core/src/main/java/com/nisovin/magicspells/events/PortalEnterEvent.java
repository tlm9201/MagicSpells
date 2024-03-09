package com.nisovin.magicspells.events;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;

import com.nisovin.magicspells.spells.instant.PortalSpell;

/**
 * This event is fired whenever an entity enters a portal from {@link PortalSpell}.
 */
public class PortalEnterEvent extends PortalEvent {

	public PortalEnterEvent(LivingEntity entity, Location destination, PortalSpell portalSpell) {
		super(entity, destination, portalSpell);
	}

}
