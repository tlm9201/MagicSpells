package com.nisovin.magicspells.util;

import org.jetbrains.annotations.UnknownNullability;

import java.util.Arrays;
import java.util.Objects;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;

public record SpellData(LivingEntity caster, LivingEntity target, Location location, float power, String[] args) {

	public static final SpellData NULL = new SpellData(null, null, null, 1f, null);

	public SpellData(LivingEntity caster, LivingEntity target, Location location, float power, String[] args) {
		this.caster = caster;
		this.target = target;
		this.location = location == null ? null : location.clone();
		this.power = power;
		this.args = args;
	}

	public SpellData(LivingEntity caster, LivingEntity target, float power, String[] args) {
		this(caster, target, null, power, args);
	}

	public SpellData(LivingEntity caster, Location location, float power, String[] args) {
		this(caster, null, location, power, args);
	}

	public SpellData(LivingEntity caster, float power, String[] args) {
		this(caster, null, null, power, args);
	}

	public SpellData(LivingEntity caster) {
		this(caster, null, null, 1f, null);
	}

	public Builder builder() {
		return new Builder(this);
	}

	public SpellData invert() {
		LivingEntity caster = this.caster;
		LivingEntity target = this.target;

		if (this.caster != null) target = this.caster;
		if (this.target != null) caster = this.target;

		return Objects.equals(caster, this.caster) && Objects.equals(target, this.target) ? this : new SpellData(caster, target, location, power, args);
	}

	@Override
	@UnknownNullability
	public Location location() {
		return location == null ? null : location.clone();
	}

	public SpellData retarget(LivingEntity target, Location location) {
		return Objects.equals(this.target, target) && Objects.equals(this.location, location) ? this :
			new SpellData(caster, target, location, power, args);
	}

	public SpellData caster(LivingEntity caster) {
		return Objects.equals(this.caster, caster) ? this : new SpellData(caster, target, location, power, args);
	}

	public SpellData target(LivingEntity target) {
		return Objects.equals(this.target, target) ? this : new SpellData(caster, target, location, power, args);
	}

	public SpellData location(Location location) {
		return Objects.equals(this.location, location) ? this :
			new SpellData(caster, target, location == null ? null : location.clone(), power, args);
	}

	public SpellData power(float power) {
		return this.power == power ? this : new SpellData(caster, target, location, power, args);
	}

	public SpellData args(String[] args) {
		return Arrays.equals(this.args, args) ? this : new SpellData(caster, target, location, power, args);
	}

	public SpellData noTargeting() {
		return target == null && location == null ? this : new SpellData(caster, power, args);
	}

	public SpellData noTarget() {
		return target == null ? this : new SpellData(caster, location, power, args);
	}

	public SpellData noLocation() {
		return location == null ? this : new SpellData(caster, target, power, args);
	}

	public boolean hasCaster() {
		return caster != null;
	}

	public boolean hasTarget() {
		return target != null;
	}

	public boolean hasLocation() {
		return location != null;
	}

	public boolean hasArgs() {
		return args != null && args.length > 0;
	}

	public static class Builder {

		private LivingEntity caster;
		private LivingEntity target;
		private Location location;
		private float power;
		private String[] args;

		public Builder() {
			this(SpellData.NULL);
		}

		public Builder(SpellData data) {
			caster = data.caster;
			target = data.target;
			location = data.location;
			power = data.power;
			args = data.args;
		}

		public Builder caster(LivingEntity caster) {
			this.caster = caster;
			return this;
		}

		public Builder target(LivingEntity target) {
			this.target = target;
			return this;
		}

		public Builder location(Location location) {
			this.location = location;
			return this;
		}

		public Builder power(float power) {
			this.power = power;
			return this;
		}

		public Builder args(String[] args) {
			this.args = args;
			return this;
		}

		public SpellData build() {
			return new SpellData(caster, target, location, power, args);
		}

	}

}
