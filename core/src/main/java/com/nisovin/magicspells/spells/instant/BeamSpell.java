package com.nisovin.magicspells.spells.instant;

import java.util.Set;
import java.util.HashSet;
import java.util.function.Predicate;

import org.apache.commons.math4.core.jdkmath.AccurateMath;

import org.bukkit.Location;
import org.bukkit.util.Vector;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import com.nisovin.magicspells.util.*;
import com.nisovin.magicspells.Subspell;
import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.spells.InstantSpell;
import com.nisovin.magicspells.util.config.ConfigData;
import com.nisovin.magicspells.events.SpellTargetEvent;
import com.nisovin.magicspells.zones.NoMagicZoneManager;
import com.nisovin.magicspells.spells.TargetedEntitySpell;
import com.nisovin.magicspells.spelleffects.EffectPosition;
import com.nisovin.magicspells.spells.TargetedLocationSpell;
import com.nisovin.magicspells.spells.TargetedEntityFromLocationSpell;

public class BeamSpell extends InstantSpell implements TargetedLocationSpell, TargetedEntitySpell, TargetedEntityFromLocationSpell {

	private final ConfigData<Vector> relativeOffset;
	private final ConfigData<Vector> targetRelativeOffset;

	private final ConfigData<Double> yOffset;
	private final ConfigData<Double> hitRadius;
	private final ConfigData<Double> maxDistance;
	private final ConfigData<Double> verticalHitRadius;
	private final ConfigData<Double> verticalRotation;
	private final ConfigData<Double> horizontalRotation;

	private final ConfigData<Float> gravity;
	private final ConfigData<Float> interval;
	private final ConfigData<Float> rotation;
	private final ConfigData<Float> beamVertOffset;
	private final ConfigData<Float> beamHorizOffset;

	private final ConfigData<Float> beamVerticalSpread;
	private final ConfigData<Float> beamHorizontalSpread;

	private final ConfigData<Boolean> changePitch;
	private final ConfigData<Boolean> stopOnHitEntity;
	private final ConfigData<Boolean> stopOnHitGround;

	private Subspell hitSpell;
	private Subspell endSpell;
	private Subspell travelSpell;
	private Subspell groundSpell;
	private Subspell entityLocationSpell;

	private final String hitSpellName;
	private final String endSpellName;
	private final String travelSpellName;
	private final String groundSpellName;
	private final String entityLocationSpellName;

	private NoMagicZoneManager zoneManager;

	private static final double ANGLE_Y = AccurateMath.toRadians(-90);

	public BeamSpell(MagicConfig config, String spellName) {
		super(config, spellName);

		relativeOffset = getConfigDataVector("relative-offset", new Vector(0, 0.5, 0));
		targetRelativeOffset = getConfigDataVector("target-relative-offset", new Vector(0, 0.5, 0));

		yOffset = getConfigDataDouble("y-offset", 0F);
		hitRadius = getConfigDataDouble("hit-radius", 2);
		maxDistance = getConfigDataDouble("max-distance", 30);
		verticalHitRadius = getConfigDataDouble("vertical-hit-radius", 2);

		verticalRotation = getConfigDataDouble("vertical-rotation", 0D);
		horizontalRotation = getConfigDataDouble("horizontal-rotation", 0D);

		gravity = getConfigDataFloat("gravity", 0F);
		interval = getConfigDataFloat("interval", 1F);
		rotation = getConfigDataFloat("rotation", 0F);
		beamVertOffset = getConfigDataFloat("beam-vert-offset", 0F);
		beamHorizOffset = getConfigDataFloat("beam-horiz-offset", 0F);

		ConfigData<Float> beamSpread = getConfigDataFloat("beam-spread", 0F);
		beamVerticalSpread = getConfigDataFloat("beam-vertical-spread", beamSpread);
		beamHorizontalSpread = getConfigDataFloat("beam-horizontal-spread", beamSpread);

		changePitch = getConfigDataBoolean("change-pitch", true);
		stopOnHitEntity = getConfigDataBoolean("stop-on-hit-entity", false);
		stopOnHitGround = getConfigDataBoolean("stop-on-hit-ground", false);

		hitSpellName = getConfigString("spell", "");
		endSpellName = getConfigString("spell-on-end", "");
		travelSpellName = getConfigString("spell-on-travel", "");
		groundSpellName = getConfigString("spell-on-hit-ground", "");
		entityLocationSpellName = getConfigString("spell-on-entity-location", "");
	}

