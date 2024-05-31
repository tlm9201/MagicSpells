package com.nisovin.magicspells.util;

/**
 * Represents an angle that can be applied to a reference angle.
 */
public record Angle(float angle, boolean relative) {

	public static final Angle DEFAULT = new Angle(0, true);

	/**
	 * Applies this angle to a reference angle.
	 *
	 * @param angle the reference angle
	 * @return the modified angle
	 */
	public float apply(final float angle) {
		return this.relative ? this.angle + angle : this.angle;
	}

}
