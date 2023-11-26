package com.nisovin.magicspells.spells.targeted;

import java.util.Map;
import java.util.Set;
import java.util.HashSet;

import org.bukkit.Location;
import org.bukkit.util.Vector;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.LivingEntity;

import com.nisovin.magicspells.util.*;
import com.nisovin.magicspells.Subspell;
import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.spells.TargetedSpell;
import com.nisovin.magicspells.util.config.ConfigData;
import com.nisovin.magicspells.events.SpellTargetEvent;
import com.nisovin.magicspells.spelleffects.SpellEffect;
import com.nisovin.magicspells.spells.TargetedEntitySpell;
import com.nisovin.magicspells.spelleffects.EffectPosition;
import com.nisovin.magicspells.spells.TargetedLocationSpell;
import com.nisovin.magicspells.spelleffects.util.EffectlibSpellEffect;

import de.slikey.effectlib.Effect;
import de.slikey.effectlib.effect.ModifiedEffect;

public class OrbitSpell extends TargetedSpell implements TargetedEntitySpell, TargetedLocationSpell {

	private static Set<OrbitTracker> trackerSet;

	private final ValidTargetList entityTargetList;

	private final ConfigData<Double> maxDuration;

	private final ConfigData<Integer> tickInterval;
	private final ConfigData<Integer> vertExpandDelay;
	private final ConfigData<Integer> horizExpandDelay;

	private final ConfigData<Float> yOffset;
	private final ConfigData<Float> hitRadius;
	private final ConfigData<Float> orbitRadius;
	private final ConfigData<Float> horizOffset;
	private final ConfigData<Float> vertExpandRadius;
	private final ConfigData<Float> verticalHitRadius;
	private final ConfigData<Float> horizExpandRadius;
	private final ConfigData<Float> secondsPerRevolution;

	private final ConfigData<Boolean> followYaw;
	private final ConfigData<Boolean> stopOnHitEntity;
	private final ConfigData<Boolean> stopOnHitGround;
	private final ConfigData<Boolean> counterClockwise;
	private final ConfigData<Boolean> requireEntityTarget;

	private String orbitSpellName;
	private String groundSpellName;
	private String entitySpellName;

	private Subspell orbitSpell;
	private Subspell groundSpell;
	private Subspell entitySpell;

	public OrbitSpell(MagicConfig config, String spellName) {
		super(config, spellName);

		trackerSet = new HashSet<>();

		entityTargetList = new ValidTargetList(this, getConfigStringList("can-hit", null));

		maxDuration = getConfigDataDouble("max-duration", 20);

		tickInterval = getConfigDataInt("tick-interval", 2);
		vertExpandDelay = getConfigDataInt("vert-expand-delay", 0);
		horizExpandDelay = getConfigDataInt("horiz-expand-delay", 0);

		yOffset = getConfigDataFloat("y-offset", 0.6F);
		hitRadius = getConfigDataFloat("hit-radius", 1F);
		orbitRadius = getConfigDataFloat("orbit-radius", 1F);
		horizOffset = getConfigDataFloat("start-horiz-offset", 0);
		vertExpandRadius = getConfigDataFloat("vert-expand-radius", 0);
		verticalHitRadius = getConfigDataFloat("vertical-hit-radius", 1F);
		horizExpandRadius = getConfigDataFloat("horiz-expand-radius", 0);
		secondsPerRevolution = getConfigDataFloat("seconds-per-revolution", 3F);

		followYaw = getConfigDataBoolean("follow-yaw", false);
		stopOnHitEntity = getConfigDataBoolean("stop-on-hit-entity", false);
		stopOnHitGround = getConfigDataBoolean("stop-on-hit-ground", false);
		counterClockwise = getConfigDataBoolean("counter-clockwise", false);
		requireEntityTarget = getConfigDataBoolean("require-entity-target", true);

		orbitSpellName = getConfigString("spell", "");
		groundSpellName = getConfigString("spell-on-hit-ground", "");
		entitySpellName = getConfigString("spell-on-hit-entity", "");
	}

	@Override
	public void initialize() {
		super.initialize();

		String prefix = "OrbitSpell '" + internalName + "' has an invalid ";

		orbitSpell = initSubspell(orbitSpellName,
				prefix + "spell defined!",
				true);

		groundSpell = initSubspell(groundSpellName,
				prefix + "spell-on-hit-ground defined!",
				true);

		entitySpell = initSubspell(entitySpellName,
				prefix + "spell-on-hit-entity defined!",
				true);
	}

	@Override
	public void turnOff() {
		for (OrbitTracker tracker : trackerSet) tracker.stop(false);
		trackerSet.clear();
	}

