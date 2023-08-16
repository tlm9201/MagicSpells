package com.nisovin.magicspells.spells.targeted;

import java.util.Collection;

import org.bukkit.Location;
import org.bukkit.util.Vector;
import org.bukkit.entity.LivingEntity;

import com.nisovin.magicspells.util.*;
import com.nisovin.magicspells.spells.TargetedSpell;
import com.nisovin.magicspells.util.config.ConfigData;
import com.nisovin.magicspells.events.SpellTargetEvent;
import com.nisovin.magicspells.spelleffects.EffectPosition;
import com.nisovin.magicspells.spells.TargetedLocationSpell;

public class ForcebombSpell extends TargetedSpell implements TargetedLocationSpell {

	private final ConfigData<Double> force;
	private final ConfigData<Double> radius;
	private final ConfigData<Double> yForce;
	private final ConfigData<Double> yOffset;
	private final ConfigData<Double> maxYForce;

	private final ConfigData<Boolean> addYForceInstead;
	private final ConfigData<Boolean> callTargetEvents;
	private final ConfigData<Boolean> powerAffectsForce;
	private final ConfigData<Boolean> addVelocityInstead;

	public ForcebombSpell(MagicConfig config, String spellName) {
		super(config, spellName);

		force = getConfigDataDouble("pushback-force", 30);
		yForce = getConfigDataDouble("additional-vertical-force", 15);
		radius = getConfigDataDouble("radius", 3);
		yOffset = getConfigDataDouble("y-offset", 0F);
		maxYForce = getConfigDataDouble("max-vertical-force", 20);

		addYForceInstead = getConfigDataBoolean("add-y-force-instead", false);
		callTargetEvents = getConfigDataBoolean("call-target-events", true);
		powerAffectsForce = getConfigDataBoolean("power-affects-force", true);
		addVelocityInstead = getConfigDataBoolean("add-velocity-instead", false);
	}

	@Override
	public CastResult cast(SpellData data) {
		TargetInfo<Location> info = getTargetedBlockLocation(data, false);
		if (info.noTarget()) return noTarget(data);

		return castAtLocation(info.spellData());
	}

	@Override
	public CastResult castAtLocation(SpellData data) {
		Location location = data.location();
		if (location.getWorld() == null) return noTarget(data);

		location = location.add(0, yOffset.get(data), 0);

		double radiusSquared = this.radius.get(data);
		radiusSquared *= radiusSquared;

		if (validTargetList.canTargetOnlyCaster()) {
			if (!data.hasCaster() || !data.caster().getWorld().equals(location.getWorld()) || data.caster().getLocation().distanceSquared(location) > radiusSquared) {
				playSpellEffects(EffectPosition.CASTER, data.caster(), data);
				playSpellEffects(EffectPosition.SPECIAL, location, data);

				return new CastResult(PostCastAction.HANDLE_NORMALLY, data);
			}

			bomb(data.caster(), location, data.target(data.caster()));
			playSpellEffects(EffectPosition.CASTER, data.caster(), data);
			playSpellEffects(EffectPosition.SPECIAL, location, data);

			return new CastResult(PostCastAction.HANDLE_NORMALLY, data);
		}

		Collection<LivingEntity> entities = location.getWorld().getLivingEntities();
		for (LivingEntity target : entities) {
			if (!validTargetList.canTarget(data.caster(), target)) continue;
			if (!target.getWorld().equals(location.getWorld())) continue;
			if (target.getLocation().distanceSquared(location) > radiusSquared) continue;

			bomb(target, location, data.target(target));
		}

		if (data.hasCaster()) playSpellEffects(EffectPosition.CASTER, data.caster(), data);
		playSpellEffects(EffectPosition.SPECIAL, location, data);

		return new CastResult(PostCastAction.HANDLE_NORMALLY, data);
	}

	private void bomb(LivingEntity target, Location location, SpellData data) {
		if (callTargetEvents.get(data) && data.hasCaster()) {
			SpellTargetEvent event = new SpellTargetEvent(this, data, target);
			if (!event.callEvent()) return;

			target = event.getTarget();
			data = event.getSpellData();
		}

		double force = this.force.get(data) / 10;
		if (powerAffectsForce.get(data)) force *= data.power();

		Vector v = target.getLocation().toVector().subtract(location.toVector()).normalize().multiply(force);

		double yForce = this.yForce.get(data) / 10;
		if (powerAffectsForce.get(data)) yForce *= data.power();

		double maxYForce = this.maxYForce.get(data) / 10;
		if (addYForceInstead.get(data)) v.setY(Math.min(v.getY() + yForce, maxYForce));
		else v.setY(Math.min(force == 0 ? yForce : v.getY() * yForce, maxYForce));

		v = Util.makeFinite(v);

		if (addVelocityInstead.get(data)) target.setVelocity(target.getVelocity().add(v));
		else target.setVelocity(v);

		playSpellEffects(location, target, data);
	}

}