	@Override
	public void initialize() {
		super.initialize();

		String prefix = "BeamSpell '" + internalName + "' has an invalid ";

		hitSpell = initSubspell(hitSpellName,
				prefix + "spell defined!",
				true);

		endSpell = initSubspell(endSpellName,
				prefix + "spell-on-end defined!",
				true);

		travelSpell = initSubspell(travelSpellName,
				prefix + "spell-on-travel defined!",
				true);


		groundSpell = initSubspell(groundSpellName,
				prefix + "spell-on-hit-ground defined!",
				true);

		entityLocationSpell = initSubspell(entityLocationSpellName,
				prefix + "spell-on-entity-location defined!",
				true);

		zoneManager = MagicSpells.getNoMagicZoneManager();
	}

	@Override
	public CastResult cast(SpellData data) {
		return castAtEntityFromLocation(data.location(data.caster().getLocation()));
	}

	@Override
	public CastResult castAtLocation(SpellData data) {
		if (!data.hasCaster()) return new CastResult(PostCastAction.ALREADY_HANDLED, data);
		return castAtEntityFromLocation(data);
	}

	@Override
	public CastResult castAtEntity(SpellData data) {
		if (!data.hasCaster()) return new CastResult(PostCastAction.ALREADY_HANDLED, data);
		return castAtEntityFromLocation(data.location(data.caster().getLocation()));
	}