	@Override
	public CastResult cast(SpellData data) {
		if (requireEntityTarget.get(data)) {
			TargetInfo<LivingEntity> info = getTargetedEntity(data);
			if (info.noTarget()) return noTarget(info);

			data = info.spellData();
			data = data.location(data.target().getLocation());

			new OrbitTracker(data);
			return new CastResult(PostCastAction.HANDLE_NORMALLY, data);
		}

		TargetInfo<Location> info = getTargetedBlockLocation(data, 0.5, 0, 0.5);
		if (info.noTarget()) return noTarget(info);
		data = info.spellData();

		new OrbitTracker(data);
		return new CastResult(PostCastAction.HANDLE_NORMALLY, data);
	}

	@Override
	public CastResult castAtEntity(SpellData data) {
		if (!data.hasCaster()) return new CastResult(PostCastAction.ALREADY_HANDLED, data);

		data = data.location(data.target().getLocation());
		new OrbitTracker(data);
		return new CastResult(PostCastAction.HANDLE_NORMALLY, data);
	}

	@Override
	public CastResult castAtLocation(SpellData data) {
		if (!data.hasCaster()) return new CastResult(PostCastAction.ALREADY_HANDLED, data);

		new OrbitTracker(data);
		return new CastResult(PostCastAction.HANDLE_NORMALLY, data);
	}

	public boolean hasOrbit(LivingEntity target) {
		for (OrbitTracker orbitTracker : trackerSet)
			if (this == orbitTracker.getOrbitSpell() && target.equals(orbitTracker.data.target()))
				return true;

		return false;
	}

	public void removeOrbits(LivingEntity target) {
		trackerSet.removeIf(tracker -> {
			if (OrbitSpell.this != tracker.getOrbitSpell()) return false;
			if (!target.equals(tracker.data.target())) return false;

			tracker.stop(false);
			return true;
		});
	}

	private class OrbitTracker implements Runnable {

		private SpellData data;

		private final BoundingBox box;
		private final Vector currentDirection;
		private final Location currentLocation;

		private final Set<LivingEntity> immune;
		private final Set<ArmorStand> armorStandSet;
		private final Map<SpellEffect, Entity> entityMap;
		private final Set<EffectlibSpellEffect> effectSet;

		private final boolean followYaw;
		private final boolean stopOnHitEntity;
		private final boolean stopOnHitGround;
		private final boolean counterClockwise;

		private final int taskId;
		private final int repeatingVertTaskId;
		private final int repeatingHorizTaskId;

		private final long startTime;

		private float yOffset;
		private float previousYaw;
		private float orbitRadius;
		private final float distancePerTick;

		private final double maxDuration;

		private OrbitTracker(SpellData data) {
			startTime = System.currentTimeMillis();

			currentLocation = data.location();
			previousYaw = currentLocation.getYaw();
			box = new BoundingBox(currentLocation, hitRadius.get(data), verticalHitRadius.get(data));

			currentDirection = currentLocation.getDirection().setY(0).normalize();
			Util.rotateVector(currentDirection, horizOffset.get(data));

			followYaw = OrbitSpell.this.followYaw.get(data);
			stopOnHitEntity = OrbitSpell.this.stopOnHitEntity.get(data);
			stopOnHitGround = OrbitSpell.this.stopOnHitGround.get(data);
			counterClockwise = OrbitSpell.this.counterClockwise.get(data);

			int tickInterval = OrbitSpell.this.tickInterval.get(data);
			taskId = MagicSpells.scheduleRepeatingTask(this, 0, tickInterval);

			orbitRadius = OrbitSpell.this.orbitRadius.get(data);
			int horizExpandDelay = OrbitSpell.this.horizExpandDelay.get(data);
			if (horizExpandDelay > 0) {
				float horizExpandRadius = OrbitSpell.this.horizExpandRadius.get(data);
				repeatingHorizTaskId = MagicSpells.scheduleRepeatingTask(() -> orbitRadius += horizExpandRadius, horizExpandDelay, horizExpandDelay);
			} else repeatingHorizTaskId = -1;

			yOffset = OrbitSpell.this.yOffset.get(data);
			int vertExpandDelay = OrbitSpell.this.vertExpandDelay.get(data);
			if (vertExpandDelay > 0) {
				float vertExpandRadius = OrbitSpell.this.vertExpandRadius.get(data);
				repeatingVertTaskId = MagicSpells.scheduleRepeatingTask(() -> yOffset += vertExpandRadius, vertExpandDelay, vertExpandDelay);
			} else repeatingVertTaskId = -1;

			distancePerTick = 6.28f * tickInterval / secondsPerRevolution.get(data) / 20;

			maxDuration = OrbitSpell.this.maxDuration.get(data) * TimeUtil.MILLISECONDS_PER_SECOND;

			this.data = data;

			immune = new HashSet<>();

			entityMap = playSpellEntityEffects(EffectPosition.PROJECTILE, currentLocation, data);
			effectSet = playSpellEffectLibEffects(EffectPosition.PROJECTILE, currentLocation, data);
			armorStandSet = playSpellArmorStandEffects(EffectPosition.PROJECTILE, currentLocation, data);

			if (data.hasTarget()) playSpellEffects(data.caster(), data.target(), data);
			else playSpellEffects(data.caster(), currentLocation, data);
		}

