package com.nisovin.magicspells.events;

import org.bukkit.event.Cancellable;
import org.bukkit.entity.LivingEntity;

import com.nisovin.magicspells.Spell;
import com.nisovin.magicspells.util.trackers.ParticleProjectileTracker;

public class ParticleProjectileHitEvent extends SpellEvent implements Cancellable {

	private LivingEntity target;

	private ParticleProjectileTracker tracker;

	private float power;

	private boolean cancelled = false;

	public ParticleProjectileHitEvent(LivingEntity caster, LivingEntity target, ParticleProjectileTracker tracker, Spell spell, float power) {
		super(spell, caster);

		this.target = target;
		this.tracker = tracker;
		this.power = power;
	}

	public ParticleProjectileTracker getTracker() {
		return tracker;
	}

	public void setTracker(ParticleProjectileTracker tracker) {
		this.tracker = tracker;
	}

	public LivingEntity getTarget() {
		return target;
	}

	public void setTarget(LivingEntity target) {
		this.target = target;
	}

	public float getPower() {
		return power;
	}

	public void setPower(float power) {
		this.power = power;
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
