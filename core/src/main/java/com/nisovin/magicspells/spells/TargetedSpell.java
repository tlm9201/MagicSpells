package com.nisovin.magicspells.spells;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;

import com.nisovin.magicspells.util.*;

public abstract class TargetedSpell extends InstantSpell {

	public TargetedSpell(MagicConfig config, String spellName) {
		super(config, spellName);
	}

	@Deprecated
	public void sendMessages(LivingEntity caster, LivingEntity target) {
		sendMessages(new SpellData(caster, target, 1f, null));
	}

	@Deprecated
	public void sendMessages(LivingEntity caster, LivingEntity target, String[] args) {
		sendMessages(new SpellData(caster, target, 1f, args));
	}

	/**
	 * Checks whether two locations are within a certain distance from each other.
	 *
	 * @param loc1  The first location
	 * @param loc2  The second location
	 * @param range The maximum distance
	 * @return true if the distance is less than the range, false otherwise
	 */
	protected boolean inRange(Location loc1, Location loc2, int range) {
		return loc1.distanceSquared(loc2) < range * range;
	}

	/**
	 * This should be called if a target should not be found. It sends the no target message
	 * and returns the appropriate return value.
	 *
	 * @deprecated Use {@link com.nisovin.magicspells.Spell#noTarget} instead.
	 *
	 * @param caster the casting living entity
	 * @return the appropriate PostCastAction value
	 */
	@Deprecated
	protected PostCastAction noTarget(LivingEntity caster) {
		return noTarget(strNoTarget, new SpellData(caster)).action();
	}

	/**
	 * This should be called if a target should not be found. It sends the provided message
	 * and returns the appropriate return value.
	 *
	 * @deprecated Use {@link com.nisovin.magicspells.Spell#noTarget} instead.
	 *
	 * @param caster  the casting living entity
	 * @param message the message to send
	 * @return the appropriate PostCastAction value
	 */
	@Deprecated
	protected PostCastAction noTarget(LivingEntity caster, String message) {
		return noTarget(message, new SpellData(caster)).action();
	}

}
