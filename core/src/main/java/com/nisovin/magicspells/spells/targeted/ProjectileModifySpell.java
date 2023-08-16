package com.nisovin.magicspells.spells.targeted;

import java.util.List;
import java.util.HashSet;
import java.util.Iterator;

import org.apache.commons.math4.core.jdkmath.AccurateMath;

import org.bukkit.Location;
import org.bukkit.util.Vector;
import org.bukkit.util.NumberConversions;

import com.nisovin.magicspells.util.*;
import com.nisovin.magicspells.Subspell;
import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.spells.TargetedSpell;
import com.nisovin.magicspells.util.config.ConfigData;
import com.nisovin.magicspells.castmodifiers.ModifierSet;
import com.nisovin.magicspells.spelleffects.EffectPosition;
import com.nisovin.magicspells.spells.TargetedLocationSpell;
import com.nisovin.magicspells.events.SpellTargetLocationEvent;
import com.nisovin.magicspells.spells.instant.ParticleProjectileSpell;
import com.nisovin.magicspells.util.trackers.ParticleProjectileTracker;

public class ProjectileModifySpell extends TargetedSpell implements TargetedLocationSpell {

	private Subspell projectileSpell;
	private String projectileSpellName;

	private final ConfigData<Boolean> stop;
	private final ConfigData<Boolean> circleShape;
	private final ConfigData<Boolean> affectOwnedProjectiles;
	private final ConfigData<Boolean> affectEnemyProjectiles;

	private final ConfigData<Integer> cone;
	private final ConfigData<Integer> vRadius;
	private final ConfigData<Integer> hRadius;
	private final ConfigData<Integer> maxTargets;

	private final ConfigData<Boolean> pointBlank;
	private final ConfigData<Boolean> claimProjectiles;

	private final SpellFilter filter;

	private final ConfigData<Float> velocity;

	private final ConfigData<Float> acceleration;
	private final ConfigData<Integer> accelerationDelay;

	private final ConfigData<Float> projectileTurn;
	private final ConfigData<Float> projectileVertGravity;
	private final ConfigData<Float> projectileHorizGravity;

	private final ConfigData<Integer> tickInterval;
	private final ConfigData<Integer> spellInterval;
	private final ConfigData<Integer> tickSpellLimit;
	private final ConfigData<Integer> maxEntitiesHit;
	private final ConfigData<Integer> intermediateEffects;
	private final ConfigData<Integer> intermediateHitboxes;
	private final ConfigData<Integer> specialEffectInterval;

	private final ConfigData<Float> hitRadius;
	private final ConfigData<Float> verticalHitRadius;
	private final ConfigData<Integer> groundHitRadius;
	private final ConfigData<Integer> groundVerticalHitRadius;

	private final ConfigData<Double> maxDuration;
	private final ConfigData<Double> maxDistance;

	private final ConfigData<Boolean> hugSurface;
	private final ConfigData<Float> heightFromSurface;

	private final ConfigData<Boolean> controllable;
	private final ConfigData<Boolean> hitGround;
	private final ConfigData<Boolean> hitAirAtEnd;
	private final ConfigData<Boolean> hitAirDuring;
	private final ConfigData<Boolean> hitAirAfterDuration;
	private final ConfigData<Boolean> stopOnHitGround;
	private final ConfigData<Boolean> stopOnModifierFail;

	private Subspell airSpell;
	private Subspell selfSpell;
	private Subspell tickSpell;
	private Subspell entitySpell;
	private Subspell groundSpell;
	private Subspell durationSpell;
	private Subspell modifierSpell;
	private Subspell entityLocationSpell;
	private String airSpellName;
	private String selfSpellName;
	private String tickSpellName;
	private String entitySpellName;
	private String groundSpellName;
	private String durationSpellName;
	private String modifierSpellName;
	private String entityLocationSpellName;

	private ModifierSet projModifiers;
	private List<String> projModifiersStrings;

