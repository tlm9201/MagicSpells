package com.nisovin.magicspells.events;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import com.nisovin.magicspells.Spell;

public class SpellApplyDamageEvent extends SpellEvent {

	private final LivingEntity target;
	private final double damage;
	private final String spellDamageType;
	private final DamageCause cause;
	private final long timestamp;
	private float modifier;
	private double flat;

	public SpellApplyDamageEvent(Spell spell, LivingEntity caster, LivingEntity target, double damage, DamageCause cause, String spellDamageType) {
		super(spell, caster);

		this.target = target;
		this.spellDamageType = spellDamageType;
		this.damage = damage;
		this.cause = cause;

		timestamp = System.currentTimeMillis();

		modifier = 1.0F;
		flat = 0.0D;
	}

	public void applyDamageModifier(float modifier) {
		this.modifier *= modifier;
	}

	public void applyFlatDamage(double flat) {
		this.flat = flat;
	}

	public LivingEntity getTarget() {
		return target;
	}

	public double getDamage() {
		return damage;
	}

	public DamageCause getCause() {
		return cause;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public float getDamageModifier() {
		return modifier;
	}

	public double getFlatDamage() {
		return flat;
	}

	public double getFinalDamage() {
		return (damage * modifier) + flat;
	}

	public String getSpellDamageType() {
		return spellDamageType;
	}

}