		@Override
		public void run() {
			if (!data.caster().isValid() || (data.hasTarget() && !data.target().isValid())) {
				stop(true);
				return;
			}

			if (maxDuration > 0 && startTime + maxDuration < System.currentTimeMillis()) {
				stop(true);
				return;
			}

			Location loc = getLocation();
			data = data.location(loc);

			if (!isTransparent(loc.getBlock())) {
				if (groundSpell != null) groundSpell.subcast(data.noTarget());
				if (stopOnHitGround) {
					stop(true);
					return;
				}
			}

			playSpellEffects(EffectPosition.SPECIAL, loc, data);

			if (effectSet != null) {
				Effect effect;
				Location effectLoc;
				for (EffectlibSpellEffect spellEffect : effectSet) {
					if (spellEffect == null) continue;
					effect = spellEffect.getEffect();
					if (effect == null) continue;

					effectLoc = spellEffect.getSpellEffect().applyOffsets(loc.clone(), data);
					effect.setLocation(effectLoc);

					if (effect instanceof ModifiedEffect) {
						Effect modifiedEffect = ((ModifiedEffect) effect).getInnerEffect();
						if (modifiedEffect != null) modifiedEffect.setLocation(effectLoc);
					}
				}
			}

			if (armorStandSet != null) {
				for (ArmorStand armorStand : armorStandSet) {
					armorStand.teleportAsync(loc);
				}
			}

			if (entityMap != null) {
				for (var entry : entityMap.entrySet()) {
					entry.getValue().teleportAsync(entry.getKey().applyOffsets(loc.clone()));
				}
			}

			if (orbitSpell != null) orbitSpell.subcast(data.noTarget());

			box.setCenter(loc);

			for (LivingEntity e : data.caster().getWorld().getLivingEntities()) {
				if (e.equals(data.caster())) continue;
				if (!e.isValid()) continue;
				if (immune.contains(e)) continue;
				if (!box.contains(e)) continue;
				if (entityTargetList != null && !entityTargetList.canTarget(e)) continue;

				SpellTargetEvent event = new SpellTargetEvent(OrbitSpell.this, data, e);
				if (!event.callEvent()) continue;

				SpellData subData = event.getSpellData();
				immune.add(event.getTarget());

				if (entitySpell != null) entitySpell.subcast(subData.noLocation());

				playSpellEffects(EffectPosition.TARGET, event.getTarget(), subData);
				playSpellEffectsTrail(loc, event.getTarget().getLocation(), subData);

				if (stopOnHitEntity) {
					stop(true);
					return;
				}
			}
		}

		private OrbitSpell getOrbitSpell() {
			return OrbitSpell.this;
		}

		private Location getLocation() {
			if (data.hasTarget()) {
				data.target().getLocation(currentLocation);

				if (followYaw) {
					float currentYaw = currentLocation.getYaw();

					if (previousYaw != currentYaw) {
						Util.rotateVector(currentDirection, currentYaw - previousYaw);
						previousYaw = currentYaw;
					}
				}
			}

			Vector perp;
			if (counterClockwise) perp = new Vector(currentDirection.getZ(), 0, -currentDirection.getX());
			else perp = new Vector(-currentDirection.getZ(), 0, currentDirection.getX());

			currentDirection.add(perp.multiply(distancePerTick)).normalize();

			return currentLocation.clone().add(0, yOffset, 0).add(currentDirection.clone().multiply(orbitRadius)).setDirection(perp);
		}


		private void stop(boolean removeTracker) {
			playSpellEffects(EffectPosition.DELAYED, getLocation(), data);

			MagicSpells.cancelTask(taskId);
			MagicSpells.cancelTask(repeatingHorizTaskId);
			MagicSpells.cancelTask(repeatingVertTaskId);

			if (effectSet != null) {
				for (EffectlibSpellEffect spellEffect : effectSet) {
					if (spellEffect == null) continue;
					if (spellEffect.getEffect() == null) continue;
					spellEffect.getEffect().cancel();
				}
				effectSet.clear();
			}

			if (armorStandSet != null) {
				for (ArmorStand armorStand : armorStandSet) {
					armorStand.remove();
				}
			}

			if (entityMap != null) {
				for (Entity entity : entityMap.values()) {
					entity.remove();
				}
			}

			if (removeTracker) trackerSet.remove(this);
		}

	}

}