	public ProjectileModifySpell(MagicConfig config, String spellName) {
		super(config, spellName);

		stop = getConfigDataBoolean("stop", false);
		circleShape = getConfigDataBoolean("circle-shape", false);
		affectOwnedProjectiles = getConfigDataBoolean("affect-owned-projectiles", true);
		affectEnemyProjectiles = getConfigDataBoolean("affect-enemy-projectiles", true);

		projectileSpellName = getConfigString("spell", "");

		cone = getConfigDataInt("cone", 0);
		vRadius = getConfigDataInt("vertical-radius", 5);
		hRadius = getConfigDataInt("horizontal-radius", 10);
		maxTargets = getConfigDataInt("max-targets", 0);

		pointBlank = getConfigDataBoolean("point-blank", true);
		claimProjectiles = getConfigDataBoolean("claim-projectiles", false);

		filter = getConfigSpellFilter();

		velocity = getConfigDataFloat("projectile-velocity", 1F);
		acceleration = getConfigDataFloat("projectile-acceleration", 0F);
		accelerationDelay = getConfigDataInt("projectile-acceleration-delay", 0);

		projectileTurn = getConfigDataFloat("projectile-turn", 0);
		projectileVertGravity = getConfigDataFloat("projectile-vert-gravity", 0F);
		projectileHorizGravity = getConfigDataFloat("projectile-horiz-gravity", 0F);

		tickInterval = getConfigDataInt("tick-interval", 2);
		spellInterval = getConfigDataInt("spell-interval", 20);
		intermediateEffects = getConfigDataInt("intermediate-effects", 0);
		specialEffectInterval = getConfigDataInt("special-effect-interval", 1);

		maxDuration = getConfigDataDouble("max-duration", 0);
		maxDistance = getConfigDataDouble("max-distance", 15);

		intermediateHitboxes = getConfigDataInt("intermediate-hitboxes", 0);
		tickSpellLimit = getConfigDataInt("tick-spell-limit", 0);
		maxEntitiesHit = getConfigDataInt("max-entities-hit", 0);
		hitRadius = getConfigDataFloat("hit-radius", 1.5F);
		verticalHitRadius = getConfigDataFloat("vertical-hit-radius", hitRadius);
		groundHitRadius = getConfigDataInt("ground-hit-radius", 1);
		groundVerticalHitRadius = getConfigDataInt("ground-vertical-hit-radius", groundHitRadius);

		hugSurface = getConfigDataBoolean("hug-surface", false);
		heightFromSurface = getConfigDataFloat("height-from-surface", 0.6F);

		controllable = getConfigDataBoolean("controllable", false);
		hitGround = getConfigDataBoolean("hit-ground", true);
		hitAirAtEnd = getConfigDataBoolean("hit-air-at-end", false);
		hitAirDuring = getConfigDataBoolean("hit-air-during", false);
		hitAirAfterDuration = getConfigDataBoolean("hit-air-after-duration", false);
		stopOnHitGround = getConfigDataBoolean("stop-on-hit-ground", true);
		stopOnModifierFail = getConfigDataBoolean("stop-on-modifier-fail", true);

		airSpellName = getConfigString("spell-on-hit-air", "");
		selfSpellName = getConfigString("spell-on-hit-self", "");
		tickSpellName = getConfigString("spell-on-tick", "");
		groundSpellName = getConfigString("spell-on-hit-ground", "");
		entitySpellName = getConfigString("spell-on-hit-entity", "");
		durationSpellName = getConfigString("spell-on-duration-end", "");
		modifierSpellName = getConfigString("spell-on-modifier-fail", "");
		entityLocationSpellName = getConfigString("spell-on-entity-location", "");

		projModifiersStrings = getConfigStringList("projectile-modifiers", null);
	}

	@Override
	public void initializeModifiers() {
		super.initializeModifiers();

		if (projModifiersStrings != null && !projModifiersStrings.isEmpty()) {
			projModifiers = new ModifierSet(projModifiersStrings, this);
			projModifiersStrings = null;
		}
	}

