package com.nisovin.magicspells.events;

import org.bukkit.Location;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import org.jetbrains.annotations.NotNull;

import com.nisovin.magicspells.util.trackers.Tracker;

public class TrackerMoveEvent extends Event {

	private static final HandlerList handlers = new HandlerList();

	private final Tracker tracker;

	private final Location from;
	private final Location to;

	public TrackerMoveEvent(Tracker tracker, Location from, Location to) {
		this.tracker = tracker;
		this.from = from;
		this.to = to;
	}

	public Tracker getTracker() {
		return tracker;
	}

	public Location getFrom() {
		return from;
	}

	public Location getTo() {
		return to;
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