	@Override
	public CastResult castAtEntityFromLocation(SpellData data) {
		if (!data.hasCaster()) return new CastResult(PostCastAction.ALREADY_HANDLED, data);

		Location loc = data.location();
		if (!changePitch.get(data)) {
			loc.setPitch(0);
			data = data.location(loc);
		}

		float beamVertOffset = this.beamVertOffset.get(data);
		if (beamVertOffset != 0) {
			loc.setPitch(loc.getPitch() - beamVertOffset);
			data = data.location(loc);
		}

		float beamHorizOffset = this.beamHorizOffset.get(data);
		if (beamHorizOffset != 0) {
			loc.setYaw(loc.getYaw() + beamHorizOffset);
			data = data.location(loc);
		}

		Vector startDir = data.hasTarget() ? data.target().getLocation().subtract(loc).toVector().normalize() : loc.getDirection();

		//apply relative offset
		Vector relativeOffset = this.relativeOffset.get(data);
		double yOffset = this.yOffset.get(data);
		if (yOffset != 0) relativeOffset = relativeOffset.clone().setY(yOffset);

		Vector horizOffset = new Vector(-startDir.getZ(), 0, startDir.getX()).normalize();
		loc.add(horizOffset.multiply(relativeOffset.getZ()));
		loc.add(loc.getDirection().multiply(relativeOffset.getX()));
		loc.setY(loc.getY() + relativeOffset.getY());

		float interval = this.interval.get(data);
		if (interval < 0.01) interval = 0.01f;

		Vector dir;
		if (!data.hasTarget()) dir = loc.getDirection().multiply(interval);
		else {
			//apply target relative offset
			Vector targetRelativeOffset = this.targetRelativeOffset.get(data);
			Location targetLoc = data.target().getLocation();
			Vector targetDir = targetLoc.getDirection();

			Vector targetHorizOffset = new Vector(-targetDir.getZ(), 0, targetDir.getX()).normalize();
			targetLoc.add(targetHorizOffset.multiply(targetRelativeOffset.getZ()));
			targetLoc.add(targetLoc.getDirection().multiply(targetRelativeOffset.getX()));
			targetLoc.setY(data.target().getLocation().getY() + targetRelativeOffset.getY());

			dir = targetLoc.toVector().subtract(loc.toVector()).normalize().multiply(interval);
		}

		Vector dirNormalized = dir.clone().normalize();

		Vector angleZ = Util.makeFinite(new Vector(-dirNormalized.getZ(), 0D, dirNormalized.getX()).normalize());
		Vector angleY = Util.makeFinite(dirNormalized.rotateAroundAxis(angleZ, ANGLE_Y).normalize());

		double verticalRotation = this.verticalRotation.get(data);
		double horizontalRotation = this.horizontalRotation.get(data);

		if (verticalRotation != 0) dir.rotateAroundAxis(angleZ, AccurateMath.toRadians(verticalRotation));
		if (horizontalRotation != 0) dir.rotateAroundAxis(angleY, AccurateMath.toRadians(horizontalRotation));

		float beamVerticalSpread = this.beamVerticalSpread.get(data);
		float beamHorizontalSpread = this.beamHorizontalSpread.get(data);
		if (beamVerticalSpread > 0 || beamHorizontalSpread > 0) {
			float rx = -1 + random.nextFloat() * 2;
			float ry = -1 + random.nextFloat() * 2;
			float rz = -1 + random.nextFloat() * 2;
			dir.add(new Vector(rx * beamHorizontalSpread, ry * beamVerticalSpread, rz * beamHorizontalSpread));
		}

		double verticalHitRadius = this.verticalHitRadius.get(data);
		double maxDistance = this.maxDistance.get(data);
		double hitRadius = this.hitRadius.get(data);

		float rotation = this.rotation.get(data);
		float gravity = -this.gravity.get(data);

		boolean stopOnHitEntity = this.stopOnHitEntity.get(data);
		boolean stopOnHitGround = this.stopOnHitGround.get(data);

		Predicate<Location> transparent = isTransparent(data);
		Set<Entity> immune = new HashSet<>();
		float d = 0;

		playSpellEffects(EffectPosition.CASTER, data.caster(), data);
		SpellData locData = data.noTargeting();

		mainLoop:
		while (d < maxDistance) {
			d += interval;
			loc.add(dir);

			if (rotation != 0) Util.rotateVector(dir, rotation);
			if (gravity != 0) dir.add(new Vector(0, gravity, 0));
			if (rotation != 0 || gravity != 0) loc.setDirection(dir);

			loc = Util.makeFinite(loc);
			locData = locData.location(loc);

			if (zoneManager.willFizzle(loc, this)) break;

			//check block collision
			if (!transparent.test(loc)) {
				playSpellEffects(EffectPosition.DISABLED, loc, locData);
				if (groundSpell != null) groundSpell.subcast(locData);
				if (stopOnHitGround) break;
			}

			playSpellEffects(EffectPosition.SPECIAL, loc, locData);

			if (travelSpell != null) travelSpell.subcast(locData);

			//check entities in the beam range
			for (LivingEntity e : loc.getNearbyLivingEntities(hitRadius, verticalHitRadius)) {
				if (e == data.caster() || !e.isValid() || immune.contains(e)) continue;
				if (validTargetList != null && !validTargetList.canTarget(e)) continue;

				SpellTargetEvent event = new SpellTargetEvent(this, locData, e);
				if (!event.callEvent()) continue;

				SpellData subData = event.getSpellData();
				LivingEntity entity = event.getTarget();

				if (hitSpell != null) hitSpell.subcast(subData.noLocation());
				if (entityLocationSpell != null) entityLocationSpell.subcast(subData.noTarget());

				playSpellEffects(EffectPosition.TARGET, entity, subData);
				playSpellEffectsTrail(data.caster().getLocation(), entity.getLocation(), subData);
				immune.add(e);

				if (stopOnHitEntity) break mainLoop;
			}
		}

		//end of the beam
		if (!zoneManager.willFizzle(loc, this) && d >= maxDistance) {
			playSpellEffects(EffectPosition.DELAYED, loc, data.location(loc));
			if (endSpell != null) endSpell.subcast(locData);
		}

		return new CastResult(PostCastAction.HANDLE_NORMALLY, data);
	}

	public Subspell getHitSpell() {
		return hitSpell;
	}

	public void setHitSpell(Subspell hitSpell) {
		this.hitSpell = hitSpell;
	}

	public Subspell getEndSpell() {
		return endSpell;
	}

	public void setEndSpell(Subspell endSpell) {
		this.endSpell = endSpell;
	}

	public Subspell getTravelSpell() {
		return travelSpell;
	}

	public void setTravelSpell(Subspell travelSpell) {
		this.travelSpell = travelSpell;
	}

	public Subspell getGroundSpell() {
		return groundSpell;
	}

	public void setGroundSpell(Subspell groundSpell) {
		this.groundSpell = groundSpell;
	}

}