	@Override
	public void initialize() {
		super.initialize();

		projectileSpell = new Subspell(projectileSpellName);
		if (!projectileSpell.process()) {
			if (!projectileSpellName.isEmpty()) MagicSpells.error("ProjectileModifySpell '" + internalName + "' has an invalid spell defined!");
			projectileSpell = null;
		}
		projectileSpellName = null;

		airSpell = new Subspell(airSpellName);
		if (!airSpell.process()) {
			if (!airSpellName.isEmpty()) MagicSpells.error("ProjectileModifySpell '" + internalName + "' has an invalid spell-on-hit-air defined!");
			airSpell = null;
		}
		airSpellName = null;

		selfSpell = new Subspell(selfSpellName);
		if (!selfSpell.process()) {
			if (!selfSpellName.isEmpty()) MagicSpells.error("ProjectileModifySpell '" + internalName + "' has an invalid spell-on-hit-self defined!");
			selfSpell = null;
		}
		selfSpellName = null;

		tickSpell = new Subspell(tickSpellName);
		if (!tickSpell.process()) {
			if (!tickSpellName.isEmpty()) MagicSpells.error("ProjectileModifySpell '" + internalName + "' has an invalid spell-on-tick defined!");
			tickSpell = null;
		}
		tickSpellName = null;

		groundSpell = new Subspell(groundSpellName);
		if (!groundSpell.process()) {
			if (!groundSpellName.isEmpty()) MagicSpells.error("ProjectileModifySpell '" + internalName + "' has an invalid spell-on-hit-ground defined!");
			groundSpell = null;
		}
		groundSpellName = null;

		entitySpell = new Subspell(entitySpellName);
		if (!entitySpell.process()) {
			if (!entitySpellName.isEmpty()) MagicSpells.error("ProjectileModifySpell '" + internalName + "' has an invalid spell-on-hit-entity defined!");
			entitySpell = null;
		}
		entitySpellName = null;

		durationSpell = new Subspell(durationSpellName);
		if (!durationSpell.process()) {
			if (!durationSpellName.isEmpty()) MagicSpells.error("ProjectileModifySpell '" + internalName + "' has an invalid spell-on-duration-end defined!");
			durationSpell = null;
		}
		durationSpellName = null;

		modifierSpell = new Subspell(modifierSpellName);
		if (!modifierSpell.process()) {
			if (!modifierSpellName.isEmpty()) MagicSpells.error("ProjectileModifySpell '" + internalName + "' has an invalid spell-on-modifier-fail defined!");
			modifierSpell = null;
		}
		modifierSpellName = null;

		entityLocationSpell = new Subspell(entityLocationSpellName);
		if (!entityLocationSpell.process()) {
			if (!entityLocationSpellName.isEmpty()) MagicSpells.error("ProjectileModifySpell '" + internalName + "' has an invalid spell-on-entity-location defined!");
			entityLocationSpell = null;
		}
		entityLocationSpellName = null;
	}

	@Override
	public CastResult cast(SpellData data) {
		if (pointBlank.get(data)) {
			SpellTargetLocationEvent targetEvent = new SpellTargetLocationEvent(this, data, data.caster().getLocation());
			if (!targetEvent.callEvent()) return noTarget(data);
			data = targetEvent.getSpellData();
		} else {
			TargetInfo<Location> info = getTargetedBlockLocation(data, false);
			if (info.noTarget()) return noTarget(info);
			data = info.spellData();
		}

		return castAtLocation(data);
	}

