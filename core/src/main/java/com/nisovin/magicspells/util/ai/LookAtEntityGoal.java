package com.nisovin.magicspells.util.ai;

import org.bukkit.entity.Mob;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;

import org.jetbrains.annotations.NotNull;

import com.nisovin.magicspells.util.Util;
import com.nisovin.magicspells.MagicSpells;

import java.util.EnumSet;
import java.util.function.Predicate;
import java.util.concurrent.ThreadLocalRandom;

import com.destroystokyo.paper.entity.ai.Goal;
import com.destroystokyo.paper.entity.ai.GoalKey;
import com.destroystokyo.paper.entity.ai.GoalType;

public class LookAtEntityGoal implements Goal<Mob> {

	private final Mob mob;
	private final Class<? extends LivingEntity> targetType;
	private final float range;
	private final float chance;

	private Entity target;
	private int lookTime;

	public LookAtEntityGoal(Mob mob, Class<? extends LivingEntity> targetType, float range, float chance) {
		this.mob = mob;
		this.targetType = targetType;
		this.range = range;
		this.chance = chance;
	}

	@Override
	public boolean shouldActivate() {
		if (ThreadLocalRandom.current().nextFloat() >= chance) return false;
		if (mob.getTarget() != null) target = mob.getTarget();

		target = Util.getNearestEntity(mob, range, entity -> {
			boolean isHuman = targetType.isAssignableFrom(HumanEntity.class);
			if (isHuman) return rides(entity).and(targetType::isInstance).test(entity);
			else return targetType.isInstance(entity);
		});
		return target != null;
	}

	@Override
	public boolean shouldStayActive() {
		return target.isValid() &&
				lookTime > 0 &&
				mob.getLocation().distanceSquared(target.getLocation()) < (range * range);
	}

	@Override
	public void start() {
		lookTime = ThreadLocalRandom.current().nextInt(40, 80);
	}

	@Override
	public void stop() {
		target = null;
	}

	@Override
	public void tick() {
		mob.lookAt(target);
		lookTime--;
	}

	@NotNull
	@Override
	public GoalKey<Mob> getKey() {
		return GoalKey.of(Mob.class, new NamespacedKey(MagicSpells.getInstance(), "look_at_entity"));
	}

	@NotNull
	@Override
	public EnumSet<GoalType> getTypes() {
		return EnumSet.of(GoalType.LOOK);
	}

	private Predicate<Entity> rides(Entity vehicle) {
		return passenger -> {
			while (passenger.getVehicle() != null) {
				passenger = passenger.getVehicle();
				if (passenger == vehicle) {
					return false;
				}
			}
			return true;
		};
	}
}
