package com.nisovin.magicspells.castmodifiers.conditions;

import java.util.EnumSet;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;

import io.papermc.paper.world.MoonPhase;

import com.nisovin.magicspells.handlers.DebugHandler;
import com.nisovin.magicspells.castmodifiers.Condition;

public class MoonPhaseCondition extends Condition {

	private MoonPhase phase;
	private MoonPhaseLegacy phaseLegacy;

	@Override
	public boolean initialize(String var) {
		if (var == null || var.isEmpty()) return false;
		String phaseName = var.toUpperCase();

		try {
			phaseLegacy = MoonPhaseLegacy.valueOf(phaseName);
		} catch (IllegalArgumentException ignored) {}
		if (phaseLegacy != null) return true;

		try {
			phase = MoonPhase.valueOf(phaseName);
		} catch (IllegalArgumentException ignored) {
			DebugHandler.debugBadEnumValue(MoonPhase.class, phaseName);
			return false;
		}

		return true;
	}
	
	@Override
	public boolean check(LivingEntity caster) {
		return moonPhase(caster.getLocation());
	}
	
	@Override
	public boolean check(LivingEntity caster, LivingEntity target) {
		return moonPhase(target.getLocation());
	}
	
	@Override
	public boolean check(LivingEntity caster, Location location) {
		return moonPhase(location);
	}

	private boolean moonPhase(Location location) {
		MoonPhase phaseNow = location.getWorld().getMoonPhase();
		if (phaseLegacy == null) return phase == phaseNow;
		return phaseLegacy.phases.contains(phaseNow);
	}

	private enum MoonPhaseLegacy {

		FULL(EnumSet.of(MoonPhase.FULL_MOON)),
		WANING(EnumSet.of(MoonPhase.WANING_GIBBOUS, MoonPhase.LAST_QUARTER, MoonPhase.WANING_CRESCENT)),
		NEW(EnumSet.of(MoonPhase.NEW_MOON)),
		WAXING(EnumSet.of(MoonPhase.WAXING_CRESCENT, MoonPhase.FIRST_QUARTER, MoonPhase.WAXING_GIBBOUS)),
		;

		final EnumSet<MoonPhase> phases;

		MoonPhaseLegacy(EnumSet<MoonPhase> phases) {
			this.phases = phases;
		}

	}

}