	@Override
	public CastResult castAtLocation(SpellData data) {
		int count = 0;

		Location location = data.location();

		Location fromLoc = data.hasCaster() ? data.caster().getLocation() : location;
		Vector facing = fromLoc.getDirection();
		Vector vLoc = fromLoc.toVector();

		BoundingBox box = new BoundingBox(location, hRadius.get(data), vRadius.get(data));
		double hRadiusSquared = box.getHorizontalRadius() * box.getHorizontalRadius();
		double vRadiusSquared = box.getVerticalRadius() * box.getVerticalRadius();

		Iterator<ParticleProjectileTracker> iterator = new HashSet<>(ParticleProjectileSpell.getProjectileTrackers()).iterator();

		int maxTargets = this.maxTargets.get(data);
		int cone = this.cone.get(data);

		double maxDistanceSquared = this.maxDistance.get(data);
		maxDistanceSquared *= maxDistanceSquared;

		boolean stop = this.stop.get(data);
		boolean hugSurface = this.hugSurface.get(data);
		boolean claimProjectiles = this.claimProjectiles.get(data);

		Location currentLoc;
		while (iterator.hasNext()) {
			ParticleProjectileTracker tracker = iterator.next();
			if (tracker == null || tracker.isStopped()) continue;
			currentLoc = tracker.getCurrentLocation();
			if (currentLoc == null) continue;
			if (!currentLoc.getWorld().equals(location.getWorld())) continue;
			if (!box.contains(currentLoc)) continue;
			if (tracker.getSpell() != null && !filter.check(tracker.getSpell())) continue;

			if (!affectOwnedProjectiles.get(data) && tracker.getCaster() != null && tracker.getCaster().equals(data.caster())) continue;
			if (!affectEnemyProjectiles.get(data) && (tracker.getCaster() == null || !tracker.getCaster().equals(data.caster()))) continue;

			if (circleShape.get(data)) {
				double hDistance = NumberConversions.square(currentLoc.getX() - location.getX()) + NumberConversions.square(currentLoc.getZ() - location.getZ());
				if (hDistance > hRadiusSquared) continue;
				double vDistance = NumberConversions.square(currentLoc.getY() - location.getY());
				if (vDistance > vRadiusSquared) continue;
			}

			if (cone > 0) {
				Vector dir = currentLoc.toVector().subtract(vLoc);
				if (AccurateMath.abs(dir.angle(facing)) > cone) continue;
			}

			if (projectileSpell != null) projectileSpell.subcast(data);

			if (stop) {
				playSpellEffects(EffectPosition.TARGET, currentLoc, data);
				playSpellEffectsTrail(location, currentLoc, data);
				if (data.hasCaster()) playSpellEffectsTrail(data.caster().getLocation(), currentLoc, data);

				count++;

				tracker.stop(false);
				iterator.remove();
				if (maxTargets > 0 && count >= maxTargets) break;
				continue;
			}

			if (claimProjectiles) tracker.setCaster(data.caster());

			tracker.setAcceleration(acceleration.get(data));
			tracker.setAccelerationDelay(accelerationDelay.get(data));

			tracker.setProjectileTurn(projectileTurn.get(data));
			tracker.setProjectileVertGravity(projectileVertGravity.get(data));
			tracker.setProjectileHorizGravity(projectileHorizGravity.get(data));
			tracker.setTickInterval(tickInterval.get(data));
			tracker.setSpellInterval(spellInterval.get(data));
			tracker.setIntermediateEffects(intermediateEffects.get(data));
			tracker.setIntermediateHitboxes(intermediateHitboxes.get(data));
			tracker.setSpecialEffectInterval(specialEffectInterval.get(data));
			tracker.setMaxDistanceSquared(maxDistanceSquared);
			tracker.setMaxDuration(maxDuration.get(data) * TimeUtil.MILLISECONDS_PER_SECOND);
			tracker.setMaxEntitiesHit(maxEntitiesHit.get(data));
			tracker.setHorizontalHitRadius(hitRadius.get(data));
			tracker.setVerticalHitRadius(verticalHitRadius.get(data));
			tracker.setGroundHorizontalHitRadius(groundHitRadius.get(data));
			tracker.setGroundVerticalHitRadius(groundVerticalHitRadius.get(data));
			tracker.setHugSurface(hugSurface);
			tracker.setHeightFromSurface(hugSurface ? heightFromSurface.get(data) : 0);
			tracker.setControllable(controllable.get(data));
			tracker.setHitGround(hitGround.get(data));
			tracker.setHitAirAtEnd(hitAirAtEnd.get(data));
			tracker.setHitAirDuring(hitAirDuring.get(data));
			tracker.setHitAirAfterDuration(hitAirAfterDuration.get(data));
			tracker.setStopOnHitGround(stopOnHitGround.get(data));
			tracker.setStopOnModifierFail(stopOnModifierFail.get(data));
			tracker.setProjectileModifiers(projModifiers);
			tracker.setTickSpellLimit(tickSpellLimit.get(data));
			if (airSpell != null) tracker.setAirSpell(airSpell);
			if (tickSpell != null) tracker.setTickSpell(tickSpell);
			if (selfSpell != null) tracker.setCasterSpell(selfSpell);
			if (groundSpell != null) tracker.setGroundSpell(groundSpell);
			if (entitySpell != null) tracker.setEntitySpell(entitySpell);
			if (durationSpell != null) tracker.setDurationSpell(durationSpell);
			if (modifierSpell != null) tracker.setModifierSpell(modifierSpell);
			if (entityLocationSpell != null) tracker.setEntityLocationSpell(entityLocationSpell);

			tracker.getCurrentVelocity().multiply(velocity.get(data));

			playSpellEffects(EffectPosition.TARGET, currentLoc, data);
			playSpellEffectsTrail(location, currentLoc, data);
			if (data.hasCaster()) playSpellEffectsTrail(data.caster().getLocation(), currentLoc, data);

			count++;

			if (maxTargets > 0 && count >= maxTargets) break;
		}

		if (data.hasCaster()) playSpellEffects(EffectPosition.CASTER, data.caster(), data);
		playSpellEffects(EffectPosition.SPECIAL, location, data);

		return new CastResult(count > 0 ? PostCastAction.HANDLE_NORMALLY : PostCastAction.ALREADY_HANDLED, data);
	}

}
